package gr.planetz.controller;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gr.planetz.Schema;
import gr.planetz.cache.CacheService;

@ActiveProfiles("itest")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ITConfiguration.class)
@WebAppConfiguration
@DirtiesContext
public class ControllerIT {

    private final ObjectNode      requestBuilder  = JsonNodeFactory.instance.objectNode();

    private final ObjectNode      responseBuilder = JsonNodeFactory.instance.objectNode();

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private CacheService          cacheService;

    @Value("${player.cache.expiration.time.in.seconds}")
    private long                  durationInSeconds;

    @Value("${player.cache.cleanup.period.in.millis}")
    private long                  cleanUpPeriod;

    private MockMvc               mockMvc;

    @Before
    public void before() {
        this.mockMvc = webAppContextSetup(this.webApplicationContext).build();
        this.requestBuilder.removeAll();
        this.responseBuilder.removeAll();
    }

    @Test
    public void assertThatWellFormedRequestWillBeHandledCorrectly() throws Exception {
        // prepare
        final String toSendRequest = this.requestBuilder.put("nickname", "AlGreed").toString();

        // perform
        final MvcResult result = this.mockMvc.perform(post(Schema.V1_0.JOIN_PATH).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(toSendRequest))
                .andExpect(status().isAccepted()).andReturn();

        final String expectedResponse = "{\"Players\":{\"AlGreed\":\"127.0.0.1\"}}";
        assertEquals("The wrong response!", expectedResponse, result.getResponse().getContentAsString());
    }

    @Test
    public void assertThatNewResponseAddsNewPlayerToOneExisted() throws Exception {
        // prepare
        final String toSendRequest = this.requestBuilder.put("nickname", "AlGreed").toString();
        this.cacheService.putPlayer("DukeNukem", "123.34.52.12");

        // perform
        MvcResult result = this.mockMvc.perform(post(Schema.V1_0.JOIN_PATH).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(toSendRequest))
                .andExpect(status().isAccepted()).andReturn();

        final String expectedResponse1 = "{\"Players\":{\"AlGreed\":\"127.0.0.1\",\"DukeNukem\":\"123.34.52.12\"}}";
        assertEquals("The wrong response!", expectedResponse1, result.getResponse().getContentAsString());

        Thread.sleep(this.cleanUpPeriod);
        this.mockMvc.perform(post(Schema.V1_0.JOIN_PATH).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(toSendRequest)).andExpect(status().isAccepted())
                .andReturn();

        Thread.sleep(this.cleanUpPeriod);
        result = this.mockMvc.perform(post(Schema.V1_0.JOIN_PATH).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(toSendRequest)).andExpect(status().isAccepted())
                .andReturn();

        final String expectedResponse2 = "{\"Players\":{\"AlGreed\":\"127.0.0.1\"}}";
        assertEquals("The wrong response!", expectedResponse2, result.getResponse().getContentAsString());
    }

}