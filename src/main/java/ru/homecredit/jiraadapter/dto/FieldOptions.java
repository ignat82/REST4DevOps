package ru.homecredit.jiraadapter.dto;

import com.atlassian.jira.issue.customfields.option.Option;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.homecredit.jiraadapter.dto.request.FieldOptionsRequest;

import java.util.List;
import java.util.Optional;

/**
 * DTO class to store Request parameters as well as response parameters and some
 * Jira properties of the field, being manipulated
 */
@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class FieldOptions {
    private final FieldOptionsRequest fieldOptionsRequest;
    private FieldParameters fieldParameters;
    private JiraOption manipulatedOption;
    private List<JiraOption> jiraOptions;
    private boolean success;
    private String errorMessage;

    public FieldOptions() {
        this(new FieldOptionsRequest());
    }

    public void setFieldParameters(FieldParameters fieldParameters) {
        this.fieldParameters = new FieldParameters();
        if (fieldParameters == null) {
            errorMessage = "failed to initialize field parameters";
        } else if (!fieldParameters.isValidContext()) {
            errorMessage = "failed to initialize field parameters due invalid context provided";
        } else if (!fieldParameters.isPermittedToEdit()) {
            errorMessage = "access to field is not permitted by plugin settings";
        } else {
            this.fieldParameters = fieldParameters;
        }
    }

    public Optional<JiraOption> getJiraOptionByValue(String optionValue) {
        return jiraOptions.stream().findAny()
                          .filter(o -> o.getOptionValue().equals(optionValue));
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

        public String toString() {

            return "id = " + optionId + "; value = " + optionValue + "; disabled = " + isDisabled;
        }
    }
}

