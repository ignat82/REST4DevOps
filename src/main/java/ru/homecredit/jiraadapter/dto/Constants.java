package ru.homecredit.jiraadapter.dto;

/**
 * constants for storing default value for FieldOptions field including
 * received from REST and acquired internally from Jira Java API
 */
public class Constants {
    public static final String DEFAULT_RECEIVED = "not provided";
    public static final String DEFAULT_ACQUIRED = "failed to acquire";
    public static final String JIRA_ADAPTER_PLUGIN_SETTINGS_PREFIX
            = "jira-adapter-plugin-settings";
    public static final String EDITABLE_FIELDS_SETTING_KEY
            = ".editableFields";
}
