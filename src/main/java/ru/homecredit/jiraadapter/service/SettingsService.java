package ru.homecredit.jiraadapter.service;

import com.atlassian.activeobjects.tx.Transactional;
import ru.homecredit.jiraadapter.entities.FieldsGroupSettings;
import ru.homecredit.jiraadapter.entities.FieldsGroupSettingsRaw;

import java.util.List;
import java.util.Optional;

@Transactional
public interface SettingsService {
    String add(String description, String[] fieldsKeys, String[] usersKeys);
    List<FieldsGroupSettings> all();
    String delete(int id);
    String edit(int id, String description, String[] fieldsKeys, String[] usersKeys);
    List<String> getAllCustomFieldsKeys();
    List<String>  getAllUsers();
    Optional<FieldsGroupSettingsRaw> getById(int id);
    boolean isPermittedToEdit(String fieldKey);
    String prettyString(FieldsGroupSettings fieldsGroupSettingsRaw);
}
