package ru.homecredit.jiraadapter.webwork;

import com.atlassian.jira.web.action.JiraWebActionSupport;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.homecredit.jiraadapter.service.JiraAdapterSettingsService;

import javax.inject.Inject;
import java.util.List;
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
    private List<String> customFieldsKeysList;
    private List<String> savedCustomFieldsKeysList;

    @Inject
    /**
     * constructor to acquire JiraSettingsService bean and receive current settings from jira
     */
    public ConfigurationWebworkAction(JiraAdapterSettingsService jiraAdapterSettingsService) {
        this.jiraAdapterSettingsService = jiraAdapterSettingsService;
        currentSettings = jiraAdapterSettingsService.getSettings().getCommaSeparatedFields();
        savedCustomFieldsKeysList = jiraAdapterSettingsService.getSettings().getEditableFields();
        customFieldsKeysList = jiraAdapterSettingsService.getAllSelectListFieldsKeys();
        log.info("customfields of select list type are {}", customFieldsKeysList);
        log.info("current settings are " + currentSettings);
    }

    @Override
    /**
     * method either saves settings received from template in customFieldsIds field,
     * or just returns the template, if no POST request was preformed
     */
    public String doExecute() {
        if (customFieldsIds != null && !currentSettings.equals(customFieldsIds)) {
            saveCustomfieldIds();
        } else {
            log.info("settings in form seems to be up to date");
        }
        return "configuration-page";
    }

    private void saveCustomfieldIds() {
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
