package ru.homecredit.jiraadapter.entities;

import net.java.ao.Entity;
import net.java.ao.schema.Table;

@Table("settings")
public interface FieldsGroupSettings extends Entity {
    void setFieldsKeys(String[] fieldsKeys);
    void setUsersKeys(String[] usersKeys);
    String[] getFieldsKeys();
    String[] getUsersKeys();
}
