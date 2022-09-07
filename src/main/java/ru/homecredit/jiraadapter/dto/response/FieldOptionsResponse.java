package ru.homecredit.jiraadapter.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.homecredit.jiraadapter.dto.FieldOptions;
import ru.homecredit.jiraadapter.dto.FieldParameters;
import ru.homecredit.jiraadapter.dto.request.FieldOptionsRequest;

import javax.xml.bind.annotation.*;
import java.util.Map;

/**
 * class to pack FieldOptions DTO to JSON response
 * XML markup is necessary for correct JSON serialization in
 * FieldOptionsController doGet and doPost methods
 */
@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
@Slf4j
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
    @XmlElement(name = "result")
    private String result;

    public FieldOptionsResponse() {
        log.info("starting FieldOptionsResponse instance construction");
    }

    /**
     * constructor repacks some transport object fields to xml-marked response object
     * @param fieldOptions - DTO
     */
    public FieldOptionsResponse(FieldOptions fieldOptions) {
        log.trace("packing response to XML...");
        FieldOptionsRequest fieldOptionsRequest = fieldOptions.getFieldOptionsRequest();
        fieldKey = fieldOptionsRequest.getFieldKey();
        projectKey = fieldOptionsRequest.getProjectKey();
        issueTypeId = fieldOptionsRequest.getIssueTypeId();
        newOption = fieldOptionsRequest.getNewOption();
        action = fieldOptionsRequest.getAction().toString();
        FieldParameters fieldParameters = fieldOptions.getFieldParameters();
        fieldName = fieldParameters.getFieldName();
        projectName = fieldParameters.getProjectName();
        fieldConfigName = fieldParameters.getFieldConfigName();
        isDisabled = fieldOptions.getIsDisabled();
        this.fieldOptions = fieldOptions.getFieldOptionsArr();
        result = Boolean.toString(fieldOptions.isResult());
    }

}
