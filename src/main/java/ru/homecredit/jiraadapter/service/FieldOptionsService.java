package ru.homecredit.jiraadapter.service;

import ru.homecredit.jiraadapter.dto.FieldOptions;

public interface FieldOptionsService {
    FieldOptions getOptions(String fieldKey,
                                   String projectKey,
                                   String issueTypeId);
    FieldOptions  postOption(String requestBody);
}
