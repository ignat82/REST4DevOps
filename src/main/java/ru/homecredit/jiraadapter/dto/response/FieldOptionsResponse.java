package ru.homecredit.jiraadapter.dto.response;

import lombok.Getter;
import lombok.Setter;
import ru.homecredit.jiraadapter.dto.FieldOptions;
import ru.homecredit.jiraadapter.dto.FieldParameters;
import ru.homecredit.jiraadapter.dto.request.FieldOptionsRequest;

import javax.xml.bind.annotation.*;
import java.util.Map;

/**
 * class to pack FieldOptions DTO to JSON response
 * XML markup is necessary for correct JSON serialization in
 * FieldOptionsController getFieldOptions and doPost methods
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
    @XmlAttribute(name = "action")
    private String action;
    @XmlElement(name = "fieldName")
    private String fieldName;
    @XmlElement(name = "projectName")
    private String projectName;
    @XmlElement(name = "fieldConfigName")
    private String fieldConfigName;
    @XmlElementWrapper(name = "fieldOptions")
    @XmlElement(name = "option")
    private String[] fieldOptions;
    @XmlElementWrapper(name = "isDisabled")
    @XmlElement(name = "disabled")
    private Map<String, Boolean> isDisabled;
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
        action = fieldOptionsRequest.getAction().toString();
        FieldParameters fieldParameters = fieldOptions.getFieldParameters();
        if (fieldParameters != null) {
            fieldName = fieldParameters.getFieldName();
            projectName = fieldParameters.getProjectName();
            fieldConfigName = fieldParameters.getFieldConfigName();
        }
        isDisabled = fieldOptions.getIsDisabled();
        this.fieldOptions = fieldOptions.getFieldOptionsArr();
        success = Boolean.toString(fieldOptions.isSuccess());
        errorMessage = fieldOptions.getErrorMessage();
    }
}
