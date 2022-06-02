package HCBplugins.rest;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.customfields.manager.OptionsManager;
import com.atlassian.jira.issue.fields.FieldManager;
import com.atlassian.jira.issue.fields.config.manager.FieldConfigSchemeManager;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Objects;


/******************************************************************************
 * The core class that does following:
 *  when constructed:
 *  - initialises LoggerUtilsObject ang gets logger from it,
 *  - acquires jira object managers,
 *  afterwards, every time when receives get request:
 *      - invokes constructor of MutableOptionsList object
 *      - invokes that object addNewOption method,
 *      - provides the object to constructor of XML response
 *      - returns the response.
 * the full path to API endpoint(?) looks like:
 * http://{hostname}/jira/rest/cfoptchange/1.0/options?field_key={jira field key}&proj_key={jira project key}&new_opt={new option}
 * http://localhost:2990/jira/rest/cfoptchange/1.0/options&?field_key=customfield_10000&proj_key=TES&new_opt=new3
 *****************************************************************************/
@Path("/options")
public class OptionsChangeController {

    private final Logger                    logger;
    private final FieldManager              fieldManager;
    private final ProjectManager            projectManager;
    private final FieldConfigSchemeManager  fieldConfigSchemeManager;
    private final OptionsManager            optionsManger;


    /**************************************************************************
     * constructor initialises logger and receives Jira objects managers trough
     * component accessor
     *************************************************************************/
    public OptionsChangeController() {
        logger = LoggerFactory.getLogger(OptionsChangeController.class);
        logger.info("starting OptionsChangeController instance construction");
        try {
            fieldManager = Objects.requireNonNull(ComponentAccessor.getFieldManager()
                    , "failed to acquire fieldManager trough ComponentAccessor");
            projectManager = Objects.requireNonNull(ComponentAccessor.getProjectManager()
                    , "failed to acquire projectManager trough ComponentAccessor");
            fieldConfigSchemeManager = Objects.requireNonNull(ComponentAccessor.getFieldConfigSchemeManager()
                    , "failed to acquire fieldConfigSchemeManager trough ComponentAccessor");
            optionsManger = Objects.requireNonNull(ComponentAccessor.getOptionsManager()
                    , "failed to acquire optionsManger trough ComponentAccessor");
            logger.info("all managers acquired successfully. instance constructed");
        } catch (Exception exception) {
            logger.error("caught exception {}", exception.getMessage());
            throw exception;
        }
    }


    /**************************************************************************
     * the core method which receives the GET parameters,
     * creates new instance of MutableOptionsList nested class, invokes the
     * .addNewOption() method of it and constructs the response with
     * PackingResponseToXML class constructor
     * @param field_key the <em>key</em> of the <em>customfield</em> from GET parameter
     * @param proj_key the <em>key</em> of the <em>project</em> from GET parameter
     * @param new_opt  string - the <em>new option</em> from GET parameter
     * @return response in XML format
     *************************************************************************/
    @GET
    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getResponse(@QueryParam("field_key") String field_key,
                                @QueryParam("proj_key") String proj_key,
                                @QueryParam("new_opt") String new_opt) {
        logger.info("starting getResponse method...");
        MutableOptionsList mutableOptionsList
                = new MutableOptionsList(field_key, proj_key, new_opt);
        mutableOptionsList.addNewOption(  getFieldManager(),
                                    getProjectManager(),
                                    getFieldConfigSchemeManager(),
                                    getOptionsManger());
        Response response = Response.ok(
                new PackingResponseToXML(mutableOptionsList)).build();
        logger.info("constructed response, returning...");
        // is it necessary to reset logger for closing the log file properly?
        logger.info("closing logger's " + logger.getName() + " handlers");
        return response;
    }

    public FieldManager getFieldManager() {
        return fieldManager;
    }

    public ProjectManager getProjectManager() {
        return projectManager;
    }

    public FieldConfigSchemeManager getFieldConfigSchemeManager() {
        return fieldConfigSchemeManager;
    }

    public OptionsManager getOptionsManger() {
        return optionsManger;
    }
}