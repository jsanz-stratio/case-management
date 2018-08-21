package com.stratio.casemanagement.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stratio.casemanagement.model.mapper.CaseRequestServiceRepositoryMapper;
import com.stratio.casemanagement.model.mapper.CaseRequestServiceRepositoryMapperImpl;
import com.stratio.casemanagement.model.repository.CaseParticipant;
import com.stratio.casemanagement.model.repository.CaseRawAttachment;
import com.stratio.casemanagement.model.repository.CaseRawData;
import com.stratio.casemanagement.model.service.CaseApplication;
import com.stratio.casemanagement.model.service.CaseRequest;
import com.stratio.casemanagement.repository.*;
import lombok.Data;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CaseRequestServiceDefaultTestPost {

    @Mock
    private CaseRequestRepository mockCaseRequestRepo;
    @Mock
    private CaseRawDataRepository mockRawDataRepo;
    @Mock
    private CaseRawAttachmentRepository mockRawAttachmentRepo;
    @Mock
    private CaseParticipantRepository mockParticipantRepo;
    @Mock
    private CaseApplicationRepository mockApplicationRepo;
    @Spy
    private CaseRequestServiceRepositoryMapper spyMapper = new CaseRequestServiceRepositoryMapperImpl();
    @InjectMocks
    private CaseRequestServiceDefault classUnderTest;

    @Captor
    private ArgumentCaptor<com.stratio.casemanagement.model.repository.CaseRequest> repoCaseRequestCaptor;
    @Captor
    private ArgumentCaptor<CaseRawData> repoCaseRawDataCaptor;
    @Captor
    private ArgumentCaptor<CaseRawAttachment> repoCaseRawAttachmentCaptor;
    @Captor
    private ArgumentCaptor<com.stratio.casemanagement.model.repository.CaseApplication> repoCaseApplicationCaptor;

    private PodamFactory podamFactory = new PodamFactoryImpl();
    private ObjectMapper jacksonObjectMapper = new ObjectMapper();

    @Test
    public void insertCaseRequest_EmptyNoApplications_NoApplicationInsertOnDB() {
        testEmptyCaseApplications(new ArrayList<>());
    }

    @Test
    public void insertCaseRequest_EmptyNoParticipant_NoParticipantInsertOnDB() {
        testEmptyParticipant("");
    }

    @Test
    public void insertCaseRequest_EmptyNoRawAttachments_NoRawAttachmentsOnDB() {
        testEmptyCaseRawAttachments(new ArrayList<>());
    }

    @Test
    public void insertCaseRequest_EmptyNoRawData_NoRawDataInsertOnDB() {
        testEmptyCaseRawData("");
    }

    @Test
    public void insertCaseRequest_EmptyNullApplications_NoApplicationInsertOnDB() {
        testEmptyCaseApplications(null);
    }

    @Test
    public void insertCaseRequest_EmptyNullParticipant_NoParticipantInsertOnDB() {
        testEmptyParticipant(null);
    }

    @Test
    public void insertCaseRequest_EmptyNullRawAttachments_NoRawAttachmentsOnDB() {
        testEmptyCaseRawAttachments(null);
    }

    @Test
    public void insertCaseRequest_EmptyNullRawData_NoRawDataInsertOnDB() {
        testEmptyCaseRawData(null);
    }

    @Test
    public void insertCaseRequest_MethodExecuted_VerifyRepoCalledWithCorrectValues() throws Exception {
        // Given
        final CaseRequest testServiceCaseRequest = generateCaseRequestServiceWithNullDates();
        final String testCaseRawData =
                jacksonObjectMapper.writeValueAsString(podamFactory.manufacturePojo(TestObject.class)); // Valid json object
        testServiceCaseRequest.setCaseRawData(testCaseRawData);

        // When
        CaseRequest result = classUnderTest.insertCaseRequest(testServiceCaseRequest);

        // Then
        verifyInsertResult(testServiceCaseRequest, result);

        verify(mockCaseRequestRepo).insertCaseRequest(repoCaseRequestCaptor.capture());
        com.stratio.casemanagement.model.repository.CaseRequest sentCaseRequestToRepo = repoCaseRequestCaptor.getValue();
        verifySentCaseRequestAndDatesForCreate(testServiceCaseRequest, sentCaseRequestToRepo);

        Long caseRequestGeneratedId = testServiceCaseRequest.getId();

        long notNullRawAttachments = testServiceCaseRequest.getCaseRawAttachments().stream().filter(Objects::nonNull).count();
        verify(mockRawAttachmentRepo, times((int) notNullRawAttachments)).insertCaseRawAttachment(repoCaseRawAttachmentCaptor.capture());
        List<CaseRawAttachment> sentCaseRawAttachmentsToRepo = repoCaseRawAttachmentCaptor.getAllValues();
        compareInsertedRawAttachmentListInRepoWithInput(testServiceCaseRequest.getCaseRawAttachments(), sentCaseRawAttachmentsToRepo,
                caseRequestGeneratedId);

        verify(mockRawDataRepo).insertCaseRawData(repoCaseRawDataCaptor.capture());
        CaseRawData sentCaseRawDataToRepo = repoCaseRawDataCaptor.getValue();
        verifySentCaseRawData(caseRequestGeneratedId, testCaseRawData, sentCaseRawDataToRepo);

        long notNullApplications = testServiceCaseRequest.getCaseApplications().stream().filter(Objects::nonNull).count();
        verify(mockApplicationRepo, times((int) notNullApplications)).insertCaseApplication(repoCaseApplicationCaptor.capture());
        List<com.stratio.casemanagement.model.repository.CaseApplication> sentCaseApplicationsToRepo = repoCaseApplicationCaptor.getAllValues();
        compareInsertedApplicationListInRepoWithInput(testServiceCaseRequest.getCaseApplications(), sentCaseApplicationsToRepo, caseRequestGeneratedId);
    }

    private void compareInsertedApplicationInRepoWithInput(CaseApplication fromService, com.stratio.casemanagement.model.repository.CaseApplication fromRepo,
                                                           Long generatedCaseRequestId) {
        assertThat(fromRepo.getCaseId(), is(generatedCaseRequestId));
        assertThat(fromRepo.getAppSeq(), is(fromService.getAppSeq()));
        assertThat(fromRepo.getProcessId(), is(fromService.getProcessId()));
        assertThat(fromRepo.getStatus(), is(fromService.getStatus()));
        assertThat(fromRepo.getLockedBy(), is(fromService.getLockedBy()));
        assertThat(fromRepo.getCreationDate(), is(notNullValue()));
        assertThat(fromRepo.getCreationUser(), is(fromService.getCreationUser()));
        assertThat(fromRepo.getModificationDate(), is(notNullValue()));
        assertThat(fromRepo.getModificationUser(), is(fromService.getModificationUser()));
    }

    private void compareInsertedApplicationListInRepoWithInput(List<CaseApplication> serviceList,
                                                               List<com.stratio.casemanagement.model.repository.CaseApplication> repoList,
                                                               Long generatedCaseRequestId) {
        if (!Stream.of(serviceList, repoList).allMatch(Objects::isNull)) {
            if (Stream.of(serviceList, repoList).anyMatch(Objects::isNull)) {
                fail("Raw attachment lists are not equal");
            } else if (serviceList.size() != repoList.size()) {
                fail("Raw attachment lists are not equal");
            } else {
                IntStream.range(0, serviceList.size())
                        .forEach(i -> compareInsertedApplicationInRepoWithInput(serviceList.get(i), repoList.get(i), generatedCaseRequestId));
            }
        }
    }

    private void compareInsertedRawAttachmentInRepoWithInput(com.stratio.casemanagement.model.service.CaseRawAttachment fromService,
                                                             CaseRawAttachment fromRepo, Long generatedCaseRequestId) {
        assertThat(fromRepo.getCaseId(), is(generatedCaseRequestId));
        assertThat(fromRepo.getSeqId(), is(fromService.getSeqId()));
        assertThat(fromRepo.getData(), is(fromService.getData()));
        assertThat(fromRepo.getMetadata(), is(fromService.getMetadata()));
    }

    private void compareInsertedRawAttachmentListInRepoWithInput(List<com.stratio.casemanagement.model.service.CaseRawAttachment> serviceList,
                                                                 List<CaseRawAttachment> repoList, Long generatedCaseRequestId) {

        if (!Stream.of(serviceList, repoList).allMatch(Objects::isNull)) {
            if (Stream.of(serviceList, repoList).anyMatch(Objects::isNull)) {
                fail("Raw attachment lists are not equal");
            } else if (serviceList.size() != repoList.size()) {
                fail("Raw attachment lists are not equal");
            } else {
                IntStream.range(0, serviceList.size())
                        .forEach(i -> compareInsertedRawAttachmentInRepoWithInput(serviceList.get(i), repoList.get(i), generatedCaseRequestId));
            }
        }
    }

    private void compareOutputApplicationsWithInput(CaseRequest inputCaseRequest, CaseRequest outputCaseRequest, List<CaseApplication> resultCaseApplications) {
        IntStream.range(0, resultCaseApplications.size()).forEach(i -> {
            CaseApplication resultCaseApplication = resultCaseApplications.get(i);
            CaseApplication inputCaseApplication = inputCaseRequest.getCaseApplications().get(i);

            assertThat(resultCaseApplication.getCaseId(), is(outputCaseRequest.getId()));
            assertThat(resultCaseApplication.getAppSeq(), is(inputCaseApplication.getAppSeq()));
            assertThat(resultCaseApplication.getProcessId(), is(inputCaseApplication.getProcessId()));
            assertThat(resultCaseApplication.getStatus(), is(inputCaseApplication.getStatus()));
            assertThat(resultCaseApplication.getLockedBy(), is(inputCaseApplication.getLockedBy()));
            assertThat(resultCaseApplication.getCreationDate(), is(notNullValue()));
            assertThat(resultCaseApplication.getCreationUser(), is(inputCaseApplication.getCreationUser()));
            assertThat(resultCaseApplication.getModificationDate(), is(notNullValue()));
            assertThat(resultCaseApplication.getModificationUser(), is(inputCaseApplication.getModificationUser()));
        });
    }

    private void compareOutputRawAttachmentsWithInput(CaseRequest inputCaseRequest, CaseRequest outputCaseRequest,
                                                      List<com.stratio.casemanagement.model.service.CaseRawAttachment> resultCaseRawAttachments) {
        IntStream.range(0, resultCaseRawAttachments.size()).forEach(i -> {
            com.stratio.casemanagement.model.service.CaseRawAttachment resultCaseRawAttachment = resultCaseRawAttachments.get(i);
            com.stratio.casemanagement.model.service.CaseRawAttachment inputCaseRawAttachment = inputCaseRequest.getCaseRawAttachments().get(i);
            assertThat(resultCaseRawAttachment.getCaseId(), is(outputCaseRequest.getId()));
            assertThat(resultCaseRawAttachment.getSeqId(), is(inputCaseRawAttachment.getSeqId()));
            assertThat(resultCaseRawAttachment.getData(), is(inputCaseRawAttachment.getData()));
            assertThat(resultCaseRawAttachment.getMetadata(), is(inputCaseRawAttachment.getMetadata()));
        });
    }

    private CaseRequest generateCaseRequestServiceWithNullDates() {
        CaseRequest caseRequest = podamFactory.manufacturePojo(CaseRequest.class);
        caseRequest.setCreationDate(null);
        caseRequest.setModificationDate(null);

        caseRequest.getCaseApplications().forEach(caseApplication -> {
            caseApplication.setCreationDate(null);
            caseApplication.setModificationDate(null);
        });

        return caseRequest;
    }

    private void testEmptyCaseApplications(List<CaseApplication> emptyApplicationList) {
        assertTrue("This method only must be used with empty input!!", CollectionUtils.isEmpty(emptyApplicationList));

        // Given
        final CaseRequest testServiceCaseRequest = generateCaseRequestServiceWithNullDates();
        testServiceCaseRequest.setCaseApplications(emptyApplicationList);

        // When
        CaseRequest result = classUnderTest.insertCaseRequest(testServiceCaseRequest);

        // Then
        verifyInsertResult(testServiceCaseRequest, result);
        verify(mockApplicationRepo, never()).insertCaseApplication(any(com.stratio.casemanagement.model.repository.CaseApplication.class));
    }

    private void testEmptyCaseRawAttachments(List<com.stratio.casemanagement.model.service.CaseRawAttachment> emptyRawAttachmentList) {
        assertTrue("This method only must be used with empty input!!", CollectionUtils.isEmpty(emptyRawAttachmentList));

        // Given
        final CaseRequest testServiceCaseRequest = generateCaseRequestServiceWithNullDates();
        testServiceCaseRequest.setCaseRawAttachments(emptyRawAttachmentList);

        // When
        CaseRequest result = classUnderTest.insertCaseRequest(testServiceCaseRequest);

        // Then
        verifyInsertResult(testServiceCaseRequest, result);

        verify(mockRawAttachmentRepo, never()).insertCaseRawAttachment(any(CaseRawAttachment.class));
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
        assertTrue("This method only must be used with empty input!!", !StringUtils.hasText(emptyParticipantValue));

        // Given
        final CaseRequest testServiceCaseRequest = generateCaseRequestServiceWithNullDates();
        testServiceCaseRequest.setCaseParticipant(emptyParticipantValue);

        // When
        CaseRequest result = classUnderTest.insertCaseRequest(testServiceCaseRequest);

        // Then
        verifyInsertResult(testServiceCaseRequest, result);

        verify(mockParticipantRepo, never()).insertCaseParticipant(any(CaseParticipant.class));
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

        List<com.stratio.casemanagement.model.service.CaseRawAttachment> inputCaseRawAttachmentList = inputCaseRequest.getCaseRawAttachments();
        List<com.stratio.casemanagement.model.service.CaseRawAttachment> resultCaseRawAttachments = outputCaseRequest.getCaseRawAttachments();
        if (CollectionUtils.isEmpty(inputCaseRawAttachmentList)) {
            assertThat(resultCaseRawAttachments, is(nullValue()));
        } else {
            compareOutputRawAttachmentsWithInput(inputCaseRequest, outputCaseRequest, resultCaseRawAttachments);
        }

        String inputCaseParticipant = inputCaseRequest.getCaseParticipant();
        assertThat(outputCaseRequest.getCaseParticipant(), is(StringUtils.hasText(inputCaseParticipant) ? inputCaseParticipant : null));

        List<CaseApplication> inputCaseApplicationList = inputCaseRequest.getCaseApplications();
        List<CaseApplication> outputCaseApplicationList = outputCaseRequest.getCaseApplications();
        if (CollectionUtils.isEmpty(inputCaseApplicationList)) {
            assertThat(outputCaseApplicationList, is(nullValue()));
        } else {
            compareOutputApplicationsWithInput(inputCaseRequest, outputCaseRequest, outputCaseApplicationList);
        }
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

    @Data
    private static class TestObject {
        private String a;
        private int b;
        private List<String> list;
        private Map<String, String> map;
    }
}
