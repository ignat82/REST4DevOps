package ru.homecredit.jiraadapter.web;

import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/options")
@Named
@Slf4j
public class FieldOptionsController {
    private final OptionsServiceAdapter optionsServiceAdapter;

    @Inject
    public FieldOptionsController(OptionsServiceAdapterImpl optionsServiceAdapter) {
        this.optionsServiceAdapter = optionsServiceAdapter;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFieldOptions(@QueryParam("fieldKey") String fieldKey,
                                    @QueryParam("projectKey") String projectKey,
                                    @QueryParam("issueTypeId") String issueTypeId) {
        log.trace("************* starting getFieldOptionsList method... ************");
        log.error("request parameters received are {}, {}, {}", fieldKey, projectKey, issueTypeId);
        return Response.ok(optionsServiceAdapter.getRequest(fieldKey, projectKey, issueTypeId)).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response doPost(String requestBody) {
        log.trace("************ starting doPost method... **************");
        log.error("request body received is {}", requestBody);
        return Response.ok(optionsServiceAdapter.postRequest(requestBody)).build();
    }
}
