package net.haltcondition.anode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Copyright Steve Smith (tarkasteve@gmail.com): 16/05/2010
 */
public class Usage
{
    private Long totalQuota;
    private Date rollOver;
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
        this.totalQuota = Long.valueOf(str);
    }

    public Date getRollOver()
    {
        return rollOver;
    }

    public void setRollOver(String str)
        throws ParseException
    {
        rollOver = dateParser.parse(str);
    }

    public Long getUsed()
    {
        return used;
    }

    public void setUsed(String str)
    {
        this.used = Long.valueOf(used);
    }
}
