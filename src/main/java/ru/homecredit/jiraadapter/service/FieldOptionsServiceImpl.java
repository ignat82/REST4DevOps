package ru.homecredit.jiraadapter.service;

import com.atlassian.jira.issue.customfields.manager.OptionsManager;
import com.atlassian.jira.issue.customfields.option.Option;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import lombok.extern.slf4j.Slf4j;
import ru.homecredit.jiraadapter.dto.FieldOptions;
import ru.homecredit.jiraadapter.dto.FieldOptions.JiraOption;
import ru.homecredit.jiraadapter.dto.request.FieldOptionsRequest;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collections;
import java.util.Optional;

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
    public FieldOptions getOptions(FieldOptionsRequest fieldOptionsRequest) {
        FieldOptions fieldOptions = fieldInitializationService.initializeField(fieldOptionsRequest);
        fieldOptions.setSuccess(fieldOptions.getErrorMessage() == null);
        return fieldOptions;
    }

    public FieldOptions postOption(FieldOptionsRequest fieldOptionsRequest) {
        FieldOptions fieldOptions = fieldInitializationService.initializeField(fieldOptionsRequest);
        if (fieldOptions.getErrorMessage() != null) {
            log.error("shutting down postOption() due to error {}", fieldOptions.getErrorMessage());
            return fieldOptions;
        }
        Action action = fieldOptions.getFieldOptionsRequest().getAction();
        if (action == null) {
            fieldOptions.setErrorMessage("action parameter not recognized");
            return fieldOptions;
        }
        if (fieldOptions.getFieldOptionsRequest().getNewOption() == null
        && fieldOptions.getFieldOptionsRequest().getOptionId() == null) {
            fieldOptions.setErrorMessage("option not provided in request");
            return fieldOptions;
        }
        if (action == ADD) {
            return addOption(fieldOptions);
        }
        Option option;
        String optionValue = fieldOptions.getFieldOptionsRequest().getNewOption();;
        try {
            option = optionsManager.getOptions(fieldOptions.getFieldParameters().getFieldConfig())
                    .getOptionById(Long.parseLong(fieldOptionsRequest.getOptionId()));
        } catch (NumberFormatException nfe) {
            log.warn("no option Id provided");
            option = optionsManager.getOptions(fieldOptions.getFieldParameters().getFieldConfig())
                                   .getOptionForValue(optionValue, null);
        }
        if (option == null) {
            String message = "option \"" + optionValue + "\" seems not to exist. shutting down";
            fieldOptions.setErrorMessage(message);
            log.error(message);
            return fieldOptions;
        }
        return (action == RENAME) ? renameOption(fieldOptions, option)
                                  : setOptionState(fieldOptions, option);
    }

    private FieldOptions renameOption(FieldOptions fieldOptions, Option option) {
        fieldOptions.setManipulatedOption(new JiraOption(option));
        String oldOptionValue = option.getValue();
        String newOptionValue = fieldOptions.getFieldOptionsRequest().getOptionNewValue();
        if (newOptionValue == null) {
            String message = "newOptionValue was not provided";
            log.error(message);
            fieldOptions.setErrorMessage(message);
        } else if (oldOptionValue.equals(newOptionValue)) {
            String message = "newOptionValue equals oldOptionValue";
            log.error(message);
            fieldOptions.setErrorMessage(message);
        } else {
            option.setValue(newOptionValue);
            optionsManager.updateOptions(Collections.singletonList(option));
            fieldOptions.setManipulatedOption(new JiraOption(option));
            fieldOptions.setSuccess(true);
            log.trace("renamed option from \"{}\"  to \"{}\"", oldOptionValue, newOptionValue);
        /* acquiring Options object and Options from it once again, cuz the
        option state changed */
            fieldInitializationService.initializeOptions(fieldOptions);
        }
        return fieldOptions;
    }

    private FieldOptions addOption(FieldOptions fieldOptions) {
        String optionValue = fieldOptions.getFieldOptionsRequest().getNewOption();
        log.trace("trying to add new option \"{}\"", optionValue);
        Optional<JiraOption> existingOption = fieldOptions.getJiraOptionByValue(optionValue);
        if (existingOption.isPresent()) {
            fieldOptions.setErrorMessage("new option \"" + optionValue + "\" exists already");
            fieldOptions.setManipulatedOption(existingOption.get());
            return fieldOptions;
        }
        int size = fieldOptions.getJiraOptions().size();
        Option createdOption =
                optionsManager.createOption(fieldOptions.getFieldParameters().getFieldConfig(),
                                    null,
                                    (long) (size + 1),
                                    optionValue);
        optionsManager.updateOptions(Collections.singletonList(createdOption));
        fieldOptions.setManipulatedOption(new JiraOption(createdOption));
        fieldOptions.setSuccess(true);
        log.trace("added option \"{}\" to Options", optionValue);
        /* acquiring Options object and Options from it once again, cuz the
        new one was appended */
        fieldInitializationService.initializeOptions(fieldOptions);
        return fieldOptions;
    }

    private FieldOptions setOptionState(FieldOptions fieldOptions, Option option) {
        fieldOptions.setManipulatedOption(new JiraOption(option));
        boolean isDisabled = (fieldOptions.getFieldOptionsRequest().getAction() == DISABLE);
        if (option.getDisabled() == isDisabled) {
            fieldOptions.setErrorMessage("option disabled state is \"" + isDisabled + "\" already");
            return fieldOptions;
        }
        option.setDisabled(isDisabled);
        optionsManager.updateOptions(Collections.singletonList(option));
        fieldOptions.setManipulatedOption(new JiraOption(option));
        fieldOptions.setSuccess(true);
        log.trace("set option \"{}\" isDisabled state to {}", option.getValue(), isDisabled);
        /* acquiring Options object and Options from it once again, cuz the
        option state changed */
        fieldInitializationService.initializeOptions(fieldOptions);
        return fieldOptions;
    }

}
