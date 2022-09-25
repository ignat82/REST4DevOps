package ru.homecredit.jiraadapter.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

// XML markup is necessary for JSON serialization
@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
@Slf4j
@Getter
@Setter
/**
 * class to store plugin settings
 */
public final class JiraAdapterSettings {
    @XmlElementWrapper(name = "editableFields")
    @XmlElement(name = "field")
    private List<String> editableFields = new ArrayList<>();
}
