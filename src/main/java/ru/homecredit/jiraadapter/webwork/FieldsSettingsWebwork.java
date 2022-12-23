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
    private List<String> allUsers;
    private List<String> allowedUsers;
    private String[] usersToSave;
    private List<FieldsGroupSettings> fieldsGroupsSettings;

    @Override
    public String execute() throws Exception {
        super.execute();
        allCustomFieldsKeys = jiraAdapterSettingsService.getAllCustomFieldsKeys();
        allUsers = fieldsGroupsSettingsService.getAllUsers();
        log.info("got users");
        fieldsGroupsSettings = fieldsGroupsSettingsService.all();
        log.info("get settings objects {}", fieldsGroupsSettings);
        return "fields-groups-settings-page";
    }
    public void doSave() {
        log.info("saving {}", Arrays.toString(usersToSave));

    }


}
