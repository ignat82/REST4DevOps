package ru.homecredit.jiraadapter.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.homecredit.jiraadapter.dto.request.FieldOptionsRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * DTO class to store Request parameters as well as response parameters and some
 * Jira properties of the field, being manipulated
 */
@Getter
@Setter
@Slf4j
public class FieldOptions {

    private FieldOptionsRequest fieldOptionsRequest;
    private FieldParameters fieldParameters;
    private String[] fieldOptionsArr;
    private Map<String, Boolean> isDisabled = new HashMap<>();
    private boolean result = false;

    /**
     * default constructor
     */
    public FieldOptions() {
        this(new FieldOptionsRequest());
    }

    /**
     *
     * @param fieldOptionsRequest - DTO object with request parameters
     */
    public FieldOptions(FieldOptionsRequest fieldOptionsRequest) {
        this.fieldOptionsRequest = fieldOptionsRequest;
    }
}

