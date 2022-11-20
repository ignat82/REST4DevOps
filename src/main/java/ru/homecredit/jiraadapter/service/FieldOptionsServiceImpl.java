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
import ru.homecredit.jiraadapter.dto.JiraAdapterSettings;
import ru.homecredit.jiraadapter.dto.request.FieldOptionsRequest;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.homecredit.jiraadapter.dto.Constants.DEFAULT_RECEIVED;
import static ru.homecredit.jiraadapter.dto.FieldOptions.JiraOption;
import static ru.homecredit.jiraadapter.dto.request.FieldOptionsRequest.Action;
import static ru.homecredit.jiraadapter.dto.request.FieldOptionsRequest.Action.*;

@Slf4j
@Named
public class FieldOptionsServiceImpl implements FieldOptionsService {
    private final FieldManager fieldManager;
    private final ProjectManager projectManager;
    private final OptionsManager optionsManager;
    private final JiraAdapterSettingsService jiraAdapterSettingsService;

    @Inject
    public FieldOptionsServiceImpl(@ComponentImport FieldManager fieldManager,
                                   @ComponentImport ProjectManager projectManager,
                                   @ComponentImport OptionsManager optionsManager,
                                   JiraAdapterSettingsServiceImpl jiraAdapterSettingsService) {
        this.fieldManager = fieldManager;
        this.projectManager = projectManager;
        this.optionsManager = optionsManager;
        this.jiraAdapterSettingsService = jiraAdapterSettingsService;
    }
    public FieldOptions getOptions(String fieldKey,
                                   String projectKey,
                                   String issueTypeId) {
        FieldOptions fieldOptions
                = initializeFieldOptions(new FieldOptionsRequest(fieldKey, projectKey, issueTypeId));
        if (fieldOptions.getErrorMessage() == null) {
            fieldOptions.setSuccess(true);
        }
        return fieldOptions;
    }

    public FieldOptions postOption(String requestBody) {
        FieldOptionsRequest fieldOptionsRequest =
                FieldOptionsRequest.initializeFromRequestBody(requestBody);
        if (fieldOptionsRequest == null) {
            FieldOptions fieldOptions = new FieldOptions();
            String errorMessage = "failed to parse request parameters";
            fieldOptions.setErrorMessage(errorMessage);
            log.error(errorMessage);
            return fieldOptions;
        }
        FieldOptions fieldOptions = initializeFieldOptions(fieldOptionsRequest);
        if (fieldOptions.getErrorMessage() != null) {
            log.error("shutting down postOption() due to error");
            return fieldOptions;
        }
        Action action = fieldOptions.getFieldOptionsRequest().getAction();
        if (action == NOT_RECOGNIZED) {
            fieldOptions.setErrorMessage("action parameter not recognized");
            return fieldOptions;
        }
        if (fieldOptions.getFieldOptionsRequest().getNewOption().equals(DEFAULT_RECEIVED)) {
            fieldOptions.setErrorMessage("option not provided in request");
            return fieldOptions;
        }
        if (action == ADD) {
            return addOption(fieldOptions);
        }
        String optionValue = fieldOptions.getFieldOptionsRequest().getNewOption();
        Option option = optionsManager.getOptions(fieldOptions.getFieldParameters().
                getFieldConfig()).getOptionForValue(optionValue, null);
        if (option == null) {
            String message = "option " + optionValue + " seems not to exist. shutting down";
            fieldOptions.setErrorMessage(message);
            log.error(message);
            return fieldOptions;
        }
        return (action == RENAME) ? renameOption(fieldOptions, option)
                : setOptionState(fieldOptions, option);
    }

