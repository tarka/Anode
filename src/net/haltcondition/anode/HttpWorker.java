package net.haltcondition.anode;

import android.os.Handler;
import android.util.Log;

/**
 * Copyright Steve Smith (tarkasteve@gmail.com): 08/05/2010
 */
public class HttpWorker
    extends ThreadWorker
{
    private static final String TAG = "HttpWorker";

    private Account account;
    private String url;

    public HttpWorker(Handler h, Account acc, String url)
    {
        super(h);
        this.account = acc;
        this.url = url;
    }

    public void run()
    {
        Log.i(TAG, "Running");
    }
}
