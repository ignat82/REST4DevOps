package ru.homecredit.jiraadapter.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import ru.homecredit.jiraadapter.dto.FieldOptions;
import ru.homecredit.jiraadapter.dto.request.FieldOptionsRequest;
import ru.homecredit.jiraadapter.dto.response.FieldOptionsResponse;
import ru.homecredit.jiraadapter.service.FieldOptionsService;
import ru.homecredit.jiraadapter.service.FieldOptionsServiceImpl;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Optional;

@Slf4j
@Named
public class OptionsServiceAdapterImpl implements OptionsServiceAdapter {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final FieldOptionsService fieldOptionsService;

    @Inject
    public OptionsServiceAdapterImpl(FieldOptionsServiceImpl fieldOptionsService) {
        this.fieldOptionsService = fieldOptionsService;
    }

    public String postRequest(String requestBody) {
        Optional<FieldOptionsRequest> fieldOptionsRequest = initializeRequestFromString(requestBody);
        log.info("initialized");
        if (fieldOptionsRequest.isPresent()) {
            FieldOptions fieldOptions = fieldOptionsService.postOption(fieldOptionsRequest.get());
            return gson.toJson(new FieldOptionsResponse(fieldOptions));
        } else {
            return "failed to parse request body";
        }
    }

    public String getRequest(String fieldKey, String projectKey, String issueTypeId) {
        FieldOptionsRequest fieldOptionsRequest = new FieldOptionsRequest(fieldKey,
                                                                          projectKey,
                                                                          issueTypeId);
        FieldOptions fieldOptions = fieldOptionsService.getOptions(fieldOptionsRequest);
        log.info("got options {}", fieldOptions.toString());
        return gson.toJson(new FieldOptionsResponse(fieldOptions));
    }

    private Optional<FieldOptionsRequest> initializeRequestFromString(String requestBody) {
        Optional<FieldOptionsRequest> fieldOptionsRequest = Optional.empty();
        try {
            fieldOptionsRequest = Optional.ofNullable(gson.fromJson(requestBody,
                                                                    FieldOptionsRequest.class));
            log.info("requestBody deserialized as {}", fieldOptionsRequest);
        } catch (Exception e) {
            log.error("failed to parse request body \"{}\" with error \"{}\"",
                      requestBody, e.getMessage());
        }
        return fieldOptionsRequest;
    }
}