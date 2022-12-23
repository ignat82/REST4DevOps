package ru.homecredit.jiraadapter.entities;

import net.java.ao.Entity;
import net.java.ao.schema.Table;

@Table("settings")
public interface FieldToUser extends Entity {

    void setField(String field);
    void setUser(String user);
    String getUser();
    String getField();
}
