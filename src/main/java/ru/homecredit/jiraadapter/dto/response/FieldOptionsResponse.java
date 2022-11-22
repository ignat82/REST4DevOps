package ru.homecredit.jiraadapter.dto.response;

import lombok.Getter;
import lombok.Setter;
import ru.homecredit.jiraadapter.dto.FieldOptions;
import ru.homecredit.jiraadapter.dto.FieldParameters;
import ru.homecredit.jiraadapter.dto.request.FieldOptionsRequest;

import javax.xml.bind.annotation.*;
import java.util.List;

import static ru.homecredit.jiraadapter.dto.FieldOptions.JiraOption;

/**
 * class to pack FieldOptions DTO to JSON response
 * XML markup is necessary for correct JSON serialization in
 * FieldOptionsController getFieldOptionsList and doPost methods
 */
@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
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
    @XmlElementWrapper(name = "fieldOptionsList")
    @XmlElement(name = "option")
    private List<String> fieldOptionsList;
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
        FieldOptionsRequest fieldOptionsRequest = fieldOptions.getFieldOptionsRequest();
        fieldKey = fieldOptionsRequest.getFieldKey();
        projectKey = fieldOptionsRequest.getProjectKey();
        issueTypeId = fieldOptionsRequest.getIssueTypeId();
        newOption = fieldOptionsRequest.getNewOption();
        optionNewValue = fieldOptionsRequest.getOptionNewValue();
        action = fieldOptionsRequest.getAction().toString();
        FieldParameters fieldParameters = fieldOptions.getFieldParameters();
        if (fieldParameters != null) {
            fieldName = fieldParameters.getFieldName();
            projectName = fieldParameters.getProjectName();
            fieldConfigName = fieldParameters.getFieldConfigName();
        }
        jiraOptions = fieldOptions.getJiraOptions();
        fieldOptionsList = fieldOptions.getFieldOptionsList();
        success = Boolean.toString(fieldOptions.isSuccess());
        errorMessage = fieldOptions.getErrorMessage();
    }
}
