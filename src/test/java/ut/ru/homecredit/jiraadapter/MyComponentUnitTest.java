package ut.ru.homecredit.jiraadapter;

import org.junit.Test;
import ru.homecredit.jiraadapter.api.MyPluginComponent;
import ru.homecredit.jiraadapter.impl.MyPluginComponentImpl;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest
{
    @Test
    public void testMyName()
    {
        MyPluginComponent component = new MyPluginComponentImpl(null, null,
                null, null, null);
        assertEquals("names do not match!", "myComponent",component.getName());
    }
}
