package ru.homecredit.jiraadapter.webwork;

import com.atlassian.jira.web.action.JiraWebActionSupport;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.homecredit.jiraadapter.service.JiraAdapterSettingsService;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Getter
@Setter
@RequiredArgsConstructor
/**
 * webwork class to output configuration page template and exchange plugin settings string with it
 */
public class ConfigurationWebworkAction extends JiraWebActionSupport {
    private final JiraAdapterSettingsService jiraAdapterSettingsService;
    private List<String> allCustomFieldsKeys;
    private List<String> savedCustomFieldsKeys;
    private String[] customFieldsKeysToSave;

    @Override
    /**
     * method either saves settings received from template in customFieldsIds field,
     * or just returns the template, if no POST request was preformed
     */
    public String execute() throws Exception {
        super.execute();
        allCustomFieldsKeys = jiraAdapterSettingsService.getAllSelectListFieldsKeys();
        savedCustomFieldsKeys = jiraAdapterSettingsService.getSettings().getEditableFields();
        return "configuration-page";
    }

    public void doSave() {
        log.info("saving {}", Arrays.toString(customFieldsKeysToSave));
        jiraAdapterSettingsService.saveCustomFieldsKeys(customFieldsKeysToSave);
    }

}
