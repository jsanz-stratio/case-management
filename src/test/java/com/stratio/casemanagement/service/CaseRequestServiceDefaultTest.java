package com.stratio.casemanagement.service;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.time.LocalDateTime;

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

        final com.stratio.casemanagement.model.repository.CaseRequest resultCaseRequestFromRepo = generateCaseRequestRepository();
        when(mockRepository.getCaseRequestById(any(Long.class))).thenReturn(resultCaseRequestFromRepo);

        // When
        CaseRequest resultCaseRequest = classUnderTest.getCaseRequestById(testId);

        // Then
        assertThat(resultCaseRequest, is(notNullValue()));
        compareServiceAndRepositoryBeans(resultCaseRequestFromRepo, resultCaseRequest);

        verify(mockRepository).getCaseRequestById(eq(testId));
    }

    @Test
    public void whenInsertCaseRequestGivenValidInputThenCheckResultMapping() {
        // Given
        final CaseRequest testServiceCaseRequest = generateCaseRequestService();

        final com.stratio.casemanagement.model.repository.CaseRequest resultCaseRequestFromRepo = generateCaseRequestRepository();

        // When
        CaseRequest resultCaseRequest = classUnderTest.insertCaseRequest(testServiceCaseRequest);

        // Then
        verifyRepositoryCalled(testServiceCaseRequest);

    }

    // TODO: Test error cases!!

    private void compareServiceAndRepositoryBeans(com.stratio.casemanagement.model.repository.CaseRequest repositoryBean, CaseRequest serviceBean) {
        assertThat(serviceBean.getId(), is(repositoryBean.getId()));
        assertThat(serviceBean.getEntityId(), is(repositoryBean.getEntityId()));
        assertThat(serviceBean.getCreationDate(), is(repositoryBean.getCreationDate()));
        assertThat(serviceBean.getModificationDate(), is(repositoryBean.getModificationDate()));
        assertThat(serviceBean.getCreationUser(), is(repositoryBean.getCreationUser()));
        assertThat(serviceBean.getModificationUser(), is(repositoryBean.getModificationUser()));
    }

    private com.stratio.casemanagement.model.repository.CaseRequest generateCaseRequestRepository() {
        return podamFactory.manufacturePojo(com.stratio.casemanagement.model.repository.CaseRequest.class);
    }

    private CaseRequest generateCaseRequestService() {
        return podamFactory.manufacturePojo(CaseRequest.class);
    }

    private void verifyRepositoryCalled(CaseRequest originalServiceBean) {
        verify(mockRepository).insertCaseRequest(repoCaseRequestCaptor.capture());
        com.stratio.casemanagement.model.repository.CaseRequest sentCaseRequestToRepo = repoCaseRequestCaptor.getValue();
        compareServiceAndRepositoryBeans(sentCaseRequestToRepo, originalServiceBean);
    }
}