package com.stratio.casemanagement.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stratio.casemanagement.model.mapper.CaseRequestServiceRepositoryMapper;
import com.stratio.casemanagement.model.mapper.CaseRequestServiceRepositoryMapperImpl;
import com.stratio.casemanagement.model.repository.CaseParticipant;
import com.stratio.casemanagement.model.repository.CaseRawData;
import com.stratio.casemanagement.model.service.CaseRequest;
import com.stratio.casemanagement.repository.CaseParticipantRepository;
import com.stratio.casemanagement.repository.CaseRawDataRepository;
import com.stratio.casemanagement.repository.CaseRequestRepository;
import lombok.Data;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.util.StringUtils;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CaseRequestServiceDefaultTest {


    @Mock
    private CaseRequestRepository mockCaseRequestRepo;
    @Mock
    private CaseRawDataRepository mockRawDataRepo;
    @Mock
    private CaseParticipantRepository mockParticipantRepo;
    @Spy
    private CaseRequestServiceRepositoryMapper spyMapper = new CaseRequestServiceRepositoryMapperImpl();
    @InjectMocks
    private CaseRequestServiceDefault classUnderTest;

    @Captor
    private ArgumentCaptor<com.stratio.casemanagement.model.repository.CaseRequest> repoCaseRequestCaptor;
    @Captor
    private ArgumentCaptor<CaseRawData> repoCaseRawDataCaptor;

    private PodamFactory podamFactory = new PodamFactoryImpl();
    private ObjectMapper jacksonObjectMapper = new ObjectMapper();

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
    public void getCaseRequestById_NoParticipants_ReturnNullParticipants() {
        // Given
        final Long testId = 66L;

        final com.stratio.casemanagement.model.repository.CaseRequest resultCaseRequestFromRepo =
                podamFactory.manufacturePojo(com.stratio.casemanagement.model.repository.CaseRequest.class);
        when(mockCaseRequestRepo.getCaseRequestById(any(Long.class))).thenReturn(resultCaseRequestFromRepo);

        final CaseRawData resultRawDataFromRepo = podamFactory.manufacturePojo(CaseRawData.class);
        when(mockRawDataRepo.getCaseRawDataById(any(Long.class))).thenReturn(resultRawDataFromRepo);

        when(mockParticipantRepo.getCaseParticipantById(any(Long.class))).thenReturn(null);

        // When
        CaseRequest resultCaseRequest = classUnderTest.getCaseRequestById(testId);

        // Then
        assertThat(resultCaseRequest, is(notNullValue()));
        assertThat(resultCaseRequest.getCaseParticipant(), is(nullValue()));
    }

    @Test
    public void getCaseRequestById_NoRawData_ReturnNullRawData() {
        // Given
        final Long testId = 66L;

        final com.stratio.casemanagement.model.repository.CaseRequest resultCaseRequestFromRepo =
                podamFactory.manufacturePojo(com.stratio.casemanagement.model.repository.CaseRequest.class);
        when(mockCaseRequestRepo.getCaseRequestById(any(Long.class))).thenReturn(resultCaseRequestFromRepo);

        when(mockRawDataRepo.getCaseRawDataById(any(Long.class))).thenReturn(null);

        final CaseParticipant resultParticipantFromRepo = podamFactory.manufacturePojo(CaseParticipant.class);
        when(mockParticipantRepo.getCaseParticipantById(any(Long.class))).thenReturn(resultParticipantFromRepo);

        // When
        CaseRequest resultCaseRequest = classUnderTest.getCaseRequestById(testId);

        // Then
        assertThat(resultCaseRequest, is(notNullValue()));
        assertThat(resultCaseRequest.getCaseRawData(), is(nullValue()));
    }

    @Test
    public void getCaseRequestById_RepositoriesReturnsNull_ReturnNull() {
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

        final CaseRawData resultRawDataFromRepo = podamFactory.manufacturePojo(CaseRawData.class);
        when(mockRawDataRepo.getCaseRawDataById(any(Long.class))).thenReturn(resultRawDataFromRepo);

        final CaseParticipant resultParticipantFromRepo = podamFactory.manufacturePojo(CaseParticipant.class);
        when(mockParticipantRepo.getCaseParticipantById(any(Long.class))).thenReturn(resultParticipantFromRepo);

        // When
        CaseRequest resultCaseRequest = classUnderTest.getCaseRequestById(testId);

        // Then
        assertThat(resultCaseRequest, is(notNullValue()));
        verifyExpectedResultFromRepositoryOutput(resultCaseRequestFromRepo, resultRawDataFromRepo, resultParticipantFromRepo, resultCaseRequest);

        verify(mockCaseRequestRepo).getCaseRequestById(eq(testId));
        verify(mockParticipantRepo).getCaseParticipantById(eq(testId));
    }

    @Test
    public void insertCaseRequest_EmptyNoParticipant_NoParticipantInsertOnDB() {
        testEmptyParticipant("");
    }

    @Test
    public void insertCaseRequest_EmptyNoRawData_NoRawDataInsertOnDB() {
        // Given
        testEmptyCaseRawData("");
    }

    @Test
    public void insertCaseRequest_EmptyNullParticipant_NoParticipantInsertOnDB() {
        testEmptyParticipant(null);
    }

    @Test
    public void insertCaseRequest_EmptyNullRawData_NoRawDataInsertOnDB() {
        testEmptyCaseRawData(null);
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

    private void testEmptyCaseRawData(String emptyRawDataValue) {
        assertTrue("This method only must be used with empty input!!", !StringUtils.hasText(emptyRawDataValue));
        // Given
        final CaseRequest testServiceCaseRequest = generateCaseRequestServiceWithNullDates();
        testServiceCaseRequest.setCaseRawData(emptyRawDataValue);

        // When
        CaseRequest result = classUnderTest.insertCaseRequest(testServiceCaseRequest);

        // Then
        verifyInsertResult(testServiceCaseRequest, result);

        verify(mockRawDataRepo, never()).insertCaseRawData(any(CaseRawData.class));
    }

    private void testEmptyParticipant(String emptyParticipantValue) {
        // Given
        final CaseRequest testServiceCaseRequest = generateCaseRequestServiceWithNullDates();
        testServiceCaseRequest.setCaseParticipant(emptyParticipantValue);

        // When
        CaseRequest result = classUnderTest.insertCaseRequest(testServiceCaseRequest);

        // Then
        verifyInsertResult(testServiceCaseRequest, result);

        verify(mockParticipantRepo, never()).insertCaseParticipant(any(CaseParticipant.class));
    }


    private void verifyExpectedResultFromRepositoryOutput(com.stratio.casemanagement.model.repository.CaseRequest requestFromRepo,
                                                          CaseRawData rawDataFromRepo, CaseParticipant participantFromRepo, CaseRequest serviceBean) {
        assertThat(serviceBean.getId(), is(requestFromRepo.getId()));
        assertThat(serviceBean.getEntityId(), is(requestFromRepo.getEntityId()));
        assertThat(serviceBean.getCreationDate(), is(requestFromRepo.getCreationDate()));
        assertThat(serviceBean.getModificationDate(), is(requestFromRepo.getModificationDate()));
        assertThat(serviceBean.getCreationUser(), is(requestFromRepo.getCreationUser()));
        assertThat(serviceBean.getModificationUser(), is(requestFromRepo.getModificationUser()));
        assertThat(serviceBean.getCaseRawData(), is(rawDataFromRepo.getRaw()));
        assertThat(serviceBean.getCaseParticipant(), is(participantFromRepo.getParticipantsData()));
    }

    private void verifyInsertResult(CaseRequest inputCaseRequest, CaseRequest outputCaseRequest) {
        // As repository entity is not modified (this is a test) all its values save operation dates are the same as in the input
        assertThat(outputCaseRequest.getId(), is(inputCaseRequest.getId()));
        assertThat(outputCaseRequest.getEntityId(), is(inputCaseRequest.getEntityId()));
        assertThat(outputCaseRequest.getCreationUser(), is(inputCaseRequest.getCreationUser()));
        assertThat(outputCaseRequest.getModificationUser(), is(inputCaseRequest.getModificationUser()));
        assertThat(outputCaseRequest.getCreationDate(), is(notNullValue()));
        assertThat(outputCaseRequest.getModificationDate(), is(notNullValue()));

        String inputCaseRawData = inputCaseRequest.getCaseRawData();
        assertThat(outputCaseRequest.getCaseRawData(), is(StringUtils.hasText(inputCaseRawData) ? inputCaseRawData : null));

        String inputCaseParticipant = inputCaseRequest.getCaseParticipant();
        assertThat(outputCaseRequest.getCaseParticipant(), is(StringUtils.hasText(inputCaseParticipant) ? inputCaseParticipant : null));
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

    @Data
    private static class TestObject {
        private String a;
        private int b;
        private List<String> list;
        private Map<String, String> map;
    }
}