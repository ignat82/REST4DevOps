package ru.homecredit.jiraadapter.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.RequiredArgsConstructor;
import ru.homecredit.jiraadapter.dto.request.FieldOptionsRequest;

import java.util.Map;

/**
 * DTO class to store Request parameters as well as response parameters and some
 * Jira properties of the field, being manipulated
 */
@Getter
@Setter
@RequiredArgsConstructor
public class FieldOptions {
    @NonNull
    private FieldOptionsRequest fieldOptionsRequest;
    private FieldParameters fieldParameters;
    private String[] fieldOptionsArr;
    private Map<String, Boolean> isDisabled;
    private boolean success;
    private String errorMessage;

    public FieldOptions() {
        this(new FieldOptionsRequest());
    }
}

