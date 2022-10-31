package ru.homecredit.jiraadapter.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import ru.homecredit.jiraadapter.dto.FieldOptions;
import ru.homecredit.jiraadapter.dto.response.FieldOptionsResponse;
import ru.homecredit.jiraadapter.service.FieldOptionsService;
import ru.homecredit.jiraadapter.service.FieldOptionsServiceImpl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * The core class for handling GET and POST requests to /options endpoint
 */
@Path("/options")
@Named
@Slf4j
public class FieldOptionsController {
    private final FieldOptionsService fieldOptionsService;
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * puts the received Jira beans to fields
     */
    @Inject
    public FieldOptionsController(FieldOptionsServiceImpl fieldOptionsService) {
        this.fieldOptionsService = fieldOptionsService;
    }

    /**
     * GET request is used to receive the list of options for customfield in
     * given context. Context is defined by project key and issue type id
     * the request url for GET request looks like:
     * http://{hostname}/jira/rest/cfoptchange/1.0/options?fieldKey={jira field key}&projKey={
     * jira project key}&issueTypeId={issue type id}
     * http://localhost:2990/jira/rest/cfoptchange/1.0/options?fieldKey=customfield_10000&projKey=test&issueTypeId=10000
     *
     * @param fieldKey - jira customfield key like - customfield_10000
     * @param projectKey - jira project key like TES
     * @param issueTypeId - jira issue type id like 10000
     * @return xml response in format, defined in FieldOptionToXML class
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFieldOptions(@QueryParam("fieldKey") String fieldKey,
                                    @QueryParam("projectKey") String projectKey,
                                    @QueryParam("issueTypeId") String issueTypeId) {
        log.trace("************* starting getFieldOptions method... ************");
        FieldOptions fieldOptions
                = fieldOptionsService.getOptions(fieldKey, projectKey, issueTypeId);
        String jsonResponse = gson.toJson(new FieldOptionsResponse(fieldOptions));
        log.info(jsonResponse);
        return Response.ok(jsonResponse).build();
    }

    /**
     * method for handling POST request to /options endpoint (adding and
     * enabling/disabling given field option)
     * @param requestBody - string in Json format with request parameters,
     *  same as for GET request with an extra parameter newOption (option value)
     * @return Response object
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response doPost(String requestBody) {
        log.trace("************ starting doPost method... **************");
        FieldOptions fieldOptions = fieldOptionsService.postOption(requestBody);
        log.info("fieldOptionsRequest was {}", fieldOptions.getFieldOptionsRequest());
        return (fieldOptions.getFieldOptionsRequest() == null)
                ? Response.ok("failed to parse request parameters. check the logs").build()
                :Response.ok(gson.toJson(new FieldOptionsResponse(fieldOptions))).build();
    }
}
