package ru.homecredit.jiraadapter.dto;

import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

// XML markup is necessary for JSON serialization
@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
@Slf4j
/**
 * class to store plugin settings
 */
public final class JiraAdapterSettings {
    @XmlElementWrapper(name = "editableFields")
    @XmlElement(name = "field")
    private List<String> editableFields;

    /**
     * default constructor
     */
    public JiraAdapterSettings() {
        log.info("starting JiraAdapterSettings instance construction");
    }

    /**
     *
     * @return - ArrayList of editable fields keys
     */
    public List<String> getEditableFields()
    {
        return new ArrayList<>(editableFields);
    }

    /**
     *
     * @return String for output of current field keys to configuration page textarea
     */
    public String getCommaSeparatedFields() {
        log.info("starting getCommaSeparatedFields method");
        StringBuilder stringBuilder = new StringBuilder();
        try {
            for (String editableField : editableFields) {
                stringBuilder.append(editableField).append(", ");
            }
            if (stringBuilder.length() != 0) {
                stringBuilder.setLength(stringBuilder.length() - 2);
            }
        } catch (Exception e) {
            log.error("settings seems to be empty. returning \"\"");
            return "";
        }
        return stringBuilder.toString();
    }

    /**
     *
     * @param editableFields - list of editable fields keys
     */
    public void setEditableFields(List<String> editableFields)
    {
        this.editableFields = new ArrayList<>(editableFields);
    }

}
