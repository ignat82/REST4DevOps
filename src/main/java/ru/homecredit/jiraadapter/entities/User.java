package ru.homecredit.jiraadapter.entities;

import net.java.ao.Entity;
import net.java.ao.ManyToMany;

public interface User extends Entity {
    @ManyToMany(value = FieldToUser.class)
    Field[] getFields();
}
