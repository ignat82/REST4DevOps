package ru.homecredit.jiraadapter.dto.request;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import ru.homecredit.jiraadapter.dto.Constants;

import static ru.homecredit.jiraadapter.dto.Constants.DEFAULT_RECEIVED;

@Getter
@Setter
/**
 * DTO class for storing request parameters of FieldOptionsController
 */
public class FieldOptionsRequest {
    private final String fieldKey;
    private final String projectKey;
    private final String issueTypeId;
    private final String newOption;
    private Action action;




    /**
     * @param fieldKey - key of Jira customfield, which options are manipulated
     * @param projectKey - key of Jira project, necessary to define manipulated field context
     * @param issueTypeId - id of Jira issue type, necessary to define manipulated field context
     * @param newOption - option to be manipulated
     * @param actionCode - string code of action to be preformed on option of given customfield
     */
    public FieldOptionsRequest(String fieldKey,
                               String projectKey,
                               String issueTypeId,
                               String newOption,
                               String actionCode) {
        this.fieldKey = StringUtils.defaultIfEmpty(fieldKey, DEFAULT_RECEIVED);
        this.projectKey = StringUtils.defaultIfEmpty(projectKey, DEFAULT_RECEIVED);;
        this.issueTypeId = StringUtils.defaultIfEmpty(issueTypeId, DEFAULT_RECEIVED);;
        this.newOption = StringUtils.defaultIfEmpty(newOption, DEFAULT_RECEIVED);;
        this.action = actionFromCode(actionCode);
    }

    /**
     *  helper method to transfer the received string value of action to enum
     * @param actionCode - string value from request, defining the action to be preformed
     * @return
     */
    private Action actionFromCode(String actionCode) {
        for (Action action : Action.ALL_VALUES) {
            if (action.getCode().equals(actionCode)) {
                return action;
            }
        }
        return Action.NOT_RECOGNIZED;
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
             Constants.DEFAULT_RECEIVED,
             Constants.DEFAULT_RECEIVED);
    }

    /**
     * default constructor
     */
    public FieldOptionsRequest() {
        this(Constants.DEFAULT_RECEIVED,
             Constants.DEFAULT_RECEIVED,
             Constants.DEFAULT_RECEIVED,
             Constants.DEFAULT_RECEIVED,
             Constants.DEFAULT_RECEIVED);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("fieldKey = ").append(fieldKey).append("; projectKey = ").
                append(projectKey).append("; issueTypeId = ").append(issueTypeId).
                             append("; new option = ").append(newOption).
                             append("; action = ").append(action).append(".");
        return stringBuilder.toString();
    }

    /**
     * nested enum class for storing action, to be preformed as the result of the request
     */
    @Getter
    @AllArgsConstructor
    public enum Action {
        @SerializedName("add") ADD ("add"),
        @SerializedName("enable") ENABLE ("enable"),
        @SerializedName("disable") DISABLE ("disable"),
        @SerializedName("not recognized") NOT_RECOGNIZED ("not recognized");

        private final String code;
        private static final Action[] ALL_VALUES = Action.values();
    }
}
