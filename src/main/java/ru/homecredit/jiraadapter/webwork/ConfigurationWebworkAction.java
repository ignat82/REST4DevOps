package ru.homecredit.jiraadapter.webwork;

import com.atlassian.jira.web.action.JiraWebActionSupport;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.homecredit.jiraadapter.service.JiraAdapterSettingsService;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Getter
@Setter
/**
 * webwork class to output configuration page template and exchange plugin settings string with it
 */
public class ConfigurationWebworkAction extends JiraWebActionSupport {
    private final JiraAdapterSettingsService jiraAdapterSettingsService;
    private List<String> allCustomFieldsKeys;
    private List<String> savedCustomFieldsKeys;
    private String[] customFieldsKeysToSave;

    @Inject
    /**
     * constructor to acquire JiraSettingsService bean and receive current settings from jira
     */
    public ConfigurationWebworkAction(JiraAdapterSettingsService jiraAdapterSettingsService) {

        log.info("constructing");
        this.jiraAdapterSettingsService = jiraAdapterSettingsService;
    }

    @Override
    /**
     * method either saves settings received from template in customFieldsIds field,
     * or just returns the template, if no POST request was preformed
     */
    public String execute() throws Exception {
        log.info("execute()");
        log.info("super.execute()");
        super.execute();
        log.info("preparing fields info");
        allCustomFieldsKeys = jiraAdapterSettingsService.getAllSelectListFieldsKeys();
        savedCustomFieldsKeys = jiraAdapterSettingsService.getSettings().getEditableFields();
        log.info("returning template from execute()");
        return "configuration-page";
    }

    public void doSave() {
        log.info("doSave()");
        log.info("allCustomFieldsKeys - {}", allCustomFieldsKeys);
        log.info("saving {}", Arrays.toString(customFieldsKeysToSave));
        jiraAdapterSettingsService.saveCustomFieldsKeys(customFieldsKeysToSave);
        log.info("returning from doSave()");
    }

}
