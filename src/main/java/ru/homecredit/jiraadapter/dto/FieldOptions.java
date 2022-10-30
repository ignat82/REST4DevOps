package ru.homecredit.jiraadapter.dto;

import com.atlassian.jira.issue.customfields.option.Option;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.homecredit.jiraadapter.dto.request.FieldOptionsRequest;

import java.util.List;

/**
 * DTO class to store Request parameters as well as response parameters and some
 * Jira properties of the field, being manipulated
 */
@Getter
@Setter
@RequiredArgsConstructor
public class FieldOptions {
    private final FieldOptionsRequest fieldOptionsRequest;
    private FieldParameters fieldParameters;
    private String[] fieldOptionsArr;
    private List<JiraOption> jiraOptions;
    private boolean success;
    private String errorMessage;

    public FieldOptions() {
        this(new FieldOptionsRequest());
    }

    @Getter
    @Setter
    public static class JiraOption {
        private Long optionId;
        private String optionValue;
        private boolean isDisabled;

        public JiraOption(Option option) {
            optionId = option.getOptionId();
            optionValue = option.getValue();
            isDisabled = option.getDisabled();
        }
    }
}