    private FieldOptions renameOption(FieldOptions fieldOptions, Option option) {
        String oldOptionValue = option.getValue();
        String newOptionValue = fieldOptions.getFieldOptionsRequest().getOptionNewValue();
        if (newOptionValue.equals(DEFAULT_RECEIVED)) {
            String message = "newOptionValue was not provided";
            log.error(message);
            fieldOptions.setErrorMessage(message);
            return fieldOptions;
        }
        option.setValue(newOptionValue);
        optionsManager.updateOptions(Collections.singletonList(option));
        fieldOptions.setSuccess(true);
        log.trace("renamed option from \"{}\"  to \"{}\"", oldOptionValue, newOptionValue);
        /* acquiring Options object and Options from it once again, cuz the
        option state changed */
        initializeOptions(fieldOptions);
        return fieldOptions;
    }
    private FieldOptions addOption(FieldOptions fieldOptions) {
        String optionValue = fieldOptions.getFieldOptionsRequest().getNewOption();
        log.trace("trying to add new option \"{}\"", optionValue);
        if (Arrays.asList(fieldOptions.getFieldOptionsArr()).contains(optionValue)) {
            fieldOptions.setErrorMessage("new option " + optionValue + " already exists");
            return fieldOptions;
        }
        int size = fieldOptions.getFieldOptionsArr().length;
        Option createdOption =
                optionsManager.createOption(fieldOptions.getFieldParameters().getFieldConfig(),
                                    null,
                                    (long) (size + 1),
                                    optionValue);
        optionsManager.updateOptions(Collections.singletonList(createdOption));
        fieldOptions.setSuccess(true);
        log.trace("added option \"{}\" to Options", optionValue);
        /* acquiring Options object and Options from it once again, cuz the
        new one was appended */
        initializeOptions(fieldOptions);
        return fieldOptions;
    }

    private FieldOptions setOptionState(FieldOptions fieldOptions, Option option) {
        boolean isDisabled = (fieldOptions.getFieldOptionsRequest().getAction() == DISABLE);
        option.setDisabled(isDisabled);
        optionsManager.updateOptions(Collections.singletonList(option));
        fieldOptions.setSuccess(true);
        log.trace("set option \"{}\" isDisabled state to {}", option.getValue(), isDisabled);
        /* acquiring Options object and Options from it once again, cuz the
        option state changed */
        initializeOptions(fieldOptions);
        return fieldOptions;
    }

    /**
     * method to acquire the options of given field in given context
     * @param fieldOptionsRequest - DTO with request parameters
     * @return - FieldOptions transport object
     */
    private FieldOptions initializeFieldOptions(FieldOptionsRequest fieldOptionsRequest) {
        FieldOptions fieldOptions = new FieldOptions(fieldOptionsRequest);
        fieldOptions.setFieldParameters(initializeFieldParameters(fieldOptionsRequest));
        if (fieldOptions.getErrorMessage() == null) {
            initializeOptions(fieldOptions);
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
        try {
            ConfigurableField field = fieldManager.
                 getConfigurableField(fieldOptionsRequest.getFieldKey());
            fieldParameters.setFieldName(field.getName());
            Project project = projectManager.
                  getProjectByCurrentKeyIgnoreCase(fieldOptionsRequest.getProjectKey());
            fieldParameters.setProjectName(project.getName());
            IssueContext issueContext =
                    new IssueContextImpl(project.getId(), fieldOptionsRequest.getIssueTypeId());
            log.trace("issue context is " + issueContext);
            FieldConfig fieldConfig = field.getRelevantConfig(issueContext);
            log.trace("field config is - " + fieldConfig);
            fieldParameters.setFieldConfig(fieldConfig);
            fieldParameters.setFieldConfigName(fieldConfig.getName());
            fieldParameters.setValidContext(true);
            log.trace("valid context " + fieldParameters.isValidContext());
            JiraAdapterSettings jiraAdapterSettings = jiraAdapterSettingsService.getSettings();
            List<String> editableFields = jiraAdapterSettings.getEditableFields();
            log.trace("editable fields are - {}", editableFields);
            boolean permittedToEdit = editableFields != null &&
                    editableFields.contains(fieldOptionsRequest.getFieldKey());
            log.trace("field {} is permitted to edit - {}", field.getName(), permittedToEdit);
            fieldParameters.setPermittedToEdit(permittedToEdit);
        } catch (Exception e) {
            log.error("failed to initialize field parameters with error {}", e.toString());
            return null;
        }
        return fieldParameters;
    }

    /**
     * method to initialize options of field, attributes of which are stored in
     * manipulated FieldOptions DTO
     * @param fieldOptions - transport object
     */
    // side effect
    private void initializeOptions(FieldOptions fieldOptions) {
        Options options = Objects.requireNonNull(optionsManager.
              getOptions(fieldOptions.getFieldParameters().getFieldConfig()),
              "failed to acquire Options object");
        fieldOptions.setFieldOptionsArr(options.stream().map(Option::getValue).toArray(String[]::new));
        fieldOptions.setJiraOptions(options.stream().map(JiraOption::new).collect(Collectors.toList()));
        log.trace("field options id's are {}", fieldOptions.getJiraOptions().toString());
        log.trace("field options are {}", Arrays.toString(fieldOptions.getFieldOptionsArr()));
    }

}
