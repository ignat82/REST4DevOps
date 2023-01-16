package ru.homecredit.jiraadapter.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import ru.homecredit.jiraadapter.dto.FieldOptions;
import ru.homecredit.jiraadapter.dto.request.FieldOptionsRequest;
import ru.homecredit.jiraadapter.dto.response.FieldOptionsResponse;
import ru.homecredit.jiraadapter.service.FieldOptionsService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collections;
import java.util.Optional;

@Slf4j
@Named
public class OptionsServiceAdapterImpl implements OptionsServiceAdapter {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final FieldOptionsService fieldOptionsService;


    @Inject
    public OptionsServiceAdapterImpl(FieldOptionsService fieldOptionsService) {
        this.fieldOptionsService = fieldOptionsService;
    }

    public FieldOptionsResponse getRequest(String fieldKey, String projectKey, String issueTypeId) {
        FieldOptionsRequest fieldOptionsRequest = new FieldOptionsRequest(fieldKey,
                                                                          projectKey,
                                                                          issueTypeId);
        FieldOptions fieldOptions = fieldOptionsService.getOptions(fieldOptionsRequest);
        log.info("got options {}", fieldOptions.toString());
        return new FieldOptionsResponse(fieldOptions);
    }

    public FieldOptionsResponse postRequest(String optionId, String requestBody) {
        return postRequest(null,
                           null,
                           null,
                           optionId,
                           requestBody);
    }

    public FieldOptionsResponse postRequest(String fieldKey,
                              String projectKey,
                              String issueTypeId,
                              String requestBody) {
        return postRequest(fieldKey,
                           projectKey,
                           issueTypeId,
                           null,
                           requestBody);
    }
    private FieldOptionsResponse postRequest(String fieldKey,
                              String projectKey,
                              String issueTypeId,
                              String optionId,
                              String requestBody) {
        Optional<FieldOptionsRequest> fieldOptionsRequest =
                initializeRequestFromString(fieldKey,
                                            projectKey,
                                            issueTypeId,
                                            optionId,
                                            requestBody);
        if (fieldOptionsRequest.isPresent()) {
            FieldOptions fieldOptions = fieldOptionsService.postOption(fieldOptionsRequest.get());
            return new FieldOptionsResponse(fieldOptions);
        } else {
            FieldOptionsResponse fieldOptionsResponse =
                    new FieldOptionsResponse(new FieldOptions());
            fieldOptionsResponse.setErrorMessages(Collections.singletonList("failed to parse request body"));
            return fieldOptionsResponse;
        }
    }
    private Optional<FieldOptionsRequest> initializeRequestFromString(String fieldKey,
                                                                      String projectKey,
                                                                      String issueTypeId,
                                                                      String optionId,
                                                                      String requestBody) {
        Optional<FieldOptionsRequest> fieldOptionsRequest;
        try {
            fieldOptionsRequest = Optional.ofNullable(gson.fromJson(requestBody,
                                                                    FieldOptionsRequest.class));
            if (!fieldOptionsRequest.isPresent()) {
               throw new IllegalArgumentException("failed to parse request body");
            }
            log.info("requestBody deserialized as {}", fieldOptionsRequest);
            FieldOptionsRequest fieldOptionsRequestConcrete = fieldOptionsRequest.get();
            fieldOptionsRequestConcrete.setFieldKey(fieldKey);
            fieldOptionsRequestConcrete.setProjectKey(projectKey);
            fieldOptionsRequestConcrete.setIssueTypeId(issueTypeId);
            fieldOptionsRequestConcrete.setOptionId(optionId);
            return Optional.of(fieldOptionsRequestConcrete);
        } catch (Exception e) {
            log.error("failed to parse request parameters \"{}\" with error \"{}\"",
                      requestBody, e.getMessage());
            fieldOptionsRequest = Optional.empty();
        }
        return fieldOptionsRequest;
    }
}
