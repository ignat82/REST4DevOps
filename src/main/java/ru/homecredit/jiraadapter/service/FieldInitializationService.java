package ru.homecredit.jiraadapter.service;

import com.atlassian.jira.issue.context.IssueContext;
import com.atlassian.jira.issue.context.IssueContextImpl;
import com.atlassian.jira.issue.customfields.manager.OptionsManager;
import com.atlassian.jira.issue.customfields.option.Options;
import com.atlassian.jira.issue.fields.ConfigurableField;
import com.atlassian.jira.issue.fields.FieldManager;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import lombok.extern.slf4j.Slf4j;
import ru.homecredit.jiraadapter.dto.FieldOptions;
import ru.homecredit.jiraadapter.dto.FieldParameters;
import ru.homecredit.jiraadapter.dto.JiraAdapterSettings;
import ru.homecredit.jiraadapter.dto.request.FieldOptionsRequest;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Named
public class FieldInitializationService {
    private final FieldManager fieldManager;
    private final ProjectManager projectManager;
    private final OptionsManager optionsManager;
    private final JiraAdapterSettingsService jiraAdapterSettingsService;

    @Inject
    public FieldInitializationService(@ComponentImport FieldManager fieldManager,
                                      @ComponentImport ProjectManager projectManager,
                                      @ComponentImport OptionsManager optionsManager,
                                      JiraAdapterSettingsServiceImpl jiraAdapterSettingsService) {
        this.fieldManager = fieldManager;
        this.projectManager = projectManager;
        this.optionsManager = optionsManager;
        this.jiraAdapterSettingsService = jiraAdapterSettingsService;
    }

    /**
     * method to acquire the options of given field in given context
     * @param fieldOptionsRequest - DTO with request parameters
     * @return - FieldOptions transport object
     */
    FieldOptions initializeField(FieldOptionsRequest fieldOptionsRequest) {
        FieldOptions fieldOptions = new FieldOptions(fieldOptionsRequest);
        try {
            fieldOptions.setFieldParameters(initializeFieldParameters(fieldOptionsRequest));
            initializeOptions(fieldOptions);
        } catch (Exception e) {
            fieldOptions.setErrorMessage("failed to acquire fieldParameters." +
                                                 "check fieldKey, projectKey and issueTypeId");
        }
        return fieldOptions;
    }

    /**
     *
     * @param fieldOptionsRequest - DTO, containing request parameters
     * @return - FieldParameters DTO with some Jira properties of manipulated customfield
     */
    private FieldParameters initializeFieldParameters(FieldOptionsRequest fieldOptionsRequest) {
        FieldParameters fieldParameters = new FieldParameters();
        ConfigurableField field =
                fieldManager.getConfigurableField(fieldOptionsRequest.getFieldKey());
        fieldParameters.setFieldName(field.getName());
        Project project = projectManager.
                getProjectByCurrentKeyIgnoreCase(fieldOptionsRequest.getProjectKey());
        fieldParameters.setProjectName(project.getName());
        IssueContext issueContext =
                new IssueContextImpl(project.getId(), fieldOptionsRequest.getIssueTypeId());
        FieldConfig fieldConfig = field.getRelevantConfig(issueContext);
        fieldParameters.setFieldConfig(fieldConfig);
        fieldParameters.setFieldConfigName(fieldConfig.getName());
        fieldParameters.setValidContext(true);
        log.trace("valid context - " + fieldParameters.isValidContext());
        JiraAdapterSettings jiraAdapterSettings = jiraAdapterSettingsService.getSettings();
        List<String> editableFields = jiraAdapterSettings.getEditableFields();
        log.trace("editable fields are - {}", editableFields);
        boolean permittedToEdit = editableFields != null &&
                editableFields.contains(fieldOptionsRequest.getFieldKey());
        log.trace("field {} is permitted to edit - {}", field.getName(), permittedToEdit);
        fieldParameters.setPermittedToEdit(permittedToEdit);
        return fieldParameters;
    }

    /**
     * method to initialize options of field, attributes of which are stored in
     * manipulated FieldOptions DTO
     * @param fieldOptions - transport object
     */
    // side effect
    void initializeOptions(FieldOptions fieldOptions) {
        Options options = Objects.requireNonNull(optionsManager.
                  getOptions(fieldOptions.getFieldParameters().getFieldConfig()),
                                                 "failed to acquire Options object");
        fieldOptions.setJiraOptions(options.stream().map(FieldOptions.JiraOption::new).collect(Collectors.toList()));
        log.trace("field options id's are {}", fieldOptions.getJiraOptions().toString());
    }
}
