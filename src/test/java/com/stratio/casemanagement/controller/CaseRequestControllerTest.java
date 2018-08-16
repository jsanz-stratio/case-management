package com.stratio.casemanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stratio.casemanagement.config.SwaggerConfiguration;
import com.stratio.casemanagement.model.controller.CaseRequestInput;
import com.stratio.casemanagement.model.mapper.CaseRequestControllerInboundMapper;
import com.stratio.casemanagement.model.mapper.CaseRequestControllerInboundMapperImpl;
import com.stratio.casemanagement.model.mapper.CaseRequestControllerOutboundMapper;
import com.stratio.casemanagement.model.mapper.CaseRequestControllerOutboundMapperImpl;
import com.stratio.casemanagement.model.service.CaseRequest;
import com.stratio.casemanagement.service.CaseRequestService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import static com.stratio.casemanagement.config.SwaggerConfiguration.API_PREFIX;
import static com.stratio.casemanagement.controller.CaseRequestController.API_BASE_PATH;
import static com.stratio.casemanagement.controller.CaseRequestController.API_VERSION;
import static org.hamcrest.CoreMatchers.is;
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

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(classUnderTest).build();
    }

    @Test
    public void whenCreateCaseRequestGivenValidInputThenReturn201CreatedAndMappedEntity() throws Exception {
        // Given
        final CaseRequestInput testCaseRequest = podamFactory.manufacturePojo(CaseRequestInput.class);

        final com.stratio.casemanagement.model.service.CaseRequest resultCaseRequestFromService = podamFactory.manufacturePojo(CaseRequest.class);
        when(mockService.insertCaseRequest(any(com.stratio.casemanagement.model.service.CaseRequest.class))).thenReturn(resultCaseRequestFromService);

        // When, then
        mockMvc.perform(
                post(URL_BASE + URL_CASE_REQUESTS_RESOURCE)
                        .content(jsonMapper.writeValueAsString(testCaseRequest))
                        .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(resultCaseRequestFromService.getId()))
                .andExpect(jsonPath("creationDate").value(resultCaseRequestFromService.getCreationDate().toString()))
                .andExpect(jsonPath("creationUser").value(resultCaseRequestFromService.getCreationUser()))
                .andExpect(jsonPath("modificationDate").value(resultCaseRequestFromService.getModificationDate().toString()))
                .andExpect(jsonPath("modificationUser").value(resultCaseRequestFromService.getModificationUser()))
                .andExpect(jsonPath("entityId").value(resultCaseRequestFromService.getEntityId()))
                .andExpect(MockMvcResultMatchers.header().string("location", generateExpectedLocationUri(resultCaseRequestFromService)));

        verify(mockService).insertCaseRequest(caseRequestCaptor.capture());
        CaseRequest caseRequestForServiceCall = caseRequestCaptor.getValue();
        verifyServiceCallForCreation(testCaseRequest, caseRequestForServiceCall);
    }

    @Test
    public void whenDeleteCaseRequestByIdGivenMoreThanOneRowAffectedThenReturn500() throws Exception {
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
    public void whenDeleteCaseRequestByIdGivenOneRowAffectedThenReturn200() throws Exception {
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
    public void whenDeleteCaseRequestByIdGivenZeroRowsAffectedThenReturn404() throws Exception {
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
    public void whenGetCaseRequestByIdGivenNullResultThenReturn404() throws Exception {
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
    public void whenGetCaseRequestByIdGivenValidResultThenReturn200AndMappedEntity() throws Exception {
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
                .andExpect(jsonPath("id").value(resultCaseRequestFromService.getId()))
                .andExpect(jsonPath("creationDate").value(resultCaseRequestFromService.getCreationDate().toString()))
                .andExpect(jsonPath("creationUser").value(resultCaseRequestFromService.getCreationUser()))
                .andExpect(jsonPath("modificationDate").value(resultCaseRequestFromService.getModificationDate().toString()))
                .andExpect(jsonPath("modificationUser").value(resultCaseRequestFromService.getModificationUser()))
                .andExpect(jsonPath("entityId").value(resultCaseRequestFromService.getEntityId()));


        verify(mockService).getCaseRequestById(eq(testId));
    }

    @Test
    public void whenUpdateCaseRequestByIdGivenMoreThanOneRowAffectedThenReturn500() throws Exception {
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
    public void whenUpdateCaseRequestByIdGivenOneRowAffectedThenReturn200() throws Exception {
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
    public void whenUpdateCaseRequestByIdGivenZeroRowsAffectedThenReturn404() throws Exception {
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
}