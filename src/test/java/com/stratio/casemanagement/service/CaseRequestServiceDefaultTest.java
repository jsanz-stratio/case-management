package com.stratio.casemanagement.service;

import com.stratio.casemanagement.model.mapper.CaseRequestServiceRepositoryMapper;
import com.stratio.casemanagement.model.mapper.CaseRequestServiceRepositoryMapperImpl;
import com.stratio.casemanagement.model.service.CaseRequest;
import com.stratio.casemanagement.repository.CaseRequestRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
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

    private void compareServiceAndRepositoryBeans(com.stratio.casemanagement.model.repository.CaseRequest resultCaseRequestFromRepo,
                                                  CaseRequest resultCaseRequest) {
        assertThat(resultCaseRequest.getId(), is(resultCaseRequestFromRepo.getId()));
        assertThat(resultCaseRequest.getEntityId(), is(resultCaseRequestFromRepo.getEntityId()));
        assertThat(resultCaseRequest.getCreationDate(), is(resultCaseRequestFromRepo.getCreationDate()));
        assertThat(resultCaseRequest.getModificationDate(), is(resultCaseRequestFromRepo.getModificationDate()));
        assertThat(resultCaseRequest.getCreationUser(), is(resultCaseRequestFromRepo.getCreationUser()));
        assertThat(resultCaseRequest.getModificationUser(), is(resultCaseRequestFromRepo.getModificationUser()));
    }

    private com.stratio.casemanagement.model.repository.CaseRequest generateCaseRequestRepository() {
        return podamFactory.manufacturePojo(com.stratio.casemanagement.model.repository.CaseRequest.class);
    }
}