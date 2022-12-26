package ut.ru.homecredit.jiraadapter.webwork;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import ru.homecredit.jiraadapter.webwork.ConfigurationWebworkAction;

import static org.junit.Assert.assertEquals;

/**
 * @since 3.5
 */
@NoArgsConstructor
@Slf4j
@RunWith(MockitoJUnitRunner.class)
public class ConfigurationWebworkActionTest {
    private ConfigurationWebworkAction configurationWebworkAction;

//    @Mock
//    JiraAdapterSettingsServiceImpl jiraAdapterSettingsService;

    @BeforeEach
    public void setup() {
        log.error("preparing tests");
        configurationWebworkAction = new ConfigurationWebworkAction();//jiraAdapterSettingsService);
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testSomething() throws Exception {
        assertEquals(configurationWebworkAction.execute(),
                     "configuration-page",
                     "webwork returns wrong template");
        log.error("test error message");
        log.info("test info message");
    }

}
