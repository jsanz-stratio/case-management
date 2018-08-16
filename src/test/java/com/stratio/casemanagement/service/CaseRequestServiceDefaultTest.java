package com.stratio.casemanagement.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stratio.casemanagement.model.mapper.CaseRequestServiceRepositoryMapper;
import com.stratio.casemanagement.model.mapper.CaseRequestServiceRepositoryMapperImpl;
import com.stratio.casemanagement.model.repository.CaseRawData;
import com.stratio.casemanagement.model.service.CaseRequest;
import com.stratio.casemanagement.repository.CaseRawDataRepository;
import com.stratio.casemanagement.repository.CaseRequestRepository;
import lombok.Data;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CaseRequestServiceDefaultTest {


    @Mock
    private CaseRequestRepository mockCaseRequestRepo;
    @Mock
    private CaseRawDataRepository mockRawDataRepo;
    @Spy
    private ObjectMapper jacksonObjectMapper = new ObjectMapper();
    @Spy
    private CaseRequestServiceRepositoryMapper spyMapper = new CaseRequestServiceRepositoryMapperImpl();
    @InjectMocks
    private CaseRequestServiceDefault classUnderTest;

    @Captor
    private ArgumentCaptor<com.stratio.casemanagement.model.repository.CaseRequest> repoCaseRequestCaptor;
    @Captor
    private ArgumentCaptor<CaseRawData> repoCaseRawDataCaptor;

    private PodamFactory podamFactory = new PodamFactoryImpl();

    @Test
    public void deleteCaseRequestById_MethodExecuted_ReturnAffectedRows() {
        // Given
        final Long testId = 44L;
        final int returnedAffectedRowsFromRepository = 232;

        when(mockCaseRequestRepo.deleteCaseRequestById(any(Long.class))).thenReturn(returnedAffectedRowsFromRepository);

        // When
        int result = classUnderTest.deleteCaseRequestById(testId);

        // Then
        assertThat(result, is(returnedAffectedRowsFromRepository));

        verify(mockCaseRequestRepo).deleteCaseRequestById(eq(testId));
    }

    @Test
    public void getCaseRequestById_RepositoryReturnsNull_ReturnNullItself() {
        // Given
        final Long testId = 66L;

        when(mockCaseRequestRepo.getCaseRequestById(any(Long.class))).thenReturn(null);

        // When
        CaseRequest resultCaseRequest = classUnderTest.getCaseRequestById(testId);

        // Then
        assertThat(resultCaseRequest, is(nullValue()));

        verify(mockCaseRequestRepo).getCaseRequestById(eq(testId));
    }

    @Test
    public void getCaseRequestById_RepositoryReturnsResult_ReturnMappedResult() {
        // Given
        final Long testId = 66L;
        final com.stratio.casemanagement.model.repository.CaseRequest resultCaseRequestFromRepo =
                podamFactory.manufacturePojo(com.stratio.casemanagement.model.repository.CaseRequest.class);
        when(mockCaseRequestRepo.getCaseRequestById(any(Long.class))).thenReturn(resultCaseRequestFromRepo);

        // When
        CaseRequest resultCaseRequest = classUnderTest.getCaseRequestById(testId);

        // Then
        assertThat(resultCaseRequest, is(notNullValue()));
        verifyServiceAndRepositoryBeansEqual(resultCaseRequestFromRepo, resultCaseRequest);

        verify(mockCaseRequestRepo).getCaseRequestById(eq(testId));
    }

    @Test
    public void insertCaseRequest_MethodExecuted_VerifyRepoCalledWithCorrectValues() throws Exception {
        // Given
        final CaseRequest testServiceCaseRequest = generateCaseRequestServiceWithNullDates();
        final String testCaseRawData = jacksonObjectMapper.writeValueAsString(podamFactory.manufacturePojo(TestObject.class)); // Valid json object
        testServiceCaseRequest.setCaseRawData(testCaseRawData);

        // When
        CaseRequest result = classUnderTest.insertCaseRequest(testServiceCaseRequest);

        // Then
        verifyInsertResult(testServiceCaseRequest, result);

        verify(mockCaseRequestRepo).insertCaseRequest(repoCaseRequestCaptor.capture());
        verify(mockRawDataRepo).insertCaseRawData(repoCaseRawDataCaptor.capture());

        com.stratio.casemanagement.model.repository.CaseRequest sentCaseRequestToRepo = repoCaseRequestCaptor.getValue();
        verifySentCaseRequestAndDatesForCreate(testServiceCaseRequest, sentCaseRequestToRepo);

        CaseRawData sentCaseRawDataToRepo = repoCaseRawDataCaptor.getValue();
        verifySentCaseRawData(testServiceCaseRequest.getId(), testCaseRawData, sentCaseRawDataToRepo);
    }

    @Test
    public void updateCaseRequestById_MethodExecuted_VerifyRepoCalledWithCorrectValuesAndAffectedRowsReturned() {
        // Given
        final Long testId = 77L;
        final CaseRequest testServiceCaseRequest = generateCaseRequestServiceWithNullDates();
        final int returnedAffectedRowsFromRepository = 66;
        when(mockCaseRequestRepo.updateCaseRequestById(any(Long.class), any(com.stratio.casemanagement.model.repository.CaseRequest.class)))
                .thenReturn(returnedAffectedRowsFromRepository);

        // When
        int result = classUnderTest.updateCaseRequestById(testId, testServiceCaseRequest);

        // Then
        assertThat(result, is(returnedAffectedRowsFromRepository));

        verify(mockCaseRequestRepo).updateCaseRequestById(eq(testId), repoCaseRequestCaptor.capture());
        com.stratio.casemanagement.model.repository.CaseRequest sentCaseRequestToRepo = repoCaseRequestCaptor.getValue();
        verifySentCaseRequestAndDatesForUpdate(testServiceCaseRequest, sentCaseRequestToRepo);
    }

    private CaseRequest generateCaseRequestServiceWithNullDates() {
        CaseRequest caseRequest = podamFactory.manufacturePojo(CaseRequest.class);
        caseRequest.setCreationDate(null);
        caseRequest.setModificationDate(null);
        return caseRequest;
    }

    private void verifyInsertResult(CaseRequest inputCaseRequest, CaseRequest outputCaseRequest) {
        // As repository entity is not modified (this is a test) all its values save operation dates are the same as in the input
        assertThat(outputCaseRequest.getId(), is(inputCaseRequest.getId()));
        assertThat(outputCaseRequest.getEntityId(), is(inputCaseRequest.getEntityId()));
        assertThat(outputCaseRequest.getCreationUser(), is(inputCaseRequest.getCreationUser()));
        assertThat(outputCaseRequest.getModificationUser(), is(inputCaseRequest.getModificationUser()));
        assertThat(outputCaseRequest.getCreationDate(), is(notNullValue()));
        assertThat(outputCaseRequest.getModificationDate(), is(notNullValue()));
        assertThat(outputCaseRequest.getCaseRawData(), is(inputCaseRequest.getCaseRawData()));
    }

    private void verifySentCaseRawData(Long createdIdForCaseRequest, String inputRawData, CaseRawData sentRawData) {
        // In this case the id is not generated in the database but gets mapped from service entity
        assertThat(sentRawData.getCaseId(), is(createdIdForCaseRequest));
        assertThat(sentRawData.getRaw(), is(inputRawData));
    }

    private void verifySentCaseRequestAndDatesForCreate(CaseRequest testServiceCaseRequest,
                                                        com.stratio.casemanagement.model.repository.CaseRequest sentCaseRequestToRepo) {
        assertThat(sentCaseRequestToRepo.getCreationDate(), is(notNullValue()));
        assertThat(sentCaseRequestToRepo.getModificationDate(), is(notNullValue()));
        assertThat(sentCaseRequestToRepo.getId(), is(testServiceCaseRequest.getId()));
        assertThat(sentCaseRequestToRepo.getEntityId(), is(testServiceCaseRequest.getEntityId()));
        assertThat(sentCaseRequestToRepo.getCreationUser(), is(testServiceCaseRequest.getCreationUser()));
        assertThat(sentCaseRequestToRepo.getModificationUser(), is(testServiceCaseRequest.getModificationUser()));
    }

    private void verifySentCaseRequestAndDatesForUpdate(CaseRequest testServiceCaseRequest,
                                                        com.stratio.casemanagement.model.repository.CaseRequest sentCaseRequestToRepo) {
        assertThat(sentCaseRequestToRepo.getCreationDate(), is(nullValue()));
        assertThat(sentCaseRequestToRepo.getModificationDate(), is(notNullValue()));
        assertThat(sentCaseRequestToRepo.getId(), is(testServiceCaseRequest.getId()));
        assertThat(sentCaseRequestToRepo.getEntityId(), is(testServiceCaseRequest.getEntityId()));
        assertThat(sentCaseRequestToRepo.getCreationUser(), is(testServiceCaseRequest.getCreationUser()));
        assertThat(sentCaseRequestToRepo.getModificationUser(), is(testServiceCaseRequest.getModificationUser()));
    }

    private void verifyServiceAndRepositoryBeansEqual(com.stratio.casemanagement.model.repository.CaseRequest repositoryBean, CaseRequest serviceBean) {
        assertThat(serviceBean.getId(), is(repositoryBean.getId()));
        assertThat(serviceBean.getEntityId(), is(repositoryBean.getEntityId()));
        assertThat(serviceBean.getCreationDate(), is(repositoryBean.getCreationDate()));
        assertThat(serviceBean.getModificationDate(), is(repositoryBean.getModificationDate()));
        assertThat(serviceBean.getCreationUser(), is(repositoryBean.getCreationUser()));
        assertThat(serviceBean.getModificationUser(), is(repositoryBean.getModificationUser()));
    }

    @Data
    private static class TestObject {
        private String a;
        private int b;
        private List<String> list;
        private Map<String, String> map;
    }
}