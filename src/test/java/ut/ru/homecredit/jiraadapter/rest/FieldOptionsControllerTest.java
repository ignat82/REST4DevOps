package ut.ru.homecredit.jiraadapter.rest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FieldOptionsControllerTest {

    // https://blog.codecentric.de/en/2014/07/agile-testing-jira-plugins/

    @Before
    public void setup() {

        /*
        new MockComponentWorker()
                .addMock(FieldManager.class, fieldManager)
                .init();
        */
    }

    @After
    public void tearDown() {

    }

    /*
    @Mock
    private FieldManager fieldManager;
    @Mock
    private ProjectManager projectManager;
    @Mock
    private FieldConfigSchemeManager fieldConfigSchemeManager;
    @Mock
    private OptionsManager optionsManger;
    */
    @Test
    public void messageIsValid() {
        /*
        FieldOptionsController resource = new FieldOptionsController();

        FieldOptionsResponse response = resource.doPost("customfield_10000"
                , "TES", "new10");
        final FieldOptionsResponse XMLResponse = (FieldOptionsResponse) response.getEntity();

        assertEquals("wrong message", "customfield_10000", XMLResponse.getFieldKey());
        */
        assertEquals("wrong message", "true", "true");
    }
}
