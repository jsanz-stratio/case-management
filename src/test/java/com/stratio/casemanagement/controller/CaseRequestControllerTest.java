package com.stratio.casemanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stratio.casemanagement.config.SwaggerConfiguration;
import com.stratio.casemanagement.model.mapper.CaseRequestControllerServiceMapper;
import com.stratio.casemanagement.model.mapper.CaseRequestControllerServiceMapperImpl;
import com.stratio.casemanagement.service.CaseRequestService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

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
    private CaseRequestControllerServiceMapper spyMapper = new CaseRequestControllerServiceMapperImpl();
    @InjectMocks
    private CaseRequestController classUnderTest;

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
        final com.stratio.casemanagement.model.controller.CaseRequest testCaseRequest = generateCaseRequestController();
        testCaseRequest.setCreationDate(null);
        testCaseRequest.setModificationDate(null);

        final com.stratio.casemanagement.model.service.CaseRequest resultCaseRequestFromService = generateCaseRequestService();
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
                .andExpect(jsonPath("entityId").value(resultCaseRequestFromService.getEntityId()));
    }

    // TODO: Test for when can't insert

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

        final com.stratio.casemanagement.model.service.CaseRequest resultCaseRequestFromService = generateCaseRequestService();
        when(mockService.getCaseRequestById(any(Long.class))).thenReturn(resultCaseRequestFromService);

        // When, then
        mockMvc.perform(
                get(URL_BASE + URL_CASE_REQUESTS_RESOURCE + URL_CASE_REQUEST_BY_ID_SUBRESOURCE, 42L)
                .accept(MediaType.APPLICATION_JSON_UTF8)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(resultCaseRequestFromService.getId()))
                .andExpect(jsonPath("creationDate").value(resultCaseRequestFromService.getCreationDate().toString()))
                .andExpect(jsonPath("creationUser").value(resultCaseRequestFromService.getCreationUser()))
                .andExpect(jsonPath("modificationDate").value(resultCaseRequestFromService.getModificationDate().toString()))
                .andExpect(jsonPath("modificationUser").value(resultCaseRequestFromService.getModificationUser()))
                .andExpect(jsonPath("entityId").value(resultCaseRequestFromService.getEntityId()));


        verify(mockService).getCaseRequestById(eq(testId));
    }

    private com.stratio.casemanagement.model.controller.CaseRequest generateCaseRequestController() {
        return podamFactory.manufacturePojo(com.stratio.casemanagement.model.controller.CaseRequest.class);
    }

    private com.stratio.casemanagement.model.service.CaseRequest generateCaseRequestService() {
        return podamFactory.manufacturePojo(com.stratio.casemanagement.model.service.CaseRequest.class);
    }
}