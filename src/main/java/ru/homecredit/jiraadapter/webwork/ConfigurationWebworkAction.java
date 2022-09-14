package ru.homecredit.jiraadapter.webwork;

import com.atlassian.jira.web.action.JiraWebActionSupport;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.homecredit.jiraadapter.service.JiraAdapterSettingsService;

import javax.inject.Inject;
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

    @Inject
    /**
     * constructor to acquire JiraSettingsService bean and receive current settings from jira
     */
    public ConfigurationWebworkAction(JiraAdapterSettingsService jiraAdapterSettingsService) {
        log.info("starting ConfigurationWebworkAction instance construction");
        this.jiraAdapterSettingsService = jiraAdapterSettingsService;
        currentSettings = jiraAdapterSettingsService.getSettings().getCommaSeparatedFields();
        log.info("current settings are " + currentSettings);
    }

    @Override
    /**
     * method either saves settings received from template in customFieldsIds field,
     * or just returns the template, if no POST request was preformed
     */
    public String doExecute() {
        log.info("ConfigurationWebworkAction.execute() method running");
        errorMessage = null;
        if (customFieldsIds != null && !currentSettings.equals(customFieldsIds)) {
            log.info("trying to save settings - {}", customFieldsIds);
            if(Pattern.matches(
                    "(?:[ ]*[,]?[ ]*customfield_[0-9]{5}[ ]*[,]?[ ]*)+", customFieldsIds)) {
                log.info("input ok");
                jiraAdapterSettingsService.saveSettings(customFieldsIds);
                currentSettings = jiraAdapterSettingsService.getSettings().getCommaSeparatedFields();
            } else {
                log.error("constructing errorMessage");
                errorMessage = "is not the sequence of comma separated custom fields " +
                        "keys like \"customfield_10000, customfield_10100\"";
            }
        } else {
            log.info("settings in form seems to be up to date");
        }
        return "configuration-page";
    }

}
