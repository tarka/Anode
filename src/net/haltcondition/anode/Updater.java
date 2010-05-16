package net.haltcondition.anode;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Copyright Steve Smith (tarkasteve@gmail.com): 12/05/2010
 */
public class Updater
    extends AppWidgetProvider
    implements Handler.Callback
{

    private static final String TAG = "Updater";

    private ExecutorService pool = Executors.newFixedThreadPool(2);


    @Override
    public boolean handleMessage(Message msg) {
        Log.i(TAG, "Got message: "+msg.what);

        if (msg.what == HttpWorker.MsgCode.ENDMSG.ordinal()) {
            // Not used
            Log.w(TAG, "Got unused ENDMSG");

        } else if (msg.what == HttpWorker.MsgCode.UPDATEMSG.ordinal()) {
            // Not used, just logged for now
            Log.i(TAG, "Update: "+msg.obj);

        } else if (msg.what == HttpWorker.MsgCode.RESULT.ordinal()) {
            // Not used, just logged for now
            Log.i(TAG, "Update: "+msg.obj);
        }

        return true;
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
        Log.i(TAG, "Running update");

        DbHelper db = new DbHelper(context);
        Account account = db.getAccount();
        if (account == null) {
            Log.w(TAG, "No account available, doing nothing");
        }

        String uri = context.getResources().getString(R.string.inode_api_url);
        ServiceParser parser = new ServiceParser();

        HttpWorker<Service> serviceWorker =
            new HttpWorker<Service>(new Handler(this), account, uri, parser);

        pool.execute(serviceWorker);

    }
}
