package ru.homecredit.jiraadapter.service;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.bc.user.search.UserSearchParams;
import com.atlassian.jira.bc.user.search.UserSearchService;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import lombok.extern.slf4j.Slf4j;
import ru.homecredit.jiraadapter.entities.FieldsGroupSettings;
import ru.homecredit.jiraadapter.entities.FieldsGroupSettingsRaw;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Named
@Slf4j
public class SettingsServiceImpl implements SettingsService {
private final ActiveObjects activeObjects;
private final UserSearchService userSearchService;
private final CustomFieldManager customFieldManager;
private final JiraAuthenticationContext jiraAuthenticationContext;
private final UserSearchParams userSearchParams
        = (new UserSearchParams.Builder())
        .allowEmptyQuery(true)
        .includeActive(true)
        .includeInactive(true)
        .maxResults(100000).build();

    @Inject
    public SettingsServiceImpl(@ComponentImport ActiveObjects activeObjects,
                               @ComponentImport UserSearchService userSearchService,
                               @ComponentImport CustomFieldManager customFieldManager,
                               @ComponentImport JiraAuthenticationContext jiraAuthenticationContext) {
        this.activeObjects = activeObjects;
        this.userSearchService = userSearchService;
        this.customFieldManager = customFieldManager;
        this.jiraAuthenticationContext = jiraAuthenticationContext;
    }

    public String add(String description, String[] fieldsKeys, String[] usersKeys) {
        String message;
        if (fieldsKeys == null || usersKeys == null) {
            message = "either fields or users are empty. won't save";
            log.error(message);
        } else if (settingsExist(fieldsKeys, usersKeys)) {
            message = "settings seems to exist already";
            log.error(message);
        } else {
            final FieldsGroupSettingsRaw newSettingsGroup = activeObjects.create(FieldsGroupSettingsRaw.class);
            newSettingsGroup.setDescription(description);
            newSettingsGroup.setFieldsKeys(Arrays.toString(fieldsKeys));
            newSettingsGroup.setUsersKeys(Arrays.toString(usersKeys));
            newSettingsGroup.save();
            message = String.format("saved settings group %s", newSettingsGroup.getID());
            log.info(message);
        }
        return message;
    }

    public List<FieldsGroupSettings> all() {
        log.info("retrying settings");
        return Arrays.asList(activeObjects.find(FieldsGroupSettingsRaw.class)).stream()
                .map(r -> new FieldsGroupSettings(r.getID(),
                                                  r.getDescription(),
                                                  Arrays.asList(r.getFieldsKeys()
                                                                 .replace("[", "")
                                                                 .replace("]", "")
                                                                 .split(", ")),
                                                  Arrays.asList(r.getUsersKeys()
                                                                 .replace("[", "")
                                                                 .replace("]", "")
                                                                 .split(", "))))
                .collect(Collectors.toList());
    }

    public String delete(int id) {
        Optional<FieldsGroupSettingsRaw> groupToDelete = getById(id);
        String message;
        if (groupToDelete.isPresent()) {
            activeObjects.delete(groupToDelete.get());
            message = String.format("deleted settings group %s", id);
            log.info(message);
        } else {
            message = String.format("failed to acquire group with id %s", id);
            log.error(message);
        }
        return message;
    }

    public String edit(int id,
                       String description,
                       String[] fieldsKeys,
                       String[] usersKeys) {
        Optional<FieldsGroupSettingsRaw> groupToEdit = getById(id);
        if (!groupToEdit.isPresent()) {
            String message = String.format("failed to acquire group with id %s", id);
            log.error(message);
            return message;
        }
        groupToEdit.get().setDescription(description);
        groupToEdit.get().setFieldsKeys(Arrays.toString(fieldsKeys));
        groupToEdit.get().setUsersKeys(Arrays.toString(usersKeys));
        groupToEdit.get().save();
        String message = String.format("saved changes to group with id %s", id);
        log.info(message);
        return message;
    }

    public List<String> getAllCustomFieldsKeys() {
        return customFieldManager.getCustomFieldObjects().stream()
                                 .map(CustomField::getId).collect(Collectors.toList());
    }

    public List<String> getAllUsers() {
        return userSearchService
                .findUsers("", userSearchParams).stream()
                .map(ApplicationUser::getKey)
                .collect(Collectors.toList());
    }

    public Optional<FieldsGroupSettingsRaw> getById(int id) {
        return Arrays.stream(activeObjects.find(FieldsGroupSettingsRaw.class))
                     .filter(s -> s.getID() == id).findAny();
    }

    public boolean isPermittedToEditByCurrentUser(String fieldKey) {
        String currentUser = jiraAuthenticationContext.getLoggedInUser().getKey();
        log.info("current user is {}", currentUser);
        return all().stream().anyMatch(s -> s.getFieldsKeys().contains(fieldKey) &&
                s.getUsersKeys().contains(currentUser));
    }

    public boolean settingsExist(String[] fieldsKeysArr, String[] usersKeysArr) {
        log.info("looking if these settings exist already");
        Arrays.sort(fieldsKeysArr);
        Arrays.sort(usersKeysArr);
        return all().stream()
                    .anyMatch(s -> s.getFieldsKeys().toString().equals(Arrays.toString(fieldsKeysArr))
                              && s.getUsersKeys().toString().equals(Arrays.toString(usersKeysArr)));
    }

    public String prettyString(FieldsGroupSettings settings) {
        String output = String.format("ID - %s\n, fields - %s\n, keys - %s\n",
                                      settings.getID(),
                                      settings.getDescription(),
                                      settings.getFieldsKeys(),
                                      settings.getUsersKeys());
        return output;
    }
}
