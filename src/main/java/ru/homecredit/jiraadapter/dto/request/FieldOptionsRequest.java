package ru.homecredit.jiraadapter.dto.request;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import static ru.homecredit.jiraadapter.dto.request.FieldOptionsRequest.Action.actionFromCode;

/**
 * DTO class for storing request parameters of FieldOptionsController
 */
@Getter
@Setter
@Slf4j
@ToString
public class FieldOptionsRequest {
    private String fieldKey;
    private String projectKey;
    private String issueTypeId;
    private final String newOption;
    private String optionId;
    private final String optionNewValue;
    private Action action;

    /**
     * @param fieldKey - key of Jira customfield, which options are manipulated
     * @param projectKey - key of Jira project, necessary to define manipulated field context
     * @param issueTypeId - id of Jira issue type, necessary to define manipulated field context
     * @param newOption - option to be manipulated
     * @param optionNewValue - new value for existing option to be set
     * @param actionCode - string code of action to be preformed on option of given customfield
     */
    public FieldOptionsRequest(String fieldKey,
                               String projectKey,
                               String issueTypeId,
                               String newOption,
                               String optionId,
                               String optionNewValue,
                               String actionCode) {
        this.fieldKey = fieldKey;
        this.projectKey = projectKey;
        this.issueTypeId = issueTypeId;
        this.newOption = newOption;
        this.optionId = optionId;
        this.optionNewValue = optionNewValue;
        this.action = actionFromCode(actionCode);
    }

    /**
     *
     * @param fieldKey - key of Jira customfield, which options are manipulated
     * @param projectKey - key of Jira project, necessary to define manipulated field context
     * @param issueTypeId - id of Jira issue type, necessary to define manipulated field context
     */
    public FieldOptionsRequest(String fieldKey,
                               String projectKey,
                               String issueTypeId) {
        this(fieldKey,
             projectKey,
             issueTypeId,
             null,
             null,
             null,
             null);
    }

    public FieldOptionsRequest(String fieldKey,
                               String optionId) {
        this(fieldKey,
             null,
             null,
             null,
             optionId,
             null,
             null);
    }

    /**
     * default constructor
     */
    public FieldOptionsRequest() {
        this(null,
             null,
             null);
    }

    /**
     * nested enum class for storing action, to be preformed as the result of the request
     */
    @Getter
    @RequiredArgsConstructor
    public enum Action {
        @SerializedName("add") ADD ("add"),
        @SerializedName("enable") ENABLE ("enable"),
        @SerializedName("disable") DISABLE ("disable"),
        @SerializedName("rename") RENAME ("rename"),
        @SerializedName("not recognized") NOT_RECOGNIZED ("not recognized");
        private static final Action[] ALL_VALUES = Action.values();
        private final String code;

        public static Action actionFromCode(String actionCode) {
            if (actionCode == null) {
                return null;
            }
            for (Action action : Action.ALL_VALUES) {
                if (action.getCode().equals(actionCode)) {
                    return action;
                }
            }
            return Action.NOT_RECOGNIZED;
        }
    }
}
