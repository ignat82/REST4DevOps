package ru.homecredit.jiraadapter.service;

import ru.homecredit.jiraadapter.dto.FieldOptions;
import ru.homecredit.jiraadapter.dto.request.FieldOptionsRequest;

public interface FieldOptionsService {

    FieldOptions getOptions(FieldOptionsRequest fieldOptionsRequest);

    FieldOptions postOption(FieldOptionsRequest fieldOptionsRequest);
}
