package ru.homecredit.jiraadapter.service;

import ru.homecredit.jiraadapter.dto.FieldOptions;

public interface FieldOptionsService {

    /**
     * method to receive the given customfield options list with some other
     * parameters of that customfield in given context
     * @param fieldKey - jira key of customfield
     * @param projectKey - jira key of project
     * @param issueTypeId - id of issuetype (issutype combined with project defines
     *                    customfield context, that's why the customfield may
     *                    have different list of options in given project,
     *                    depending from issuetype this customfield field is
     *                    used in
     * @return FieldOptions DTO
     */
    FieldOptions getOptions(String fieldKey,
                                   String projectKey,
                                   String issueTypeId);

    /**
     * method that does the manipulation on field and option, received in POST
     * request body, and packs the result, errormessage, if any,
     * and some Jira properties to FieldOptions DTO
     * @param requestBody - string, received in request body by controller
     * @return FieldOptions DTO
     */
    FieldOptions postOption(String requestBody);
}
