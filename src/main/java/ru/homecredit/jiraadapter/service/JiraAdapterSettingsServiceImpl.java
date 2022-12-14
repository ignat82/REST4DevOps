package ru.homecredit.jiraadapter.service;

import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import lombok.extern.slf4j.Slf4j;
import ru.homecredit.jiraadapter.dto.JiraAdapterSettings;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ru.homecredit.jiraadapter.dto.Constants.EDITABLE_FIELDS_SETTING_KEY;
import static ru.homecredit.jiraadapter.dto.Constants.JIRA_ADAPTER_PLUGIN_SETTINGS_PREFIX;

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
     * @param customFieldManager - injected by spring to invoking class
     */
    @Inject
    public JiraAdapterSettingsServiceImpl(@ComponentImport PluginSettingsFactory pluginSettingsFactory,
                                          @ComponentImport CustomFieldManager customFieldManager) {
        pluginSettings = pluginSettingsFactory.createGlobalSettings();
        this.customFieldManager = customFieldManager;
    }

    public List<String> getAllCustomFieldsKeys() {
        return customFieldManager.getCustomFieldObjects().stream()
                         .map(CustomField::getId).collect(Collectors.toList());
    }

    public JiraAdapterSettings getSettings() {
        log.info("running getSettings()");
        JiraAdapterSettings jiraAdapterSettings = new JiraAdapterSettings();
        try {
            List<String> fieldKeys = (List<String>)
                pluginSettings.get(JIRA_ADAPTER_PLUGIN_SETTINGS_PREFIX
                                                + EDITABLE_FIELDS_SETTING_KEY);
            jiraAdapterSettings.setEditableFields(fieldKeys);
        } catch (Exception e) {
            log.error("failed to acquire plugin settings with error " + e);
        }
        return jiraAdapterSettings;
    }

    public void saveCustomFieldsKeys(String[] customFieldsKeys) {
        pluginSettings.put(JIRA_ADAPTER_PLUGIN_SETTINGS_PREFIX
                                   + EDITABLE_FIELDS_SETTING_KEY,
                               (customFieldsKeys == null)
                                       ? new ArrayList<String> ()
                                       : Arrays.asList(customFieldsKeys));
    }

    public boolean isPermittedToEdit(String fieldKey) {
        List<String> editableFields = getSettings().getEditableFields();
        log.trace("editable fields are - {}", editableFields);
        boolean permittedToEdit = editableFields != null &&
                editableFields.contains(fieldKey);
        log.trace("field {} is permitted to edit - {}", fieldKey, permittedToEdit);
        return permittedToEdit;
    }
}
