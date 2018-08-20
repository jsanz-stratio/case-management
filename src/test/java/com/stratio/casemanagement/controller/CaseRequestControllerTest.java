package com.stratio.casemanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stratio.casemanagement.config.SwaggerConfiguration;
import com.stratio.casemanagement.model.controller.CaseRequestInput;
import com.stratio.casemanagement.model.mapper.CaseRequestControllerInboundMapper;
import com.stratio.casemanagement.model.mapper.CaseRequestControllerInboundMapperImpl;
import com.stratio.casemanagement.model.mapper.CaseRequestControllerOutboundMapper;
import com.stratio.casemanagement.model.mapper.CaseRequestControllerOutboundMapperImpl;
import com.stratio.casemanagement.model.service.CaseRawAttachment;
import com.stratio.casemanagement.model.service.CaseRequest;
import com.stratio.casemanagement.service.CaseRequestService;
import lombok.Data;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.util.List;
import java.util.Map;

import static com.stratio.casemanagement.config.SwaggerConfiguration.API_PREFIX;
import static com.stratio.casemanagement.controller.CaseRequestController.API_BASE_PATH;
import static com.stratio.casemanagement.controller.CaseRequestController.API_VERSION;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class CaseRequestControllerTest {

    private final static String URL_BASE = SwaggerConfiguration.API_PREFIX + CaseRequestController.API_VERSION;

    private final static String URL_CASE_REQUESTS_RESOURCE = CaseRequestController.API_BASE_PATH;
    private final static String URL_CASE_REQUEST_BY_ID_SUBRESOURCE = "/{id}";

    @Mock
    private CaseRequestService mockService;
    @Spy
    private CaseRequestControllerInboundMapper spyInMapper = new CaseRequestControllerInboundMapperImpl();
    @Spy
    private CaseRequestControllerOutboundMapper spyOutMapper = new CaseRequestControllerOutboundMapperImpl();
    @InjectMocks
    private CaseRequestController classUnderTest;

    @Captor
    private ArgumentCaptor<CaseRequest> caseRequestCaptor;

    private PodamFactory podamFactory = new PodamFactoryImpl();
    private MockMvc mockMvc;
    private ObjectMapper jsonMapper = new ObjectMapper();

    @Test
    public void createCaseRequest_ValidInput_Return201CreatedAndMappedEntity() throws Exception {
        // Given
        final CaseRequestInput testCaseRequest = podamFactory.manufacturePojo(CaseRequestInput.class);

        final com.stratio.casemanagement.model.service.CaseRequest returnedCaseRequestFromService = podamFactory.manufacturePojo(CaseRequest.class);
        // Add valid json object to raw data
        returnedCaseRequestFromService.setCaseRawData(jsonMapper.writeValueAsString(podamFactory.manufacturePojo(TestObject.class)));
        when(mockService.insertCaseRequest(any(com.stratio.casemanagement.model.service.CaseRequest.class))).thenReturn(returnedCaseRequestFromService);

        // When, then
        mockMvc.perform(
                post(URL_BASE + URL_CASE_REQUESTS_RESOURCE)
                        .content(jsonMapper.writeValueAsString(testCaseRequest))
                        .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(returnedCaseRequestFromService.getId())))
                .andExpect(jsonPath("$.creationDate", is(returnedCaseRequestFromService.getCreationDate().toString())))
                .andExpect(jsonPath("$.creationUser", is(returnedCaseRequestFromService.getCreationUser())))
                .andExpect(jsonPath("$.modificationDate", is(returnedCaseRequestFromService.getModificationDate().toString())))
                .andExpect(jsonPath("$.modificationUser", is(returnedCaseRequestFromService.getModificationUser())))
                .andExpect(jsonPath("$.entityId", is(returnedCaseRequestFromService.getEntityId())))
                .andExpect(jsonPath("$.caseRawData", is(returnedCaseRequestFromService.getCaseRawData())))
                .andExpect(jsonPath("$.caseRawAttachments", hasSize(returnedCaseRequestFromService.getCaseRawAttachments().size())))
                .andExpect(jsonPath("$.caseRawAttachments[*].caseId", hasAllCaseIdItems(returnedCaseRequestFromService)))
                .andExpect(jsonPath("$.caseRawAttachments[*].seqId", hasAllSeqIdItems(returnedCaseRequestFromService)))
                .andExpect(jsonPath("$.caseRawAttachments[*].data", hasAllDataItems(returnedCaseRequestFromService)))
                .andExpect(jsonPath("$.caseRawAttachments[*].metadata", hasAllMetadataItems(returnedCaseRequestFromService)))
                .andExpect(jsonPath("$.caseParticipant", is(returnedCaseRequestFromService.getCaseParticipant())))
                .andExpect(header().string("location", generateExpectedLocationUri(returnedCaseRequestFromService)));

        verify(mockService).insertCaseRequest(caseRequestCaptor.capture());
        CaseRequest caseRequestForServiceCall = caseRequestCaptor.getValue();
        verifyServiceCallForCreation(testCaseRequest, caseRequestForServiceCall);
    }

    @Test
    public void deleteCaseRequestById_MoreThanOneRowAffected_Return500() throws Exception {
        // Given
        final Long testId = 42L;
        when(mockService.deleteCaseRequestById(any(Long.class))).thenReturn(2);

        // When, then
        mockMvc.perform(
                delete(URL_BASE + URL_CASE_REQUESTS_RESOURCE + URL_CASE_REQUEST_BY_ID_SUBRESOURCE, testId)
        )
                .andExpect(status().isInternalServerError());

        verify(mockService).deleteCaseRequestById(eq(testId));
    }

    @Test
    public void deleteCaseRequestById_OneRowAffected_Return200() throws Exception {
        // Given
        final Long testId = 42L;
        when(mockService.deleteCaseRequestById(any(Long.class))).thenReturn(1);

        // When, then
        mockMvc.perform(
                delete(URL_BASE + URL_CASE_REQUESTS_RESOURCE + URL_CASE_REQUEST_BY_ID_SUBRESOURCE, testId)
        )
                .andExpect(status().isOk());

        verify(mockService).deleteCaseRequestById(eq(testId));
    }

    @Test
    public void deleteCaseRequestById_ZeroRowsAffected_Return404() throws Exception {
        // Given
        final Long testId = 42L;
        when(mockService.deleteCaseRequestById(any(Long.class))).thenReturn(0);

        // When, then
        mockMvc.perform(
                delete(URL_BASE + URL_CASE_REQUESTS_RESOURCE + URL_CASE_REQUEST_BY_ID_SUBRESOURCE, testId)
        )
                .andExpect(status().isNotFound());

        verify(mockService).deleteCaseRequestById(eq(testId));
    }

    @Test
    public void getCaseRequestById_NullResult_Return404() throws Exception {
        // Given
        final Long testId = 42L;

        when(mockService.getCaseRequestById(any(Long.class))).thenReturn(null);

        // When, then
        mockMvc.perform(
                get(URL_BASE + URL_CASE_REQUESTS_RESOURCE + URL_CASE_REQUEST_BY_ID_SUBRESOURCE, testId)
                        .accept(MediaType.APPLICATION_JSON_UTF8)
        )
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));

        verify(mockService).getCaseRequestById(eq(testId));
    }

    @Test
    public void getCaseRequestById_ValidResult_Return200AndMappedEntity() throws Exception {
        // Given
        final Long testId = 42L;

        final com.stratio.casemanagement.model.service.CaseRequest resultCaseRequestFromService = podamFactory.manufacturePojo(CaseRequest.class);
        when(mockService.getCaseRequestById(any(Long.class))).thenReturn(resultCaseRequestFromService);

        // When, then
        mockMvc.perform(
                get(URL_BASE + URL_CASE_REQUESTS_RESOURCE + URL_CASE_REQUEST_BY_ID_SUBRESOURCE, testId)
                        .accept(MediaType.APPLICATION_JSON_UTF8)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(resultCaseRequestFromService.getId())))
                .andExpect(jsonPath("$.creationDate", is(resultCaseRequestFromService.getCreationDate().toString())))
                .andExpect(jsonPath("$.creationUser", is(resultCaseRequestFromService.getCreationUser())))
                .andExpect(jsonPath("$.modificationDate", is(resultCaseRequestFromService.getModificationDate().toString())))
                .andExpect(jsonPath("$.modificationUser", is(resultCaseRequestFromService.getModificationUser())))
                .andExpect(jsonPath("$.entityId", is(resultCaseRequestFromService.getEntityId())))
                .andExpect(jsonPath("$.caseRawData", is(resultCaseRequestFromService.getCaseRawData())))
                .andExpect(jsonPath("$.caseRawAttachments", hasSize(resultCaseRequestFromService.getCaseRawAttachments().size())))
                .andExpect(jsonPath("$.caseRawAttachments[*].caseId", hasAllCaseIdItems(resultCaseRequestFromService)))
                .andExpect(jsonPath("$.caseRawAttachments[*].seqId", hasAllSeqIdItems(resultCaseRequestFromService)))
                .andExpect(jsonPath("$.caseRawAttachments[*].data", hasAllDataItems(resultCaseRequestFromService)))
                .andExpect(jsonPath("$.caseRawAttachments[*].metadata", hasAllMetadataItems(resultCaseRequestFromService)))
                .andExpect(jsonPath("$.caseParticipant", is(resultCaseRequestFromService.getCaseParticipant())));


        verify(mockService).getCaseRequestById(eq(testId));
    }

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(classUnderTest).build();
    }

    @Test
    public void updateCaseRequestById_MoreThanOneRowAffected_Return500() throws Exception {
        // Given
        final Long testId = 42L;
        final CaseRequestInput testCaseRequestInput = podamFactory.manufacturePojo(CaseRequestInput.class);
        when(mockService.updateCaseRequestById(any(Long.class), any(CaseRequest.class))).thenReturn(2);

        // When, then
        mockMvc.perform(
                put(URL_BASE + URL_CASE_REQUESTS_RESOURCE + URL_CASE_REQUEST_BY_ID_SUBRESOURCE, testId)
                        .content(jsonMapper.writeValueAsString(testCaseRequestInput))
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
        )
                .andExpect(status().isInternalServerError());

        verify(mockService).updateCaseRequestById(eq(testId), caseRequestCaptor.capture());
        CaseRequest caseRequestForServiceCall = caseRequestCaptor.getValue();
        verifyServiceCallForUpdate(testCaseRequestInput, caseRequestForServiceCall);
    }

    @Test
    public void updateCaseRequestById_OneRowAffected_Return200() throws Exception {
        // Given
        final Long testId = 42L;
        final CaseRequestInput testCaseRequestInput = podamFactory.manufacturePojo(CaseRequestInput.class);
        when(mockService.updateCaseRequestById(any(Long.class), any(CaseRequest.class))).thenReturn(1);

        // When, then
        mockMvc.perform(
                put(URL_BASE + URL_CASE_REQUESTS_RESOURCE + URL_CASE_REQUEST_BY_ID_SUBRESOURCE, testId)
                        .content(jsonMapper.writeValueAsString(testCaseRequestInput))
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
        )
                .andExpect(status().isOk());

        verify(mockService).updateCaseRequestById(eq(testId), caseRequestCaptor.capture());
        CaseRequest caseRequestForServiceCall = caseRequestCaptor.getValue();
        verifyServiceCallForUpdate(testCaseRequestInput, caseRequestForServiceCall);
    }

    @Test
    public void updateCaseRequestById_ZeroRowsAffected_Return404() throws Exception {
        // Given
        final Long testId = 42L;
        final CaseRequestInput testCaseRequestInput = podamFactory.manufacturePojo(CaseRequestInput.class);
        when(mockService.updateCaseRequestById(any(Long.class), any(CaseRequest.class))).thenReturn(0);

        // When, then
        mockMvc.perform(
                put(URL_BASE + URL_CASE_REQUESTS_RESOURCE + URL_CASE_REQUEST_BY_ID_SUBRESOURCE, testId)
                        .content(jsonMapper.writeValueAsString(testCaseRequestInput))
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
        )
                .andExpect(status().isNotFound());

        verify(mockService).updateCaseRequestById(eq(testId), caseRequestCaptor.capture());
        CaseRequest caseRequestForServiceCall = caseRequestCaptor.getValue();
        verifyServiceCallForUpdate(testCaseRequestInput, caseRequestForServiceCall);
    }

    private String generateExpectedLocationUri(CaseRequest resultCaseRequestFromService) {
        return API_PREFIX + API_VERSION + API_BASE_PATH + "/" + resultCaseRequestFromService.getId();
    }

    private Matcher<Iterable<? extends Long>> hasAllCaseIdItems(CaseRequest resultCaseRequestFromService) {
        return containsInAnyOrder(resultCaseRequestFromService.getCaseRawAttachments().stream().map(CaseRawAttachment::getCaseId).toArray(Long[]::new));
    }

    private Matcher<Iterable<? extends String>> hasAllDataItems(CaseRequest resultCaseRequestFromService) {
        return containsInAnyOrder(resultCaseRequestFromService.getCaseRawAttachments().stream().map(CaseRawAttachment::getData).toArray(String[]::new));
    }

    private Matcher<Iterable<? extends String>> hasAllMetadataItems(CaseRequest resultCaseRequestFromService) {
        return containsInAnyOrder(resultCaseRequestFromService.getCaseRawAttachments().stream().map(CaseRawAttachment::getMetadata).toArray(String[]::new));
    }

    private Matcher<Iterable<? extends Long>> hasAllSeqIdItems(CaseRequest resultCaseRequestFromService) {
        return containsInAnyOrder(resultCaseRequestFromService.getCaseRawAttachments().stream().map(CaseRawAttachment::getSeqId).toArray(Long[]::new));
    }

    private void verifyServiceCallCommonParameters(CaseRequestInput testCaseRequest, CaseRequest caseRequestForServiceCall) {
        assertThat(caseRequestForServiceCall.getId(), is(nullValue()));
        assertThat(caseRequestForServiceCall.getCreationDate(), is(nullValue()));
        assertThat(caseRequestForServiceCall.getModificationDate(), is(nullValue()));
        assertThat(caseRequestForServiceCall.getEntityId(), is(testCaseRequest.getEntityId()));
    }

    private void verifyServiceCallForCreation(CaseRequestInput testCaseRequest, CaseRequest caseRequestForServiceCall) {
        verifyServiceCallCommonParameters(testCaseRequest, caseRequestForServiceCall);
        assertThat(caseRequestForServiceCall.getCreationUser(), is(testCaseRequest.getOperationUser()));
        assertThat(caseRequestForServiceCall.getModificationUser(), is(testCaseRequest.getOperationUser()));
    }

    private void verifyServiceCallForUpdate(CaseRequestInput testCaseRequest, CaseRequest caseRequestForServiceCall) {
        verifyServiceCallCommonParameters(testCaseRequest, caseRequestForServiceCall);
        assertThat(caseRequestForServiceCall.getCreationUser(), is(nullValue()));
        assertThat(caseRequestForServiceCall.getModificationUser(), is(testCaseRequest.getOperationUser()));
    }

    @Data
    private static class TestObject {
        private String a;
        private int b;
        private List<String> list;
        private Map<String, String> map;
    }

}