package ru.homecredit.jiraadapter.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import ru.homecredit.jiraadapter.dto.response.FieldOptionsResponse;

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
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Inject
    public FieldOptionsController(OptionsServiceAdapter optionsServiceAdapter) {
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
        FieldOptionsResponse fieldOptionsResponse = optionsServiceAdapter.getRequest(fieldKey, projectKey, issueTypeId);
        Response.ResponseBuilder responseBuilder = (fieldOptionsResponse.isSuccess())
                ? Response.ok()
                : Response.status(400);
        responseBuilder.entity(gson.toJson(fieldOptionsResponse));
        return responseBuilder.build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/post_options/{fieldKey}/{projectKey}/{issueTypeId}")
    public Response doPost(@PathParam("fieldKey") String fieldKey,
                           @PathParam("projectKey") String projectKey,
                           @PathParam("issueTypeId") String issueTypeId,
                           String requestBody) {
        log.trace("************ starting doPost method... **************");
        log.error("request body received is {}", requestBody);
        FieldOptionsResponse fieldOptionsResponse = optionsServiceAdapter.postRequest(fieldKey,
                                                                                     projectKey,
                                                                                     issueTypeId,
                                                                                     requestBody);
        Response.ResponseBuilder responseBuilder = (fieldOptionsResponse.isSuccess())
                ? Response.ok()
                : Response.status(400);
        responseBuilder.entity(gson.toJson(fieldOptionsResponse));
        return responseBuilder.build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/post_option_ids/{optionId}")
    public Response handleOption(@PathParam("optionId") String optionId,
                                 String requestBody) {
        log.trace("************ starting doPost method... **************");
        log.error("request body received is {}", requestBody);

        FieldOptionsResponse fieldOptionsResponse = optionsServiceAdapter.postRequest(optionId,
                                                             requestBody);
        Response.ResponseBuilder responseBuilder = (fieldOptionsResponse.isSuccess())
                ? Response.ok()
                : Response.status(400);
        responseBuilder.entity(gson.toJson(fieldOptionsResponse));
        return responseBuilder.build();
    }
}
