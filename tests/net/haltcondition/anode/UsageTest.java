/**
 * Copyright Steve Smith (tarkasteve@gmail.com): 30/05/2010
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as
 * published by the Free Software Foundation..
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package net.haltcondition.anode;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;

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

    @Test
    void testDaysFebLeap()
    {
        try {
            usage.setRollOver("2008-03-21");
        } catch (ParseException e) {
            fail(e.getMessage(), e);
        }
        assertEquals((int)usage.getDaysInPeriod(), 29);
    }

    @Test
    void testDaysCrossYears()
    {
        try {
            usage.setRollOver("2008-01-21");
        } catch (ParseException e) {
            fail(e.getMessage(), e);
        }
        assertEquals((int)usage.getDaysInPeriod(), 31);
    }

    @Test
    void testDaysInWhole()
    {
        try {
            usage.setRollOver("2008-05-21");
        } catch (ParseException e) {
            fail(e.getMessage(), e);
        }
        Calendar now = new GregorianCalendar(2008, Calendar.APRIL, 22);

        Double in = usage.getDaysIntoPeriod(now);
        assertEquals(in, 1.0);
    }

    @Test
    void testDaysInPartial()
    {
        try {
            usage.setRollOver("2008-05-21");
        } catch (ParseException e) {
            fail(e.getMessage(), e);
        }
        Calendar now = new GregorianCalendar(2008, Calendar.MAY, 20, 12, 0);

        Double in = usage.getDaysIntoPeriod(now);
        assertEquals(in, 29.5);
    }
}