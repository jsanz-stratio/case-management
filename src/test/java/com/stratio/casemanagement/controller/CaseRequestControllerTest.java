package com.stratio.casemanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stratio.casemanagement.config.SwaggerConfiguration;
import com.stratio.casemanagement.model.controller.CaseRequestRequest;
import com.stratio.casemanagement.model.mapper.CaseRequestRequestControllerToCaseRequestServiceMapper;
import com.stratio.casemanagement.model.mapper.CaseRequestRequestControllerToCaseRequestServiceMapperImpl;
import com.stratio.casemanagement.model.mapper.CaseRequestServiceToCaseRequestResponseControllerMapper;
import com.stratio.casemanagement.model.mapper.CaseRequestServiceToCaseRequestResponseControllerMapperImpl;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class CaseRequestControllerTest {

    private final static String URL_BASE = SwaggerConfiguration.API_PREFIX + CaseRequestController.API_VERSION;

    private final static String URL_CASE_REQUESTS_RESOURCE = CaseRequestController.API_BASE_PATH;
    private final static String URL_CASE_REQUEST_BY_ID_SUBRESOURCE = "/{id}";

    @Mock
    private CaseRequestService mockService;
    @Spy
    private CaseRequestRequestControllerToCaseRequestServiceMapper spyInMapper = new CaseRequestRequestControllerToCaseRequestServiceMapperImpl();
    @Spy
    private CaseRequestServiceToCaseRequestResponseControllerMapper spyOutMapper = new CaseRequestServiceToCaseRequestResponseControllerMapperImpl();
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
        final CaseRequestRequest testCaseRequest = podamFactory.manufacturePojo(CaseRequestRequest.class);

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
        verifyServiceCall(testCaseRequest, caseRequestForServiceCall);
    }

    @Test
    public void whenGetCaseRequestByIdGivenNullResultThenReturn404() throws Exception {
        // Given
        final Long testId = 42L;

        when(mockService.getCaseRequestById(any(Long.class))).thenReturn(null);

        // When, then
        mockMvc.perform(
                get(URL_BASE + URL_CASE_REQUESTS_RESOURCE + URL_CASE_REQUEST_BY_ID_SUBRESOURCE, 42L)
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
                get(URL_BASE + URL_CASE_REQUESTS_RESOURCE + URL_CASE_REQUEST_BY_ID_SUBRESOURCE, 42L)
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

    // TODO: Test for when can't insert

    private String generateExpectedLocationUri(CaseRequest resultCaseRequestFromService) {
        return API_PREFIX + API_VERSION + API_BASE_PATH + "/" + resultCaseRequestFromService.getId();
    }

    private void verifyServiceCall(CaseRequestRequest testCaseRequest, CaseRequest caseRequestForServiceCall) {
        assertThat(caseRequestForServiceCall.getId(), is(nullValue()));
        assertThat(caseRequestForServiceCall.getCreationDate(), is(nullValue()));
        assertThat(caseRequestForServiceCall.getModificationDate(), is(nullValue()));
        assertThat(caseRequestForServiceCall.getEntityId(), is(testCaseRequest.getEntityId()));
        assertThat(caseRequestForServiceCall.getCreationUser(), is(testCaseRequest.getCreationUser()));
        assertThat(caseRequestForServiceCall.getModificationUser(), is(testCaseRequest.getModificationUser()));
    }
}