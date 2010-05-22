
package net.haltcondition.anode;

import android.content.Context;
import android.content.SharedPreferences;


public class SettingsHelper
{
    // Common constants
    public static final String PREF_FILE = "settings";
    public static final String PREF_USERNAME = "username";
    public static final String PREF_PASSWORD = "password";
    public static final String PREF_SVCNAME = "svcname";
    public static final String PREF_SVCID = "svcid";

    private final Context ctx;

    public SettingsHelper(Context ctx)
    {
        this.ctx = ctx;
    }

    public void setAccount(Account acc)
    {
        SharedPreferences.Editor setedit = ctx.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE).edit();
        setedit.putString(PREF_USERNAME, acc.getUsername());
        setedit.putString(PREF_PASSWORD, acc.getPassword());
        setedit.commit();
    }

    public Account getAccount()
    {
        SharedPreferences settings = ctx.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        return new Account(settings.getString(PREF_USERNAME, ""),
                           settings.getString(PREF_PASSWORD, ""));
    }

    public void setService(Service svc)
    {
        SharedPreferences.Editor setedit = ctx.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE).edit();
        setedit.putString(PREF_SVCNAME, svc.getServiceName());
        setedit.putString(PREF_SVCID, svc.getServiceId());
        setedit.commit();
    }

    public Service getService()
    {
        SharedPreferences settings = ctx.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        return new Service(settings.getString(PREF_SVCNAME, ""),
                           settings.getString(PREF_SVCID, ""));        
    }

}
