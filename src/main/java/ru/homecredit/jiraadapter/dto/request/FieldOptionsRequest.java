package ru.homecredit.jiraadapter.dto.request;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.homecredit.jiraadapter.dto.Constants;

/**
 * DTO class for storing request parameters of FieldOptionsController
 */
@Getter
@Setter
@Slf4j
public class FieldOptionsRequest {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final String fieldKey;
    private final String projectKey;
    private final String issueTypeId;
    private final String newOption;
    private final String optionNewValue;
    private Action action;

    public static FieldOptionsRequest initializeFromRequestBody(String requestBody) {
        FieldOptionsRequest fieldOptionsRequest = null;
        try {
            fieldOptionsRequest = gson.fromJson(requestBody, FieldOptionsRequest.class);
            log.info("json deserialized \n{}", fieldOptionsRequest);
        } catch (Exception e) {
            log.error("could not parse fieldOptionsRequest body - {}", requestBody);
            log.error("exception is - {}", e.getMessage());
        }
        return fieldOptionsRequest;
    }

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
                               String optionNewValue,
                               String actionCode) {
        this.fieldKey = fieldKey;
        this.projectKey = projectKey;
        this.issueTypeId = issueTypeId;
        this.newOption = newOption;
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
             Constants.DEFAULT_RECEIVED,
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
             Constants.DEFAULT_RECEIVED,
             Constants.DEFAULT_RECEIVED);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("fieldKey = ").append(fieldKey).append("; projectKey = ").
                append(projectKey).append("; issueTypeId = ").append(issueTypeId).
                append("; new option = ").append(newOption).append("; action = ").
                append(action).append(".");
        return stringBuilder.toString();
    }

    /**
     *  helper method to transfer the received string value of action to enum
     * @param actionCode - string value from request, defining the action to be preformed
     * @return - Action enum
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
    }
}
