package ru.homecredit.jiraadapter.impl;

import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.ApplicationProperties;
import lombok.extern.slf4j.Slf4j;
import ru.homecredit.jiraadapter.api.MyPluginComponent;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * class is used just to import all the necessary Jira beans with @ComponentImport
 * and put these beans with single @Inject annotation in other classes
 */
@ExportAsService ({MyPluginComponent.class})
@Named ("myPluginComponent")
@Slf4j
public class MyPluginComponentImpl implements MyPluginComponent {
    private final ApplicationProperties applicationProperties;

    /**
     * constructor to pick the necessary beans from Jira application
     */
    @Inject
    public MyPluginComponentImpl(@ComponentImport ApplicationProperties applicationProperties) {
        log.trace("creating MyPluginComponentImpl instance");
        this.applicationProperties = applicationProperties;
    }

    public String getName() {
        if(applicationProperties != null) {
            return "myComponent:" + applicationProperties.getDisplayName();
        }
        return "myComponent";
    }
}
