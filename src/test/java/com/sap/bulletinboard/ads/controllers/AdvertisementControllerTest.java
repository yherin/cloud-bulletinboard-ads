package com.sap.bulletinboard.ads.controllers;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.IOException;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.bulletinboard.ads.config.WebAppContextConfig;
import com.sap.bulletinboard.ads.models.Advertisement;
import org.springframework.web.util.UriComponents;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { WebAppContextConfig.class })
@WebAppConfiguration
//@formatter:off
public class AdvertisementControllerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdvertisementControllerTest.class);
    private static final String LOCATION = "Location";
    private static final String SOME_TITLE = "MyNewAdvertisement";

    @Inject
    WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        mockMvc.perform(buildDeleteRequest());
    }

    @Test
    public void create() throws Exception {
        mockMvc.perform(buildPostRequest(SOME_TITLE))
                .andExpect(status().isCreated())
                .andExpect(header().string(LOCATION, is(not(""))))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.title", is(SOME_TITLE))); // requires com.jayway.jsonpath:json-path
    }



    @Test
    public void readAll() throws Exception {
        mockMvc.perform(buildPostRequest("abc"));
        mockMvc.perform(buildPostRequest("efg"));
        mockMvc.perform(buildPostRequest("xyz"));
        MvcResult mvcResult = mockMvc.perform(buildGetRequest(null))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
//                .andExpect(content().json("[{\"title\":\"abc\"}]"))
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();

        assertTrue(content.contains("{\"title\":\"abc\"}") );
        assertTrue(content.contains("{\"title\":\"efg\"}") );
        assertTrue(content.contains("{\"title\":\"xyz\"}") );

        // TODO create new advertisement using POST, then retrieve all advertisements using GET
    }

    @Test
    public void readByIdNotFound() throws Exception {
        mockMvc.perform(buildGetRequest("1")).andExpect(status().isNotFound());
    }

    @Test
    public void readByIdInvalid() throws Exception {
        mockMvc.perform(buildGetRequest("-1")).andExpect(status().isBadRequest());
    }

    @Test
    public void readById() throws Exception {
        MvcResult postResult = mockMvc.perform(buildPostRequest(SOME_TITLE)).andExpect(status().isCreated()).andReturn();
        String loc =  postResult.getResponse().getHeader("Location");
        String id = loc.replace(AdvertisementController.PATH,"");
        MvcResult getResult = mockMvc.perform(buildGetRequest(id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andReturn();
        String content = getResult.getResponse().getContentAsString();
        LOGGER.info("Res: "+content);
        assertEquals("{\"title\":\""+ SOME_TITLE+"\"}", content);


    }

    private MockHttpServletRequestBuilder buildPostRequest(String adsTitle) throws Exception {
        Advertisement advertisement = new Advertisement();
        advertisement.setTitle(adsTitle);

        // post the advertisement as a JSON entity in the request body
        return post(AdvertisementController.PATH).content(toJson(advertisement)).contentType(APPLICATION_JSON_UTF8);
    }

    private MockHttpServletRequestBuilder buildGetRequest(String id) throws Exception {
        if (id == null) {
            return get(AdvertisementController.PATH);
        } else {
            return get(AdvertisementController.PATH + AdvertisementController.ID, id);
        }
    }

    private MockHttpServletRequestBuilder buildDeleteRequest() throws Exception {
        return delete(AdvertisementController.PATH + AdvertisementController.DELETE);
    }

    private String toJson(Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }

    private String getIdFromLocation(String location) {
        return location.substring(location.lastIndexOf('/') + 1);
    }

    private <T> T convertJsonContent(MockHttpServletResponse response, Class<T> clazz) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String contentString = response.getContentAsString();
        return objectMapper.readValue(contentString, clazz);
    }
}