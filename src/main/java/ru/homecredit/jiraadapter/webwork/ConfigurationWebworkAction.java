package ru.homecredit.jiraadapter.webwork;

import com.atlassian.jira.web.action.JiraWebActionSupport;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.homecredit.jiraadapter.dto.JiraAdapterSettings;
import ru.homecredit.jiraadapter.service.JiraAdapterSettingsService;

import javax.inject.Inject;
import java.util.*;
import java.util.regex.Pattern;

@Slf4j
@Getter
@Setter
/**
 * webwork class to output configuration page template and exchange plugin settings string with it
 */
public class ConfigurationWebworkAction extends JiraWebActionSupport {
    private final JiraAdapterSettingsService jiraAdapterSettingsService;
    private String currentSettings;
    private String customFieldsIds = null;
    private String errorMessage;
    private List<String> allCustomFieldsKeysList;
    private List<String> savedCustomFieldsKeysList;
    private List<String> customFieldsKeysListToSave = new ArrayList<>();

    @Inject
    /**
     * constructor to acquire JiraSettingsService bean and receive current settings from jira
     */
    public ConfigurationWebworkAction(JiraAdapterSettingsService jiraAdapterSettingsService) {
        log.info("START ********************************");
        this.jiraAdapterSettingsService = jiraAdapterSettingsService;
        JiraAdapterSettings jiraAdapterSettings = jiraAdapterSettingsService.getSettings();
        currentSettings = jiraAdapterSettings.getCommaSeparatedFields();
        savedCustomFieldsKeysList = jiraAdapterSettings.getEditableFields();
        allCustomFieldsKeysList = jiraAdapterSettingsService.getAllSelectListFieldsKeys();
        log.info("allCustomFieldsKeysList is {}", allCustomFieldsKeysList);
        log.info("currentSettings are " + currentSettings);
        log.info("savedCustomFieldsKeysList are " + savedCustomFieldsKeysList);
        log.info("customFieldsKeysListToSave is " + customFieldsKeysListToSave);
        log.info("customFieldsIds " + customFieldsIds);
    }

    @Override
    /**
     * method either saves settings received from template in customFieldsIds field,
     * or just returns the template, if no POST request was preformed
     */
    public String doExecute() {
        log.info("doExecute()");
        log.info("customFieldsKeysListToSave is " + customFieldsKeysListToSave);
        log.info("customFieldsIds " + customFieldsIds);
        if (customFieldsIds != null && !currentSettings.equals(customFieldsIds)) {
            saveCustomfieldIds();
        } else {
            log.info("settings in form seems to be up to date");
        }
        return "configuration-page";
    }

    private void saveCustomfieldIds() {
        log.info("savedCustomFieldsKeysList are " + savedCustomFieldsKeysList);
        log.info("customFieldsKeysListToSave is " + customFieldsKeysListToSave);
        log.info("trying to save settings - {}", customFieldsIds);
        if(Pattern.matches("(?:[ ]*[,]?[ ]*customfield_[0-9]{5}[ ]*[,]?[ ]*)+"
                , customFieldsIds)) {
            log.info("input ok");
            jiraAdapterSettingsService.saveSettings(customFieldsIds);
            currentSettings = jiraAdapterSettingsService.getSettings().getCommaSeparatedFields();
        } else {
            log.error("constructing errorMessage");
            errorMessage = "is not the sequence of comma separated custom fields " +
                    "keys like \"customfield_10000, customfield_10100\"";
        }
    }
}
