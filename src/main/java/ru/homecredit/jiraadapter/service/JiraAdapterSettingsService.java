package ru.homecredit.jiraadapter.service;

import ru.homecredit.jiraadapter.dto.JiraAdapterSettings;

import java.util.List;

/**
 * service class for manging customfield options trough Jira Java API by handling
 * the data, received from controller in form of request body string, and returning
 * to controller the FieldOptions transport object with information of the action
 * preformed and some Jira parameters
 */
public interface JiraAdapterSettingsService {
    /**
     *
     * @return - list of all customfields in this Jira instance
     */
    List<String> getAllCustomFieldsKeys();

    /**
     * receives the current plugin settings from jira
     * @return - object with current plugin settings
     */
    JiraAdapterSettings getSettings();

    /**
     * saves the list of keys of customfield, which list of options is permitted
     * to edit by calling the plugin REST endpoint
     * @param customFieldsKeys - list of customfields keys to save in plugin settings
     */
    void saveCustomFieldsKeys(String[] customFieldsKeys);

    boolean isPermittedToEdit(String fieldKey);

}
