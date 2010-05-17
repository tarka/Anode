package net.haltcondition.anode;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;

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

    private ExecutorService pool = Executors.newFixedThreadPool(2);

    private RemoteViews views;

    @Override
    public boolean handleMessage(Message msg) {
        Log.i(TAG, "Got message: "+msg.what);

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


    private void setUsage(Usage usage)
    {
        views.setProgressBar(R.id.widget_progress, 100, (int)usage.getPercentageUsed(), false);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
        Log.i(TAG, "Running update");

        // FIXME: Needs to be thread-safe? This should just bea 
        views = new RemoteViews(context.getPackageName(), R.layout.widget);

        DbHelper db = new DbHelper(context);
        Account account = db.getAccount();
        Service service = db.getService();
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

    }
}
