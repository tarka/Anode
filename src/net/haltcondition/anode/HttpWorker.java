package net.haltcondition.anode;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

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


    public String fetch(Handler hdl, XmlHandler xmlHandler)
    {
        HttpClient client = new DefaultHttpClient();
        String ret = "!!!!! Fail !!!!!";


        try {
            Log.d(TAG, "performing get " + url );

            Message msg = hdl.obtainMessage(1);
            msg.obj = "Fetching page ...";
            hdl.sendMessage(msg);

            HttpGet method = new HttpGet( new URI(url) );
            HttpResponse response = client.execute(method);
            if ( response != null ) {
                //ret = responseToString(response);
                //Log.i(TAG, "received " + ret );

                msg = hdl.obtainMessage(1);
                msg.obj = "Parsing XML ...";
                hdl.sendMessage(msg);

                xmlHandler.doParse(response.getEntity().getContent());
            } else {
                Log.i(TAG, "got a null response" );
            }

        } catch (IOException e) {
            Log.e(TAG, "!!! IOException " + e.getMessage() );
            return null;
        } catch (URISyntaxException e) {
            Log.e(TAG, "!!! URISyntaxException " + e.getMessage() );
            return null;
        } finally {
            client.getConnectionManager().shutdown();
        }

        return ret;
    }


}
