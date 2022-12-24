package ru.homecredit.jiraadapter.service;

import com.atlassian.activeobjects.tx.Transactional;
import ru.homecredit.jiraadapter.entities.FieldsGroupSettingsRaw;
import java.util.Optional;

import java.util.List;

@Transactional
public interface SettingsService {
    String add(String description, String[] fieldsKeys, String[] usersKeys);
    List<FieldsGroupSettingsRaw> all();
    String delete(int id);
    String edit(int id, String description, String[] fieldsKeys, String[] usersKeys);
    List<String>  getAllUsers();
    Optional<FieldsGroupSettingsRaw> getById(int id);
    String prettyString(FieldsGroupSettingsRaw fieldsGroupSettingsRaw);
}
