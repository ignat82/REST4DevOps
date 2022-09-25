package ru.homecredit.jiraadapter.service;

import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import lombok.extern.slf4j.Slf4j;
import ru.homecredit.jiraadapter.dto.JiraAdapterSettings;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * service class to retrieve and rewrite plugin settings - the array of strings
 * with jira customfield keys
 */
@Slf4j
@Named
public class JiraAdapterSettingsServiceImpl implements JiraAdapterSettingsService{
    private final PluginSettings pluginSettings;
    private final CustomFieldManager customFieldManager;

    /**
     * constructor creates settingsObject
     * @param pluginSettingsFactory - injected by spring to invoking class
     */
    @Inject
    /**
     * constructor with single parameter - PluginSettingsFactory Jira bean from application
     */
    public JiraAdapterSettingsServiceImpl(PluginSettingsFactory pluginSettingsFactory,
                                          CustomFieldManager customFieldManager) {
        pluginSettings = pluginSettingsFactory.createGlobalSettings();
        this.customFieldManager = customFieldManager;
    }

    public List<String> getAllSelectListFieldsKeys() {
        List<String> customfieldsKeys = new ArrayList<>();
        for (CustomField customField : customFieldManager.getCustomFieldObjects()) {
            customfieldsKeys.add(customField.getId());
        }
        return customfieldsKeys;
    }

    /**
     * receives the current plugin settings from jira
     * @return - object with current plugin settings
     */
    public JiraAdapterSettings getSettings() {
        log.info("running getSettings()");
        JiraAdapterSettings jiraAdapterSettings = new JiraAdapterSettings();
        try {
            List<String> fieldKeys = (List<String>)
                this.pluginSettings.get(JiraAdapterSettings.class.getName() + ".editableFields");
            jiraAdapterSettings.setEditableFields(fieldKeys);
        } catch (Exception e) {
            log.error("failed to acquire plugin settings with error " + e);
        }
        return jiraAdapterSettings;
    }

    /**
     *
     * @param customFieldKeys - list of customfield keys to save in plugin settings
     */
    public void saveCustomFieldsKeys(String[] customFieldKeys) {
        pluginSettings.put(JiraAdapterSettings.class.getName() + ".editableFields",
                               (customFieldKeys == null)
                                       ? new ArrayList<String> () :
                                       Arrays.asList(customFieldKeys));
    }
}
