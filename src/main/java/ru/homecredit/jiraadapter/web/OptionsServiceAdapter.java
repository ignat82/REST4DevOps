package ru.homecredit.jiraadapter.web;

import ru.homecredit.jiraadapter.dto.response.FieldOptionsResponse;

public interface OptionsServiceAdapter {
    FieldOptionsResponse getRequest(String fieldKey, String projectKey, String issueTypeId);

    FieldOptionsResponse postRequest(String fieldKey,
                       String projectKey,
                       String issueTypeId,
                       String requestBody);

    FieldOptionsResponse postRequest(String optionId, String requestBody);
}
