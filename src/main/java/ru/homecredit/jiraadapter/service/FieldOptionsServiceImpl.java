package ru.homecredit.jiraadapter.service;

import com.atlassian.jira.issue.customfields.manager.OptionsManager;
import com.atlassian.jira.issue.customfields.option.Option;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import lombok.extern.slf4j.Slf4j;
import ru.homecredit.jiraadapter.dto.FieldOptions;
import ru.homecredit.jiraadapter.dto.request.FieldOptionsRequest;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;
import java.util.Collections;

import static ru.homecredit.jiraadapter.dto.Constants.DEFAULT_RECEIVED;
import static ru.homecredit.jiraadapter.dto.request.FieldOptionsRequest.Action;
import static ru.homecredit.jiraadapter.dto.request.FieldOptionsRequest.Action.*;

@Slf4j
@Named
public class FieldOptionsServiceImpl implements FieldOptionsService {
    private final OptionsManager optionsManager;
    private final FieldInitializationService fieldInitializationService;

    @Inject
    public FieldOptionsServiceImpl(@ComponentImport OptionsManager optionsManager,
                                   FieldInitializationService fieldInitializationService) {
        this.optionsManager = optionsManager;
        this.fieldInitializationService = fieldInitializationService;
    }
    public FieldOptions getOptions(String fieldKey,
                                   String projectKey,
                                   String issueTypeId) {
        FieldOptions fieldOptions = fieldInitializationService
                .initializeField(new FieldOptionsRequest(fieldKey, projectKey, issueTypeId));
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
        FieldOptions fieldOptions = fieldInitializationService.initializeField(fieldOptionsRequest);
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
        fieldInitializationService.initializeOptions(fieldOptions);
        return fieldOptions;
    }
    private FieldOptions addOption(FieldOptions fieldOptions) {
        String optionValue = fieldOptions.getFieldOptionsRequest().getNewOption();
        log.trace("trying to add new option \"{}\"", optionValue);
        if (Arrays.asList(fieldOptions.getFieldOptionsList()).contains(optionValue)) {
            fieldOptions.setErrorMessage("new option " + optionValue + " already exists");
            return fieldOptions;
        }
        int size = fieldOptions.getFieldOptionsList().size();
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
        fieldInitializationService.initializeOptions(fieldOptions);
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
        fieldInitializationService.initializeOptions(fieldOptions);
        return fieldOptions;
    }

}
