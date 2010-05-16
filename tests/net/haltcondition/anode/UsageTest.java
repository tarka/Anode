package net.haltcondition.anode;

import junit.framework.TestCase;
import net.haltcondition.anode.Usage;

import java.lang.Exception;
import java.lang.Override;
import java.util.Calendar;

public class UsageTest
    extends TestCase
{
    private Usage usage;

    @Override
    protected void setUp() throws Exception
    {
        usage = new Usage();
    }

    void testDateParse(){
        usage.setRollOver("2000-05-21");
        assertEquals(usage.getRollOver().get(Calendar.YEAR), 2000);
        assertEquals(usage.getRollOver().get(Calendar.MONTH), 5);
        assertEquals(usage.getRollOver().get(Calendar.DAY_OF_MONTH), 21);
    }
}