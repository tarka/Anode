
package net.haltcondition.anode;

import android.app.AlarmManager;
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
    public static final String PREF_UPDATE = "updatefreq";

    private final SharedPreferences settings;

    public SettingsHelper(Context ctx)
    {
        settings = ctx.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
    }

    public SharedPreferences getSharedPreferences()
    {
        return settings;
    }

    public void setAccount(Account acc)
    {
        SharedPreferences.Editor setedit = settings.edit();
        setedit.putString(PREF_USERNAME, acc.getUsername());
        setedit.putString(PREF_PASSWORD, acc.getPassword());
        setedit.commit();
    }

    public Account getAccount()
    {
        return new Account(settings.getString(PREF_USERNAME, ""),
                           settings.getString(PREF_PASSWORD, ""));
    }

    public void setService(Service svc)
    {
        SharedPreferences.Editor setedit = settings.edit();
        setedit.putString(PREF_SVCNAME, svc.getServiceName());
        setedit.putString(PREF_SVCID, svc.getServiceId());
        setedit.commit();
    }

    public Service getService()
    {
        return new Service(settings.getString(PREF_SVCNAME, ""),
                           settings.getString(PREF_SVCID, ""));        
    }

    // FIXME: Is storing the AlarmManager const OK?
    public long getUpdateInterval()
    {
        return settings.getLong(PREF_UPDATE, AlarmManager.INTERVAL_HOUR);
    }

    public void setUpdateInterval(long interval)
    {
        SharedPreferences.Editor setedit = settings.edit();
        setedit.putLong(PREF_UPDATE, interval);
        setedit.commit();
    }

    // All in one so we only send one update
    public void setAll(Account account, Service svc, long updateInterval)
    {
        SharedPreferences.Editor setedit = settings.edit();

        setedit.putString(PREF_USERNAME, account.getUsername());
        setedit.putString(PREF_PASSWORD, account.getPassword());

        setedit.putString(PREF_SVCNAME, svc.getServiceName());
        setedit.putString(PREF_SVCID, svc.getServiceId());

        setedit.putLong(PREF_UPDATE, updateInterval);

        setedit.commit();
    }

}
