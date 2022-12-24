package ru.homecredit.jiraadapter.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FieldsGroupSettings {
    private int ID;
    private String description;
    private String[] fieldsKeys;
    private String[] usersKeys;
}
