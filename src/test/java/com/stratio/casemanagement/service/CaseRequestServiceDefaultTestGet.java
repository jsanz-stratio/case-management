package com.stratio.casemanagement.service;

import com.stratio.casemanagement.model.mapper.CaseRequestServiceRepositoryMapper;
import com.stratio.casemanagement.model.mapper.CaseRequestServiceRepositoryMapperImpl;
import com.stratio.casemanagement.model.repository.CaseParticipant;
import com.stratio.casemanagement.model.repository.CaseRawAttachment;
import com.stratio.casemanagement.model.repository.CaseRawData;
import com.stratio.casemanagement.model.service.CaseRequest;
import com.stratio.casemanagement.repository.CaseParticipantRepository;
import com.stratio.casemanagement.repository.CaseRawAttachmentRepository;
import com.stratio.casemanagement.repository.CaseRawDataRepository;
import com.stratio.casemanagement.repository.CaseRequestRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CaseRequestServiceDefaultTestGet {

    @Mock
    private CaseRequestRepository mockCaseRequestRepo;
    @Mock
    private CaseRawDataRepository mockRawDataRepo;
    @Mock
    private CaseRawAttachmentRepository mockRawAttachmentRepo;
    @Mock
    private CaseParticipantRepository mockParticipantRepo;
    @Spy
    private CaseRequestServiceRepositoryMapper spyMapper = new CaseRequestServiceRepositoryMapperImpl();
    @InjectMocks
    private CaseRequestServiceDefault classUnderTest;

    private PodamFactory podamFactory = new PodamFactoryImpl();

    @Test
    @SuppressWarnings("unchecked")
    public void getCaseRequestById_NoParticipants_ReturnNullParticipants() {
        // Given
        final Long testId = 66L;

        mockRepositoriesWith(
                podamFactory.manufacturePojo(com.stratio.casemanagement.model.repository.CaseRequest.class),
                podamFactory.manufacturePojo(CaseRawData.class),
                null,
                podamFactory.manufacturePojo(List.class, CaseRawAttachment.class)

        );

        // When
        CaseRequest resultCaseRequest = classUnderTest.getCaseRequestById(testId);

        // Then
        assertThat(resultCaseRequest, is(notNullValue()));
        assertThat(resultCaseRequest.getCaseParticipant(), is(nullValue()));
    }

    @Test
    public void getCaseRequestById_NoRawAttachmentsEmpty_ReturnEmptyListRawAttachments() {
        // Given
        final Long testId = 66L;

        mockRepositoriesWith(
                podamFactory.manufacturePojo(com.stratio.casemanagement.model.repository.CaseRequest.class),
                podamFactory.manufacturePojo(CaseRawData.class),
                podamFactory.manufacturePojo(CaseParticipant.class),
                new ArrayList<>()
        );

        // When
        CaseRequest resultCaseRequest = classUnderTest.getCaseRequestById(testId);

        // Then
        assertThat(resultCaseRequest, is(notNullValue()));
        assertThat(resultCaseRequest.getCaseRawAttachments(), is(empty()));
    }

    @Test
    public void getCaseRequestById_NoRawAttachments_ReturnNullRawAttachments() {
        // Given
        final Long testId = 66L;

        mockRepositoriesWith(
                podamFactory.manufacturePojo(com.stratio.casemanagement.model.repository.CaseRequest.class),
                podamFactory.manufacturePojo(CaseRawData.class),
                podamFactory.manufacturePojo(CaseParticipant.class),
                null
        );

        // When
        CaseRequest resultCaseRequest = classUnderTest.getCaseRequestById(testId);

        // Then
        assertThat(resultCaseRequest, is(notNullValue()));
        assertThat(resultCaseRequest.getCaseRawAttachments(), is(nullValue()));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void getCaseRequestById_NoRawData_ReturnNullRawData() {
        // Given
        final Long testId = 66L;

        mockRepositoriesWith(
                podamFactory.manufacturePojo(com.stratio.casemanagement.model.repository.CaseRequest.class),
                null,
                podamFactory.manufacturePojo(CaseParticipant.class),
                podamFactory.manufacturePojo(List.class, CaseRawAttachment.class)

        );

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

        @SuppressWarnings("unchecked") final List<CaseRawAttachment> resultRawAttachmentListFromRepo =
                podamFactory.manufacturePojo(List.class, CaseRawAttachment.class);
        when(mockRawAttachmentRepo.getCaseRawAttachmentListByCaseId(any(Long.class))).thenReturn(resultRawAttachmentListFromRepo);

        // When
        CaseRequest resultCaseRequest = classUnderTest.getCaseRequestById(testId);

        // Then
        assertThat(resultCaseRequest, is(notNullValue()));
        compareServiceResultAndRepositoryOutputs(resultCaseRequestFromRepo, resultRawDataFromRepo, resultRawAttachmentListFromRepo, resultParticipantFromRepo,
                resultCaseRequest);

        verify(mockCaseRequestRepo).getCaseRequestById(eq(testId));
        verify(mockParticipantRepo).getCaseParticipantById(eq(testId));
        verify(mockRawAttachmentRepo).getCaseRawAttachmentListByCaseId(eq(testId));
    }

    private void compareReturnedRawAttachmentListWithRepoOutput(List<com.stratio.casemanagement.model.service.CaseRawAttachment> serviceList,
                                                                List<CaseRawAttachment> repoList) {

        if (!Stream.of(serviceList, repoList).allMatch(Objects::isNull)) {
            if (Stream.of(serviceList, repoList).anyMatch(Objects::isNull)) {
                fail("Raw attachment lists are not equal");
            } else if (serviceList.size() != repoList.size()) {
                fail("Raw attachment lists are not equal");
            } else {
                IntStream.range(0, serviceList.size())
                        .forEach(i -> compareReturnedRawAttachmentWithRepoOutput(serviceList.get(i), repoList.get(i)));
            }
        }
    }

    private void compareReturnedRawAttachmentWithRepoOutput(com.stratio.casemanagement.model.service.CaseRawAttachment fromService,
                                                            CaseRawAttachment fromRepo) {
        assertThat(fromService.getCaseId(), is(fromRepo.getCaseId()));
        assertThat(fromService.getSeqId(), is(fromRepo.getSeqId()));
        assertThat(fromService.getData(), is(fromRepo.getData()));
        assertThat(fromService.getMetadata(), is(fromRepo.getMetadata()));
    }

    private void compareServiceResultAndRepositoryOutputs(com.stratio.casemanagement.model.repository.CaseRequest requestFromRepo,
                                                          CaseRawData rawDataFromRepo, List<CaseRawAttachment> rawAttachmentListFromRepo,
                                                          CaseParticipant participantFromRepo, CaseRequest resultFromService) {
        assertThat(resultFromService.getId(), is(requestFromRepo.getId()));
        assertThat(resultFromService.getEntityId(), is(requestFromRepo.getEntityId()));
        assertThat(resultFromService.getCreationDate(), is(requestFromRepo.getCreationDate()));
        assertThat(resultFromService.getModificationDate(), is(requestFromRepo.getModificationDate()));
        assertThat(resultFromService.getCreationUser(), is(requestFromRepo.getCreationUser()));
        assertThat(resultFromService.getModificationUser(), is(requestFromRepo.getModificationUser()));

        assertThat(resultFromService.getCaseRawData(), is(rawDataFromRepo.getRaw()));

        assertThat(resultFromService.getCaseParticipant(), is(participantFromRepo.getParticipantsData()));

        compareReturnedRawAttachmentListWithRepoOutput(resultFromService.getCaseRawAttachments(), rawAttachmentListFromRepo);
    }

    private void mockRepositoriesWith(com.stratio.casemanagement.model.repository.CaseRequest resultForCaseRequest, CaseRawData resultForRawData,
                                      CaseParticipant resultForParticipant, List<CaseRawAttachment> resultForRawAttachmentList) {
        when(mockCaseRequestRepo.getCaseRequestById(any(Long.class))).thenReturn(resultForCaseRequest);
        when(mockRawDataRepo.getCaseRawDataById(any(Long.class))).thenReturn(resultForRawData);
        when(mockParticipantRepo.getCaseParticipantById(any(Long.class))).thenReturn(resultForParticipant);
        when(mockRawAttachmentRepo.getCaseRawAttachmentListByCaseId(any(Long.class))).thenReturn(resultForRawAttachmentList);
    }
}
