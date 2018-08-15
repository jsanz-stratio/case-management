package com.stratio.casemanagement.service;

import com.stratio.casemanagement.model.mapper.CaseRequestServiceRepositoryMapper;
import com.stratio.casemanagement.model.mapper.CaseRequestServiceRepositoryMapperImpl;
import com.stratio.casemanagement.model.service.CaseRequest;
import com.stratio.casemanagement.repository.CaseRequestRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

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
    private CaseRequestRepository mockRepository;
    @Spy
    private CaseRequestServiceRepositoryMapper spyMapper = new CaseRequestServiceRepositoryMapperImpl();
    @InjectMocks
    private CaseRequestServiceDefault classUnderTest;

    @Captor
    private ArgumentCaptor<com.stratio.casemanagement.model.repository.CaseRequest> repoCaseRequestCaptor;

    private PodamFactory podamFactory = new PodamFactoryImpl();

    @Test
    public void whenDeleteCaseRequestByIdGivenResultThenReturnAffectedRows() {
        // Given
        final Long testId = 44L;
        final int returnedAffectedRowsFromRepository = 232;

        when(mockRepository.deleteCaseRequestById(any(Long.class))).thenReturn(returnedAffectedRowsFromRepository);

        // When
        int result = classUnderTest.deleteCaseRequestById(testId);

        // Then
        assertThat(result, is(returnedAffectedRowsFromRepository));

        verify(mockRepository).deleteCaseRequestById(eq(testId));
    }

    @Test
    public void whenGetCaseRequestByIdGivenNullResultThenReturnNull() {
        // Given
        final Long testId = 66L;

        when(mockRepository.getCaseRequestById(any(Long.class))).thenReturn(null);

        // When
        CaseRequest resultCaseRequest = classUnderTest.getCaseRequestById(testId);

        // Then
        assertThat(resultCaseRequest, is(nullValue()));

        verify(mockRepository).getCaseRequestById(eq(testId));
    }

    @Test
    public void whenGetCaseRequestByIdGivenValidResultThenCheckResultMapping() {
        // Given
        final Long testId = 66L;
        final com.stratio.casemanagement.model.repository.CaseRequest resultCaseRequestFromRepo =
                podamFactory.manufacturePojo(com.stratio.casemanagement.model.repository.CaseRequest.class);
        when(mockRepository.getCaseRequestById(any(Long.class))).thenReturn(resultCaseRequestFromRepo);

        // When
        CaseRequest resultCaseRequest = classUnderTest.getCaseRequestById(testId);

        // Then
        assertThat(resultCaseRequest, is(notNullValue()));
        compareServiceAndRepositoryBeans(resultCaseRequestFromRepo, resultCaseRequest);

        verify(mockRepository).getCaseRequestById(eq(testId));
    }

    @Test
    public void whenInsertCaseRequestGivenValidInputThenCheckResultMappingHasNotNullDates() {
        // Given
        final CaseRequest testServiceCaseRequest = generateCaseRequestServiceWithNullDates();

        // When
        classUnderTest.insertCaseRequest(testServiceCaseRequest);

        // Then
        verify(mockRepository).insertCaseRequest(repoCaseRequestCaptor.capture());
        com.stratio.casemanagement.model.repository.CaseRequest sentCaseRequestToRepo = repoCaseRequestCaptor.getValue();
        assertThat(sentCaseRequestToRepo.getCreationDate(), is(notNullValue()));
        assertThat(sentCaseRequestToRepo.getModificationDate(), is(notNullValue()));
        assertThat(sentCaseRequestToRepo.getId(), is(testServiceCaseRequest.getId()));
        assertThat(sentCaseRequestToRepo.getEntityId(), is(testServiceCaseRequest.getEntityId()));
        assertThat(sentCaseRequestToRepo.getCreationUser(), is(testServiceCaseRequest.getCreationUser()));
        assertThat(sentCaseRequestToRepo.getModificationUser(), is(testServiceCaseRequest.getModificationUser()));

    }

    private void compareServiceAndRepositoryBeans(com.stratio.casemanagement.model.repository.CaseRequest repositoryBean, CaseRequest serviceBean) {
        assertThat(serviceBean.getId(), is(repositoryBean.getId()));
        assertThat(serviceBean.getEntityId(), is(repositoryBean.getEntityId()));
        assertThat(serviceBean.getCreationDate(), is(repositoryBean.getCreationDate()));
        assertThat(serviceBean.getModificationDate(), is(repositoryBean.getModificationDate()));
        assertThat(serviceBean.getCreationUser(), is(repositoryBean.getCreationUser()));
        assertThat(serviceBean.getModificationUser(), is(repositoryBean.getModificationUser()));
    }

    private CaseRequest generateCaseRequestServiceWithNullDates() {
        CaseRequest caseRequest = podamFactory.manufacturePojo(CaseRequest.class);
        caseRequest.setCreationDate(null);
        caseRequest.setModificationDate(null);
        return caseRequest;
    }
}