package net.haltcondition.anode;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Copyright Steve Smith (tarkasteve@gmail.com): 16/05/2010
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
public class Usage
{
    private Long totalQuota;
    private Calendar rollOver;
    private Long used;

    private SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd");

    public Usage()
    {
    }

    public Long getTotalQuota()
    {
        return totalQuota;
    }

    public void setTotalQuota(String str)
    {
        totalQuota = Long.valueOf(str);
    }

    public Calendar getRollOver()
    {
        return rollOver;
    }

    public void setRollOver(String str)
        throws ParseException
    {
        Date d = dateParser.parse(str);
        rollOver = new GregorianCalendar();
        rollOver.setTime(d);
        rollOver.setLenient(true);
    }

    public Long getUsed()
    {
        return used;
    }

    public void setUsed(String str)
    {
        used = Long.valueOf(str);
    }

    public Double getPercentageUsed()
    {
        return (used.doubleValue()) / (totalQuota.doubleValue()) * 100.0;
    }

    public Integer getDaysInPeriod()
    {
        Calendar c = (Calendar)rollOver.clone();

        // Assumption: Internode periods always cross a month boundary.
        // This seems reasonable, otherwise how would the roll-over e.g. periods
        // ending on the 31st?
        c.add(Calendar.MONTH, -1);
        return c.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    // Fucking static unmockable bastard ...
    Double getDaysIntoPeriod(Calendar now)
    {
        Calendar start = (Calendar)rollOver.clone();
        start.add(Calendar.MONTH, -1);

        Double ms = new Double(now.getTimeInMillis() - start.getTimeInMillis());
        return ms / 1000 / 60 / 60 / 24;
    }

    public Double getDaysIntoPeriod()
    {
        return getDaysIntoPeriod(Calendar.getInstance());
    }

    public Double getPercentageIntoPeriod()
    {
        return getDaysIntoPeriod() / getDaysInPeriod() * 100.0;
    }

    public Double getOptimalNow()
    {
        return getTotalQuota() / getDaysInPeriod() * getDaysIntoPeriod();
    }

}
