package ru.homecredit.jiraadapter.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.homecredit.jiraadapter.dto.FieldOptions;
import ru.homecredit.jiraadapter.dto.FieldParameters;
import ru.homecredit.jiraadapter.dto.request.FieldOptionsRequest;

import javax.xml.bind.annotation.*;
import java.util.List;
import java.util.Optional;

import static ru.homecredit.jiraadapter.dto.Constants.DEFAULT_ACQUIRED;
import static ru.homecredit.jiraadapter.dto.Constants.DEFAULT_RECEIVED;
import static ru.homecredit.jiraadapter.dto.FieldOptions.JiraOption;
import static ru.homecredit.jiraadapter.dto.request.FieldOptionsRequest.Action.NOT_RECOGNIZED;

/**
 * class to pack FieldOptions DTO to JSON response
 * XML markup is necessary for correct JSON serialization in
 * FieldOptionsController getFieldOptionsList and doPost methods
 */
@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
@Slf4j
public class FieldOptionsResponse {

    @XmlAttribute
    private String fieldKey;
    @XmlAttribute
    private String projectKey;
    @XmlAttribute(name = "issueTypeId")
    private String issueTypeId;
    @XmlAttribute(name = "newOption")
    private String newOption;
    @XmlAttribute(name = "optionNewValue")
    private String optionNewValue;
    @XmlAttribute(name = "action")
    private String action;
    @XmlElement(name = "fieldName")
    private String fieldName;
    @XmlElement(name = "projectName")
    private String projectName;
    @XmlElement(name = "fieldConfigName")
    private String fieldConfigName;
    @XmlElementWrapper(name = "jiraOptions")
    @XmlElement(name = "jiraOption")
    private List<JiraOption> jiraOptions;
    @XmlElement(name = "success")
    private String success;
    @XmlElement(name = "errorMessage")
    private String errorMessage;

    /**
     * constructor repacks some transport object fields to xml-marked response object
     * @param fieldOptions - DTO
     */
    public FieldOptionsResponse(FieldOptions fieldOptions) {
        log.info("FieldOptionsResponse construction");
        FieldOptionsRequest fieldOptionsRequest = fieldOptions.getFieldOptionsRequest();
        fieldKey = Optional.ofNullable(fieldOptionsRequest.getFieldKey()).orElse(DEFAULT_RECEIVED);
        projectKey = Optional.ofNullable(fieldOptionsRequest.getProjectKey()).orElse(DEFAULT_RECEIVED);
        issueTypeId = Optional.ofNullable(fieldOptionsRequest.getIssueTypeId()).orElse(DEFAULT_RECEIVED);
        newOption = Optional.ofNullable(fieldOptionsRequest.getNewOption()).orElse(DEFAULT_RECEIVED);
        optionNewValue = Optional.ofNullable(fieldOptionsRequest.getOptionNewValue()).orElse(DEFAULT_RECEIVED);
        action = Optional.ofNullable(fieldOptionsRequest.getAction()).orElse(NOT_RECOGNIZED).toString();
        FieldParameters fieldParameters = fieldOptions.getFieldParameters();
        if (fieldParameters != null) {
            fieldName =
                    Optional.ofNullable(fieldParameters.getFieldName()).orElse(DEFAULT_ACQUIRED);
            projectName =
                    Optional.ofNullable(fieldParameters.getProjectName()).orElse(DEFAULT_ACQUIRED);
            fieldConfigName =
                    Optional.ofNullable(fieldParameters.getFieldConfigName()).orElse(DEFAULT_ACQUIRED);
        }
        jiraOptions = fieldOptions.getJiraOptions();
        success = Boolean.toString(fieldOptions.isSuccess());
        errorMessage = fieldOptions.getErrorMessage();
    }
}
