package ru.homecredit.jiraadapter.web;

import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import ru.homecredit.jiraadapter.service.JiraAdapterSettingsService;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * class to handle POST request to /settings endpoint - receiving and updating
 * the keys of customfield, which options will be permitted to handle by
 * requests to /options endpoint
 */
@Path("/settings")
@Named
@Slf4j
public class JiraAdapterSettingsController {
    private final JiraAdapterSettingsService jiraAdapterSettingsService;
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * constructor just acquires settingsService Jira bean
     * @param jiraAdapterSettingsService - jira bean to be injected by spring
     */
    @Inject
    public JiraAdapterSettingsController(JiraAdapterSettingsService jiraAdapterSettingsService) {
        log.info("starting FieldOptionsController instance construction");
        this.jiraAdapterSettingsService = jiraAdapterSettingsService;
    }

    @GET
    @AnonymousAllowed
    @Produces(MediaType.APPLICATION_JSON)
    /**
     * method for acquiring plugin settings trough get request
     * @return - Response object with list of editable fields currenly stored as plugin settings
     */
    public Response getSettings() {
        log.trace("************* starting getSettings method... ************");
        return Response.ok(gson.toJson(
                jiraAdapterSettingsService.getSettings())).build();
    }
}
