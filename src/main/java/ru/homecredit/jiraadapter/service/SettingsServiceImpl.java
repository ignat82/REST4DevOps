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
import java.util.Optional;
import java.util.stream.Collectors;

@Named
@Slf4j
public class SettingsServiceImpl implements SettingsService {
private final ActiveObjects activeObjects;
private final UserSearchService userSearchService;
    private final UserSearchParams userSearchParams
            = (new UserSearchParams.Builder())
            .allowEmptyQuery(true)
            .includeActive(true)
            .includeInactive(true)
            .maxResults(100000).build();

    @Inject
    public SettingsServiceImpl(@ComponentImport ActiveObjects activeObjects,
                               @ComponentImport UserSearchService userSearchService) {
        this.activeObjects = activeObjects;
        this.userSearchService = userSearchService;
    }

    public FieldsGroupSettings add(String description, String[] fieldsKeys, String[] usersKeys) {
        final FieldsGroupSettings newSettingsGroup = activeObjects.create(FieldsGroupSettings.class);
        newSettingsGroup.setDescription(description);
        newSettingsGroup.setFieldsKeys(Arrays.toString(fieldsKeys));
        newSettingsGroup.setUsersKeys(Arrays.toString(usersKeys));
        newSettingsGroup.save();
        return newSettingsGroup;
    }

    public List<FieldsGroupSettings> all() {
        log.info("retrying settings");
        return Arrays.asList(activeObjects.find(FieldsGroupSettings.class));
    }

    public String delete(int id) {
        Optional<FieldsGroupSettings> groupToDelete = getById(id);
        String message;
        if (groupToDelete.isPresent()) {
            activeObjects.delete(groupToDelete.get());
            message = String.format("deleted group with id %s", id);
            log.info(message);
        } else {
            message = String.format("failed to acquire group with id %s", id);
            log.error(message);
        }
        return message;
    }

    public String edit(int id, String description, String[] fieldsKeys, String[] usersKeys) {
        Optional<FieldsGroupSettings> groupToEdit = getById(id);
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
        log.error(message);
        return message;
    }

    public List<String> getAllUsers() {
        return userSearchService
                .findUsers("", userSearchParams).stream()
                .map(ApplicationUser::getKey)
                .collect(Collectors.toList());
    }

    public Optional<FieldsGroupSettings> getById(int id) {
        return all().stream().filter(s -> s.getID() == id).findAny();
    }

    public boolean settingsExist(String[] fieldsKeysArr, String[] usersKeysArr) {
        log.info("looking if these settings exist already");
        Arrays.sort(fieldsKeysArr);
        Arrays.sort(usersKeysArr);
        return all().stream()
                    .anyMatch(s -> s.getFieldsKeys().equals(Arrays.toString(fieldsKeysArr))
                              && s.getUsersKeys().equals(Arrays.toString(usersKeysArr)));
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
