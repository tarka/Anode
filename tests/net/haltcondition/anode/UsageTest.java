package net.haltcondition.anode;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.text.ParseException;
import java.util.Calendar;

@Test
public class UsageTest
{
    private Usage usage;

    @BeforeMethod
    protected void setUp()
    {
        usage = new Usage();
    }

    @Test
    void testDateParse(){
        try {
            usage.setRollOver("2000-12-21");
        } catch (ParseException e) {
            fail(e.getMessage(), e);
        }
        assertEquals(usage.getRollOver().get(Calendar.YEAR), 2000);
        assertEquals(usage.getRollOver().get(Calendar.MONTH), Calendar.DECEMBER);
        assertEquals(usage.getRollOver().get(Calendar.DAY_OF_MONTH), 21);
    }
}