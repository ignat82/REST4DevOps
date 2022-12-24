package ru.homecredit.jiraadapter.service;

import com.atlassian.jira.issue.context.IssueContext;
import com.atlassian.jira.issue.context.IssueContextImpl;
import com.atlassian.jira.issue.customfields.manager.OptionsManager;
import com.atlassian.jira.issue.customfields.option.Option;
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
import ru.homecredit.jiraadapter.dto.request.FieldOptionsRequest;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Named
public class FieldInitializationService {
    private final FieldManager fieldManager;
    private final ProjectManager projectManager;
    private final OptionsManager optionsManager;
    private final JiraAdapterSettingsService jiraAdapterSettingsService;
    //private final SettingsServiceImpl settingsService;

    @Inject
    public FieldInitializationService(@ComponentImport FieldManager fieldManager,
                                      @ComponentImport ProjectManager projectManager,
                                      @ComponentImport OptionsManager optionsManager,
                                      //@ComponentImport SettingsServiceImpl settingsService,
                                      JiraAdapterSettingsServiceImpl jiraAdapterSettingsService) {
        this.fieldManager = fieldManager;
        this.projectManager = projectManager;
        this.optionsManager = optionsManager;
        //this.settingsService = settingsService;
        this.jiraAdapterSettingsService = jiraAdapterSettingsService;
    }

    /**
     * method to acquire the options of given field in given context
     * @param fieldOptionsRequest - DTO with request parameters
     * @return - FieldOptions transport object
     */
    public FieldOptions initialize(FieldOptionsRequest fieldOptionsRequest) {
        FieldOptions fieldOptions = new FieldOptions(fieldOptionsRequest);
        try {
            FieldParameters fieldParameters = (fieldOptionsRequest.getOptionId() == null)
                    ? initializeFieldParameters(fieldOptionsRequest)
                    : initializeFieldParameters(fieldOptionsRequest.getOptionId());
            if (fieldOptionsRequest.getOptionId() != null) {
                String fieldKey = fieldParameters.getFieldConfig().getFieldId();
                log.info(fieldOptionsRequest.toString());
                fieldOptions.getFieldOptionsRequest().setFieldKey(fieldKey);
                log.info("point 3");
                boolean isPermittedToEdit = jiraAdapterSettingsService.isPermittedToEdit(fieldKey);
                fieldParameters.setPermittedToEdit(isPermittedToEdit);
            }
            fieldOptions.setFieldParameters(fieldParameters);
            log.info("fieldOptions {}", fieldOptions);
            initializeOptions(fieldOptions);
        } catch (Exception e) {
            fieldOptions.addErrorMessage("failed to acquire fieldParameters." +
                 " check fieldKey, projectKey and issueTypeId");
        }
        return fieldOptions;
    }

    private FieldParameters initializeFieldParameters(String optionId) {
        Option option = optionsManager.findByOptionId(Long.parseLong(optionId));
        log.info(String.valueOf(option==null));
        FieldParameters fieldParameters = new FieldParameters();
        FieldConfig fieldConfig = option.getRelatedCustomField();
        fieldParameters.setFieldConfig(fieldConfig);
        fieldParameters.setFieldConfigName(fieldConfig.getName());
        fieldParameters.setFieldName(fieldConfig.getCustomField().getFieldName());
        fieldParameters.setValidContext(true);
        return fieldParameters;
    }

    /**
     *
     * @param fieldOptionsRequest - DTO, containing request parameters
     * @return - FieldParameters DTO with some Jira properties of manipulated customfield
     */
    private FieldParameters initializeFieldParameters(FieldOptionsRequest fieldOptionsRequest) {
        FieldParameters fieldParameters = new FieldParameters();
        String fieldKey = fieldOptionsRequest.getFieldKey();
        fieldParameters.setPermittedToEdit(jiraAdapterSettingsService.isPermittedToEdit(fieldKey));
        ConfigurableField field =
                fieldManager.getConfigurableField(fieldKey);
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
        fieldOptions.setJiraOptions(
                options.stream().map(FieldOptions.JiraOption::new).collect(Collectors.toList()));
        log.trace("field options id's are {}", fieldOptions.getJiraOptions().toString());
    }
}
