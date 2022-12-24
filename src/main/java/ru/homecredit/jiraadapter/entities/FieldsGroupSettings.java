package ru.homecredit.jiraadapter.entities;

import net.java.ao.Entity;
import net.java.ao.schema.Table;

@Table("settings")
public interface FieldsGroupSettings extends Entity {
    void setDescription(String description);
    void setFieldsKeys(String fieldsKeys);

    void setUsersKeys(String usersKeys);
    String getDescription();

    String getFieldsKeys();

    String getUsersKeys();
    //int getID();
}
