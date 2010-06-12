package net.haltcondition.anode;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Copyright Steve Smith (tarkasteve@gmail.com): 12/05/2010
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
public class WidgetUpdater
    extends AppWidgetProvider
    implements Handler.Callback
{
    private static final String TAG = "Updater";

    private ExecutorService pool = Executors.newSingleThreadExecutor();

    // Save for when workers return
    private Context ctx;

    @Override
    public void onEnabled(Context context)
    {
        super.onEnabled(context);

        Log.d(TAG, "DOING ENABLED");

        makeClickable(context);

        SettingsHelper settings = new SettingsHelper(context);
        if (settings.hasSettings()) {
            initiateUsageUpdate(context);
            setAlarm(context);
        } else {
            Intent intent = new Intent(context, EditSettings.class);
            PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
            try {
                pi.send();
            } catch (PendingIntent.CanceledException e) {
                Log.e(TAG, "Failed to send initial config intent");
            }
        }
    }

    private void setAlarm(Context context)
    {
        Log.d(TAG, "Setting Alarm");

        SettingsHelper settings = new SettingsHelper(context);

        Intent i = new Intent(Common.USAGE_ALARM);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarm.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis(), settings.getUpdateInterval(), pi);
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equals(Common.USAGE_ALARM)) {
            Log.d(TAG, "Got usage update alarm");
            initiateUsageUpdate(context);

        } else if (intent.getAction().equals(Common.SETTINGS_UPDATE)) {
            Log.d(TAG, "Got settings update broadcast");
            initiateUsageUpdate(context);
            setAlarm(context);

        } else if (intent.getAction().equals(Common.USAGE_UPDATE)) {
            Log.d(TAG, "Got usage update broadcast");
            // FIXME: Needs parcelable usage
            //setUsage((Usage)intent.getSerializableExtra(Common.USAGE_UPDATE));

        } else {
            super.onReceive(context, intent);
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        Log.d(TAG, "Got message: "+ msg.what);

        if (msg.what == HttpWorker.MsgCode.ENDMSG.ordinal()) {
            // Not used
            Log.w(TAG, "Got unused ENDMSG");

        } else if (msg.what == HttpWorker.MsgCode.UPDATEMSG.ordinal()) {
            // Not used, just logged for now
            Log.d(TAG, "Update: "+msg.obj);

        } else if (msg.what == HttpWorker.MsgCode.ERRORMSG.ordinal()) {
            Log.e(TAG, "Error: "+msg.obj);
            // FIXME: Display somehow

        } else if (msg.what == HttpWorker.MsgCode.RESULT.ordinal()) {
            Log.d(TAG, "Got Result");
            setUsage((Usage)msg.obj);
        }

        return true;
    }


    private void setUsage(Usage usage)
    {
        if (ctx == null) {
            Log.d(TAG, "No context, can't update widget");
            return;
        }
        Log.d(TAG, "Updating widget");


        // Update vals
        RemoteViews views = new RemoteViews(ctx.getPackageName(), R.layout.widget);

        final NumberFormat oneDP = new DecimalFormat("#0.0");
        final NumberFormat noDP = new DecimalFormat("#0");

        Long total = usage.getTotalQuota() / Common.GB;
        double used = usage.getUsed().doubleValue() / Common.GB;
        int pcUsed = usage.getPercentageUsed().intValue();
        double diff = usage.getOptimalNow() - usage.getUsed();
        double adiff = Math.abs(diff / Common.GB);

        // total = 200L;
        // used = 200;
        // pcUsed = 100;
        // diff = 200;
        // adiff = 200;

        if (pcUsed > 99) {
            pcUsed = 100;
            // Slightly reduce text to ensure room
            views.setFloat(R.id.widget_usedpc, "setTextSize", 36f);
        }

        // Don't bother with decimal for larger numbers, we need the space
        String usedfmt = (total > 99 || used > 99) ? noDP.format(used) : oneDP.format(used);
        String difffmt = (adiff > 99) ? noDP.format(adiff) : oneDP.format(adiff);

        views.setTextViewText(R.id.widget_usedpc, pcUsed+"%");

        views.setTextViewText(R.id.widget_total, total.toString());
        views.setTextViewText(R.id.widget_used, usedfmt);

        views.setTextViewText(R.id.widget_quotalevel, difffmt);
        views.setTextViewText(R.id.widget_overunder, diff > 0 ? "under" : "over");

        views.setProgressBar(R.id.widget_progress, 100, pcUsed, false);


        AppWidgetManager awm = AppWidgetManager.getInstance(ctx);
        int[] ids = awm.getAppWidgetIds(new ComponentName(ctx, WidgetUpdater.class));
        awm.updateAppWidget(ids, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
        Log.d(TAG, "Running update");

        // FIXME: Is this OK?
        ctx = context;

        makeClickable(context, appWidgetManager, appWidgetIds);
        initiateUsageUpdate(context);
    }

    private void makeClickable(Context context)
    {
        AppWidgetManager awm = AppWidgetManager.getInstance(context);
        makeClickable(context, awm, awm.getAppWidgetIds(new ComponentName(context, WidgetUpdater.class)));
    }

    private void makeClickable(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
        // Make clickable
        Intent intent = new Intent(context, EditSettings.class);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widget_layout, pi);
        appWidgetManager.updateAppWidget(appWidgetIds, views);
    }

    private void initiateUsageUpdate(Context context)
    {
        this.ctx = context;

        // Delegate fetch to thread
        SettingsHelper settings = new SettingsHelper(context);
        ConnectivityManager cmgr= (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (!cmgr.getBackgroundDataSetting() ||
            (settings.getWifiOnly() && cmgr.getActiveNetworkInfo().getType() != ConnectivityManager.TYPE_WIFI))
        {
            Log.i(TAG, "Skipping as background sync disabled or WiFi not enabled");
            return;
        }

        Account account = settings.getAccount();
        Service service = settings.getService();

        if (account == null || service == null) {
            Log.w(TAG, "Account or Service not available, doing nothing");
            return;
        }

        HttpWorker<Usage> usageWorker =
            new HttpWorker<Usage>(new Handler(this), account,
                                  Common.usageUri(service), new UsageParser());
        pool.execute(usageWorker);
    }
}
