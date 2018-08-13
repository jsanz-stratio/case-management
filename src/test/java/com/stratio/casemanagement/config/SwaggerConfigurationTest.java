package com.stratio.casemanagement.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class SwaggerConfigurationTest {

    private SwaggerConfiguration classUnderTest = new SwaggerConfiguration();

    @Test
    public void testSwaggerConfiguration() {
        // When
        Docket resultDocket = classUnderTest.productApi();

        // Then
        assertThat(resultDocket, is(not(nullValue())));
        assertThat(resultDocket.getDocumentationType(), is(DocumentationType.SWAGGER_2));
        assertThat(resultDocket.getGroupName(), is("default"));
    }
}