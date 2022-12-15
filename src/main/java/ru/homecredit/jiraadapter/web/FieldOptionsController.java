package ru.homecredit.jiraadapter.web;

import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
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
    @Path("/get_options/{fieldKey}/{projectKey}/{issueTypeId}")
    public Response getFieldOptions(@PathParam("fieldKey") String fieldKey,
                                    @PathParam("projectKey") String projectKey,
                                    @PathParam("issueTypeId") String issueTypeId) {
        log.trace("************* starting getFieldOptionsList method... ************");
        log.error("request parameters received are {}, {}, {}", fieldKey, projectKey, issueTypeId);
        return Response.ok(optionsServiceAdapter.getRequest(fieldKey, projectKey, issueTypeId)).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("post_options/{fieldKey}/{projectKey}/{issueTypeId}")
    public Response doPost(@PathParam("fieldKey") String fieldKey,
                           @PathParam("projectKey") String projectKey,
                           @PathParam("issueTypeId") String issueTypeId,
                           String requestBody) {
        log.trace("************ starting doPost method... **************");
        log.error("request body received is {}", requestBody);
        return Response.ok(optionsServiceAdapter.postRequest(fieldKey,
                                                             projectKey,
                                                             issueTypeId,
                                                             requestBody)).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("post_option_ids/{optionId}")
    public Response handleOption(@PathParam("optionId") String optionId,
                                 String requestBody) {
        log.trace("************ starting doPost method... **************");
        log.error("request body received is {}", requestBody);
        return Response.ok(optionsServiceAdapter.postRequest(optionId,
                                                             requestBody)).build();
    }
}
