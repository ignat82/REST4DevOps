package ru.homecredit.jiraadapter.web;

public interface OptionsServiceAdapter {
    String postRequest(String fieldKey,
                       String projectKey,
                       String issueTypeId,
                       String requestBody);
    String getRequest(String fieldKey, String projectKey, String issueTypeId);
}
