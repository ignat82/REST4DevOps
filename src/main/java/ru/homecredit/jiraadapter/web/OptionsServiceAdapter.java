package ru.homecredit.jiraadapter.web;

public interface OptionsServiceAdapter {
    String getRequest(String fieldKey, String projectKey, String issueTypeId);

    String postRequest(String fieldKey,
                       String projectKey,
                       String issueTypeId,
                       String requestBody);

    String postRequest(String optionId, String requestBody);
}
