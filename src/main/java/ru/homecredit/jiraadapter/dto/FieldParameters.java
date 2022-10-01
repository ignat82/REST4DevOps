package ru.homecredit.jiraadapter.dto;

import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

import static ru.homecredit.jiraadapter.dto.Constants.DEFAULT_ACQUIRED;

/**
 * DTO class to store manipulated field Jira parameters
 */
@Setter
@Getter
public class FieldParameters {
    private String fieldName = DEFAULT_ACQUIRED;
    private String projectName = DEFAULT_ACQUIRED;
    @Expose(serialize = false, deserialize = false)
    private FieldConfig fieldConfig;
    private String fieldConfigName = DEFAULT_ACQUIRED;
    private boolean validContext;
    private boolean isPermittedToEdit;
}
