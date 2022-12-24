package ru.homecredit.jiraadapter.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class FieldsGroupSettings {
    private int ID;
    private String description;
    private List<String> fieldsKeys;
    private List<String> usersKeys;
}
