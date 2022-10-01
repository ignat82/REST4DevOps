package ru.homecredit.jiraadapter.webwork;

import com.atlassian.jira.web.action.JiraWebActionSupport;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.homecredit.jiraadapter.service.JiraAdapterSettingsService;

import java.util.Arrays;
import java.util.List;


/**
 * webwork class to output configuration page template, provide list of customfields,
 * persisting, in this jira instance, and save customfields, permitted to edit,
 * to plugin settings
 */
@Slf4j
@Getter
@Setter
@RequiredArgsConstructor
public class ConfigurationWebworkAction extends JiraWebActionSupport {
    private final JiraAdapterSettingsService jiraAdapterSettingsService;
    private List<String> allCustomFieldsKeys;
    private List<String> savedCustomFieldsKeys;
    private String[] customFieldsKeysToSave;


    /**
     * method either saves settings received from template in customFieldsIds field,
     * or just returns the template, if no POST request was preformed
     */
    @Override
    public String execute() throws Exception {
        super.execute();
        allCustomFieldsKeys = jiraAdapterSettingsService.getAllCustomFieldsKeys();
        savedCustomFieldsKeys = jiraAdapterSettingsService.getSettings().getEditableFields();
        return "configuration-page";
    }

    /**
     * method is invoked when submitting form in configuration-page.vm template
     * by super.execute() call in execute() method above
     */
    public void doSave() {
        log.info("saving {}", Arrays.toString(customFieldsKeysToSave));
        jiraAdapterSettingsService.saveCustomFieldsKeys(customFieldsKeysToSave);
    }

}
