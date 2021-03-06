
package net.haltcondition.anode;

import android.app.AlarmManager;
import android.content.Context;
import android.content.SharedPreferences;


/**
 * Copyright Steve Smith (tarkasteve@gmail.com): 2010
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
public class SettingsHelper
{
    // Common constants
    public static final String PREF_FILE = "settings";
    public static final String PREF_USERNAME = "username";
    public static final String PREF_PASSWORD = "password";
    public static final String PREF_SVCNAME = "svcname";
    public static final String PREF_SVCID = "svcid";
    public static final String PREF_UPDATE = "updatefreq";
    public static final String PREF_WIFI_ONLY = "wifi_only";

    private final SharedPreferences settings;

    public SettingsHelper(Context ctx)
    {
        settings = ctx.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
    }

    public SharedPreferences getSharedPreferences()
    {
        return settings;
    }

    public boolean hasSettings()
    {
        return settings.contains(PREF_USERNAME);
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

    public boolean getWifiOnly()
    {
        return settings.getBoolean(PREF_WIFI_ONLY, false);
    }

    // All in one so we only send one update
    public void setAll(Account account, Service svc, long updateInterval, boolean wifi_only)
    {
        SharedPreferences.Editor setedit = settings.edit();

        setedit.putString(PREF_USERNAME, account.getUsername());
        setedit.putString(PREF_PASSWORD, account.getPassword());

        setedit.putString(PREF_SVCNAME, svc.getServiceName());
        setedit.putString(PREF_SVCID, svc.getServiceId());

        setedit.putLong(PREF_UPDATE, updateInterval);

        setedit.putBoolean(PREF_WIFI_ONLY, wifi_only);

        setedit.commit();
    }

}
