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

/**
 * Tests for CaseRequestServiceDefault. Currently get and create tests have their own test class
 */
@RunWith(MockitoJUnitRunner.class)
public class CaseRequestServiceDefaultTest {


    @Mock
    private CaseRequestRepository mockCaseRequestRepo;
    @Spy
    private CaseRequestServiceRepositoryMapper spyMapper = new CaseRequestServiceRepositoryMapperImpl();
    @InjectMocks
    private CaseRequestServiceDefault classUnderTest;

    @Captor
    private ArgumentCaptor<com.stratio.casemanagement.model.repository.CaseRequest> repoCaseRequestCaptor;

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

    private void verifySentCaseRequestAndDatesForUpdate(CaseRequest testServiceCaseRequest,
                                                        com.stratio.casemanagement.model.repository.CaseRequest sentCaseRequestToRepo) {
        assertThat(sentCaseRequestToRepo.getCreationDate(), is(nullValue()));
        assertThat(sentCaseRequestToRepo.getModificationDate(), is(notNullValue()));
        assertThat(sentCaseRequestToRepo.getId(), is(testServiceCaseRequest.getId()));
        assertThat(sentCaseRequestToRepo.getEntityId(), is(testServiceCaseRequest.getEntityId()));
        assertThat(sentCaseRequestToRepo.getCreationUser(), is(testServiceCaseRequest.getCreationUser()));
        assertThat(sentCaseRequestToRepo.getModificationUser(), is(testServiceCaseRequest.getModificationUser()));
    }
}