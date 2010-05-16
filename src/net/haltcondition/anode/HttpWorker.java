package net.haltcondition.anode;

import android.os.Handler;
import android.util.Log;
import net.haltcondition.util.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Copyright Steve Smith (tarkasteve@gmail.com): 08/05/2010
 */
public class HttpWorker<RType>
    extends ThreadWorker<RType>
{
    private static final String TAG = "HttpWorker";
    
    private Account account;
    private String url;
    private HttpResultHandler<RType> resultHandler;

    public HttpWorker(Handler h, String url, HttpResultHandler<RType> resultHandler)
    {
        super(h);
        this.url = url;
        this.resultHandler = resultHandler;
    }

    public HttpWorker(Handler h, Account acc, String url, HttpResultHandler<RType> resultHandler)
    {
        super(h);
        this.account = acc;
        this.url = url;
        this.resultHandler = resultHandler;
    }

    public void run()
    {
        Log.i(TAG, "Running");

        DefaultHttpClient client = new DefaultHttpClient();
        try {
            Log.d(TAG, "performing get " + url );
            sendUpdate("Fetching page ...");

            URI u = new URI(url);
            HttpGet method = new HttpGet(u);

            if (account != null) {
                Log.i(TAG, "Using account "+account.getUsername());
                /*
                 * This sucks, but I've wasted half a day trying to get HttpClient basic auth to work.
                 * Android appears to ship with alpha version of 4.0 that doesn't match the docs.
                 */
                String auth = Base64.encodeBytes((account.getUsername() + ":" + account.getPassword()).getBytes()).toString();
                method.addHeader("Authorization", "Basic " + auth);
            }

            HttpResponse response = client.execute(method);
            if ( response == null){
                Log.i(TAG, "got a null response" );
                sendError("Error Retrieving Service Data");

            } else if (response.getStatusLine().getStatusCode() == 200) {
                Log.d(TAG, "Status: "+response.getStatusLine());
                
                sendUpdate("Parsing XML ...");
                sendResult(resultHandler.parse(response.getEntity().getContent()));

            } else {
                String err = "Failed to get data: "+response.getStatusLine().getStatusCode()+" / "+response.getStatusLine().getReasonPhrase();
                Log.e(TAG, err);
                sendError(err);
            }

        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            sendError("IO Error Retrieving Data");
        } catch (URISyntaxException e) {
            Log.e(TAG, "Error parsing URL" + e.getMessage());
            sendError("URI Error Retrieving Data");

        } finally {
            client.getConnectionManager().shutdown();
        }

    }


}
