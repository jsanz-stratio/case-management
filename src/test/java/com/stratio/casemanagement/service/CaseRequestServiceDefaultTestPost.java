package com.stratio.casemanagement.service;

import com.fasterxml.jackson.databind.ObjectMapper;
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

    private PodamFactory podamFactory = new PodamFactoryImpl();
    private ObjectMapper jacksonObjectMapper = new ObjectMapper();

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

        long notNullRawAttachments = testServiceCaseRequest.getCaseRawAttachments().stream().filter(Objects::nonNull).count();
        verify(mockRawAttachmentRepo, times((int) notNullRawAttachments)).insertCaseRawAttachment(repoCaseRawAttachmentCaptor.capture());
        List<CaseRawAttachment> sentCaseRawAttachmentsToRepo = repoCaseRawAttachmentCaptor.getAllValues();
        compareInsertedRawAttachmentListInRepoWithInput(testServiceCaseRequest.getCaseRawAttachments(), sentCaseRawAttachmentsToRepo,
                testServiceCaseRequest.getId());

        verify(mockRawDataRepo).insertCaseRawData(repoCaseRawDataCaptor.capture());
        CaseRawData sentCaseRawDataToRepo = repoCaseRawDataCaptor.getValue();
        verifySentCaseRawData(testServiceCaseRequest.getId(), testCaseRawData, sentCaseRawDataToRepo);
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
        return caseRequest;
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
