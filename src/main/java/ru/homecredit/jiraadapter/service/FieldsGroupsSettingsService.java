package ru.homecredit.jiraadapter.service;

import com.atlassian.activeobjects.tx.Transactional;
import ru.homecredit.jiraadapter.entities.FieldsGroupSettings;

import java.util.List;

@Transactional
public interface FieldsGroupsSettingsService {
    FieldsGroupSettings add(String fieldsKeys, String usersKeys);
    List<FieldsGroupSettings> all();
    List<String>  getAllUsers();
}
