package ru.homecredit.jiraadapter.dto;

import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO class to store manipulated field Jira parameters
 */
@Setter
@Getter
public class FieldParameters {
    private String fieldName;
    private  String projectName;
    @Expose(serialize = false, deserialize = false)
    private FieldConfig fieldConfig;
    private String fieldConfigName;
    private boolean validContext;
    private boolean isPermittedToEdit;
}
