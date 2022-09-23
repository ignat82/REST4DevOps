package ru.homecredit.jiraadapter.service;

import ru.homecredit.jiraadapter.dto.JiraAdapterSettings;

import java.util.List;

public interface JiraAdapterSettingsService {
    List<String> getAllSelectListFieldsKeys();
    JiraAdapterSettings getSettings();
    void saveSettings(String customFieldsIds);

}
