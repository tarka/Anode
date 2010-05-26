package net.haltcondition.anode;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
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
 */
public class WidgetUpdater
    extends AppWidgetProvider
    implements Handler.Callback
{

    private static final String TAG = "Updater";
    private static final Long GB = 1000000000L;

    private static final String USAGE_ACTON = "net.haltcondition.anode.USAGE_BROADCAST";

    private ExecutorService pool = Executors.newSingleThreadExecutor();

    // Save for when workers return
    private Context ctx;
    private AppWidgetManager mgr;
    private int[] widgetIds;

    @Override
    public void onEnabled(Context context)
    {
        super.onEnabled(context);

        Log.i(TAG, "DOING ENABLED");

        AlarmManager alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        Intent i = new Intent(USAGE_ACTON);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);

        alarm.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, pi);
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (! intent.getAction().equals(USAGE_ACTON)) {
            super.onReceive(context, intent);
            return;
        }

        Log.i(TAG, "Got usage update broadcast action");

        AppWidgetManager awm = AppWidgetManager.getInstance(context);
        onUpdate(context, awm, awm.getAppWidgetIds(new ComponentName(context, WidgetUpdater.class)));

    }

    @Override
    public boolean handleMessage(Message msg) {
        Log.i(TAG, "Got message: "+ msg.what);

        if (msg.what == HttpWorker.MsgCode.ENDMSG.ordinal()) {
            // Not used
            Log.w(TAG, "Got unused ENDMSG");

        } else if (msg.what == HttpWorker.MsgCode.UPDATEMSG.ordinal()) {
            // Not used, just logged for now
            Log.i(TAG, "Update: "+msg.obj);

        } else if (msg.what == HttpWorker.MsgCode.ERRORMSG.ordinal()) {
            Log.e(TAG, "Error: "+msg.obj);
            // FIXME: Display somehow

        } else if (msg.what == HttpWorker.MsgCode.RESULT.ordinal()) {
            Log.i(TAG, "Result: "+msg.obj);
            setUsage((Usage)msg.obj);
        }

        return true;
    }

    
    private static final NumberFormat oneDP = new DecimalFormat("#0.0");

    private void setUsage(Usage usage)
    {
        for (int id: widgetIds) {
            RemoteViews views = new RemoteViews(ctx.getPackageName(), R.layout.widget);

            // Update vals
            Double diff = usage.getOptimalNow() - usage.getUsed();

            views.setProgressBar(R.id.widget_progress, 100, usage.getPercentageUsed().intValue(), false);

            views.setTextViewText(R.id.widget_usedpc, usage.getPercentageUsed().intValue()+"%");

            views.setTextViewText(R.id.widget_total, ((Long)(usage.getTotalQuota()/GB)).toString());
            views.setTextViewText(R.id.widget_used, oneDP.format(usage.getUsed() / GB));

            views.setTextViewText(R.id.widget_quotalevel, oneDP.format(diff / GB));
            views.setTextViewText(R.id.widget_overunder, diff > 0 ? "under" : "over");

            mgr.updateAppWidget(id, views);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
        Log.i(TAG, "Running update");

        // FIXME: Is this OK to do?
        ctx = context;
        mgr = appWidgetManager;
        widgetIds = appWidgetIds;

        // Delegate fetch to thread
        SettingsHelper settings = new SettingsHelper(context);
        Account account = settings.getAccount();
        Service service = settings.getService();

        if (account == null || service == null) {
            Log.w(TAG, "Account or Service not available, doing nothing");
            return;
        }

        String uri = context.getResources().getString(R.string.inode_api_url)
                     + service.getServiceId()
                     + "/usage";

        UsageParser parser = new UsageParser();

        HttpWorker<Usage> usageWorker =
            new HttpWorker<Usage>(new Handler(this), account, uri, parser);

        pool.execute(usageWorker);

        // Make clickable
        Intent intent = new Intent(context, EditSettings.class);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widget_layout, pi);
        appWidgetManager.updateAppWidget(appWidgetIds, views);
    }
}
