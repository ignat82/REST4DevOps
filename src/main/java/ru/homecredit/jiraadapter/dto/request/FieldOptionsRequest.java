package ru.homecredit.jiraadapter.dto.request;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import static ru.homecredit.jiraadapter.dto.request.FieldOptionsRequest.Action.actionFromCode;

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
             null,
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

    public static FieldOptionsRequest initializeFromPostRequestBody(String requestBody) {
        FieldOptionsRequest fieldOptionsRequest = null;
        try {
            fieldOptionsRequest = gson.fromJson(requestBody, FieldOptionsRequest.class);
            log.info("requestBody deserialized as {}", fieldOptionsRequest);
        } catch (Exception e) {
            log.error("failed to parse request body \"{}\" with error \"{}\"",
                      requestBody, e.getMessage());
        }
        return fieldOptionsRequest;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("fieldKey = ").append(fieldKey)
                .append("; projectKey = ").append(projectKey).append("; issueTypeId = ")
                .append(issueTypeId).append("; new option = ").append(newOption)
                .append("; action = ").append(action).append(".").toString();
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
