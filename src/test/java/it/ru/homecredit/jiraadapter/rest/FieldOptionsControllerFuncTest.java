package it.ru.homecredit.jiraadapter.rest;

import ru.homecredit.jiraadapter.dto.response.FieldOptionsResponse;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FieldOptionsControllerFuncTest {

    @Before
    public void setup() {

    }

    @After
    public void tearDown() {

    }

    @Test
    public void messageIsValid() {

        String baseUrl = System.getProperty("baseurl");
        String resourceUrl = baseUrl + "/rest/cfoptchange/1.0/message";

        RestClient client = new RestClient();
        Resource resource = client.resource(resourceUrl);

        FieldOptionsResponse message = resource.get(FieldOptionsResponse.class);

        assertEquals("wrong message", "customfield_10000", message.getFieldKey());
    }
}
