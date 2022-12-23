package ru.homecredit.jiraadapter.webwork;

import com.atlassian.jira.web.action.JiraWebActionSupport;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.homecredit.jiraadapter.entities.FieldsGroupSettings;
import ru.homecredit.jiraadapter.service.FieldsGroupsSettingsServiceImpl;
import ru.homecredit.jiraadapter.service.JiraAdapterSettingsService;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@Slf4j
public class FieldsSettingsWebwork extends JiraWebActionSupport {
    private final JiraAdapterSettingsService jiraAdapterSettingsService;
    private final FieldsGroupsSettingsServiceImpl fieldsGroupsSettingsService;
    private List<String> allCustomFieldsKeys;
    private List<String> savedCustomFieldsKeys;
    private String[] customFieldsKeysToSave;
    private List<String> allUsers;
    private List<String> savedUsers;
    private String[] usersToSave;
    private List<FieldsGroupSettings> fieldsGroupsSettings;

    @Override
    public String execute() throws Exception {
        super.execute();
        allCustomFieldsKeys = jiraAdapterSettingsService.getAllCustomFieldsKeys();
        allUsers = fieldsGroupsSettingsService.getAllUsers();
        fieldsGroupsSettings = fieldsGroupsSettingsService.all();
        log.info("get settings objects {}", fieldsGroupsSettings);
        return "fields-groups-settings-page";
    }
    public void doSave() {
        log.info("saving fields {} and users{}", customFieldsKeysToSave, Arrays.toString(usersToSave));
        if (customFieldsKeysToSave == null || usersToSave == null) {
            log.info("either fields or users are empty. won't save");
            return;
        }
        Arrays.sort(customFieldsKeysToSave);
        Arrays.sort(usersToSave);
        String fields = Arrays.toString(customFieldsKeysToSave);
        String users = Arrays.toString(usersToSave);
        if (fieldsGroupsSettingsService.settingsExist(fields, users)) {
            log.info("settings seems to exist already");
        } else {
            FieldsGroupSettings settingsToSave = fieldsGroupsSettingsService
                    .add(Arrays.toString(customFieldsKeysToSave), Arrays.toString(usersToSave));
            log.info("saved settings {}", settingsToSave);
        }
    }


}