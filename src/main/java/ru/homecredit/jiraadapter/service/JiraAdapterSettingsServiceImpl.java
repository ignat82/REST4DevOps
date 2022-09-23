package ru.homecredit.jiraadapter.service;

import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
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
        log.info("there are {} customField objects",
                 customFieldManager.getCustomFieldObjects().size());
        for (CustomField customField : customFieldManager.getCustomFieldObjects()) {
            customfieldsKeys.add(customField.getId());
        }
        log.info("customfieldKeys are {}", customfieldsKeys);
        return customfieldsKeys;
    }

    /**
     * receives the current plugin settings from jira
     * @return - object with current plugin settings
     */
    public JiraAdapterSettings getSettings() {
        JiraAdapterSettings jiraAdapterSettings = new JiraAdapterSettings();
        try {
            log.trace(JiraAdapterSettings.class.getName());
            List<String> fieldKeys = (List<String>)
                this.pluginSettings.get(JiraAdapterSettings.class.getName() +
                                                ".editableFields");
            log.trace("field keys are - {}", fieldKeys);
            jiraAdapterSettings.setEditableFields(fieldKeys);
            log.info("editableFields are: {}.", fieldKeys);
        } catch (Exception e) {
            log.error("failed to acquire plugin settings with error " + e);
        }
        return jiraAdapterSettings;
    }

    /**
     * parses the string with comma separated field keys and packs them to JiraAdapterSettings
     * object
     *
     * @param customFieldsIds - string with comma separated field keys
     */
    public void saveSettings(String customFieldsIds) {
        try {
            String[] fieldsKeys = StringUtils.deleteWhitespace(customFieldsIds).split(",");
            for (String key : fieldsKeys) {
                log.trace("key is: {}", key);
            }
            pluginSettings.put(JiraAdapterSettings.class.getName() +
                                       ".editableFields", Arrays.asList(fieldsKeys));
        } catch (Exception exception){
            log.error("caught {} when parsing customFieldsIds", exception.getMessage());
        }
        getSettings();
    }
}
