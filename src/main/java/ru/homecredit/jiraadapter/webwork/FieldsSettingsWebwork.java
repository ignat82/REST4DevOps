package ru.homecredit.jiraadapter.webwork;

import com.atlassian.jira.web.action.JiraWebActionSupport;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.homecredit.jiraadapter.entities.FieldsGroupSettings;
import ru.homecredit.jiraadapter.service.JiraAdapterSettingsService;
import ru.homecredit.jiraadapter.service.SettingsServiceImpl;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@Slf4j
public class FieldsSettingsWebwork extends JiraWebActionSupport {
    private final JiraAdapterSettingsService jiraAdapterSettingsService;
    private final SettingsServiceImpl settingsService;
    private List<String> allCustomFieldsKeys;
    private List<String> savedCustomFieldsKeys;
    private String[] customFieldsKeysToSave;
    private List<String> allUsers;
    private List<String> savedUsers;
    private String[] usersToSave;
    private List<FieldsGroupSettings> currentSettings;
    private String groupID;
    private String description;
    private String message;

    @Override
    public String execute() throws Exception {
        super.execute();
        allCustomFieldsKeys = jiraAdapterSettingsService.getAllCustomFieldsKeys();
        allUsers = settingsService.getAllUsers();
        currentSettings = settingsService.all();
        log.info("get settings objects: {}", currentSettings);
        currentSettings.forEach(s -> log.info(settingsService.prettyString(s)));
        return "fields-groups-settings-page";
    }
    public void doSave() {
        log.info("saving settings group. fields {}, users{}",
                 customFieldsKeysToSave,
                 usersToSave);
        if (customFieldsKeysToSave == null || usersToSave == null) {
            message = "either fields or users are empty. won't save";
            log.info(message);
            return;
        }
        if (settingsService.settingsExist(customFieldsKeysToSave, usersToSave)) {
            message = "settings seems to exist already";
            log.info(message);
        } else {
            FieldsGroupSettings settingsToSave = settingsService
                    .add(description, customFieldsKeysToSave, usersToSave);
            log.info("saved settings {}", settingsToSave);
        }
    }

    public void doDelete() {
        log.info("deleting settings group {}", groupID);
        message = settingsService.delete(Integer.parseInt(groupID));
    }

    public void doEdit() {
        log.info("editing settings group {}", groupID);
        message = settingsService.edit(Integer.parseInt(groupID),
                                       description,
                                       customFieldsKeysToSave,
                                       usersToSave);
    }
}
