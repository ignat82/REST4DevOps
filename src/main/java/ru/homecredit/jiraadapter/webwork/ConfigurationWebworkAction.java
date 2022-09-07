package ru.homecredit.jiraadapter.webwork;

import com.atlassian.jira.web.action.JiraWebActionSupport;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.homecredit.jiraadapter.service.JiraAdapterSettingsService;

import javax.inject.Inject;

@Slf4j
@Getter
@Setter
/**
 * webwork class to output configuration page template and exchange plugin settings string with it
 */
public class ConfigurationWebworkAction extends JiraWebActionSupport {
    private final JiraAdapterSettingsService jiraAdapterSettingsService;
    private String currentSettings;
    private String customFieldsIds;
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
        log.info("ConfigurationWebworkAction.execute() running");
        if (customFieldsIds != null) {
            log.info("saving settings - {}", customFieldsIds);
            jiraAdapterSettingsService.saveSettings(customFieldsIds);
            currentSettings = jiraAdapterSettingsService.getSettings().getCommaSeparatedFields();
        }
        return "configuration-page";
    }

    public void doValidation() {
        if (customFieldsIds == null) {
           errorMap.put("input format error", "put comma separated customfields keys, like - "+
                                "\"customfield_00001, customfield_00002\"");
        }
    }
}
