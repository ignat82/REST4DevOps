package ru.homecredit.jiraadapter.service;

import com.atlassian.activeobjects.tx.Transactional;
import ru.homecredit.jiraadapter.entities.FieldsGroupSettings;
import java.util.Optional;

import java.util.List;

@Transactional
public interface SettingsService {
    FieldsGroupSettings add(String description, String[] fieldsKeys, String[] usersKeys);
    List<FieldsGroupSettings> all();
    boolean deleteById(int id);
    List<String>  getAllUsers();
    Optional<FieldsGroupSettings> getById(int id);
    String prettyString(FieldsGroupSettings fieldsGroupSettings);
}
