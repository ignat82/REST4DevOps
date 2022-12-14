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

    public String getRequest(String fieldKey, String projectKey, String issueTypeId) {
        FieldOptionsRequest fieldOptionsRequest = new FieldOptionsRequest(fieldKey,
                                                                          projectKey,
                                                                          issueTypeId);
        FieldOptions fieldOptions = fieldOptionsService.getOptions(fieldOptionsRequest);
        log.info("got options {}", fieldOptions.toString());
        return gson.toJson(new FieldOptionsResponse(fieldOptions));
    }

    public String postRequest(String optionId, String requestBody) {
        return postRequest(null,
                           null,
                           null,
                           optionId,
                           requestBody);
    }

    public String postRequest(String fieldKey,
                              String projectKey,
                              String issueTypeId,
                              String requestBody) {
        return postRequest(fieldKey,
                           projectKey,
                           issueTypeId,
                           null,
                           requestBody);
    }
    private String postRequest(String fieldKey,
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
            return gson.toJson(new FieldOptionsResponse(fieldOptions));
        } else {
            return "failed to parse request body";
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
