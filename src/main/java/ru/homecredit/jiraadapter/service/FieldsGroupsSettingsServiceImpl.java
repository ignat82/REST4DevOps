package ru.homecredit.jiraadapter.service;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.bc.user.search.UserSearchParams;
import com.atlassian.jira.bc.user.search.UserSearchService;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import lombok.extern.slf4j.Slf4j;
import ru.homecredit.jiraadapter.entities.FieldsGroupSettings;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Named
@Slf4j
public class FieldsGroupsSettingsServiceImpl implements FieldsGroupsSettingsService {
private final ActiveObjects activeObjects;
private final UserSearchService userSearchService;
    private final UserSearchParams userSearchParams
            = (new UserSearchParams.Builder())
            .allowEmptyQuery(true)
            .includeActive(true)
            .includeInactive(true)
            .maxResults(100000).build();

    @Inject
    public FieldsGroupsSettingsServiceImpl(@ComponentImport ActiveObjects activeObjects,
                                           @ComponentImport UserSearchService userSearchService) {
        this.activeObjects = activeObjects;
        this.userSearchService = userSearchService;
    }

    public FieldsGroupSettings add(String[] fieldsKeys, String[] usersKeys) {
        final FieldsGroupSettings newSettingsGroup = activeObjects.create(FieldsGroupSettings.class);
        newSettingsGroup.setFieldsKeys(fieldsKeys);
        newSettingsGroup.setUsersKeys(usersKeys);
        newSettingsGroup.save();
        return newSettingsGroup;
    }

    public List<FieldsGroupSettings> all() {
        log.error("retrying settings");
        return Arrays.asList(activeObjects.find(FieldsGroupSettings.class));
    }

    public List<String> getAllUsers() {
        return userSearchService
                .findUsers("", userSearchParams).stream()
                .map(ApplicationUser::getKey)
                .collect(Collectors.toList());
    }
}
