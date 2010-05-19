package net.haltcondition.anode;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.math.BigDecimal;
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

    @Test
    void testQuotaParse()
    {
        usage.setTotalQuota("120000000000");
        assertEquals(usage.getTotalQuota().equals(120000000000L), true);
    }

    @Test
    void testUsageParse()
    {
        usage.setUsed("120000000000");
        assertEquals(usage.getUsed().equals(120000000000L), true);
    }

    @Test
    void testPCUsed()
    {
        usage.setTotalQuota("100");
        usage.setUsed("25");
        assertEquals(Math.abs(usage.getPercentageUsed() - 25.0) < 0.01, true);
    }

    @Test
    void testDaysFeb()
    {
        try {
            usage.setRollOver("2010-03-21");
        } catch (ParseException e) {
            fail(e.getMessage(), e);
        }
        assertEquals((int)usage.getDaysInPeriod(), 28);
    }
}