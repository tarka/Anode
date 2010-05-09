package net.haltcondition.anode;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Future;

/**
 * Copyright Steve Smith (tarkasteve@gmail.com): 08/05/2010
 */
public class HttpWorker<T>
    extends ThreadWorker<T>
{
    private static final String TAG = "HttpWorker";
    
    private Account account;
    private String url;
    private HttpResultHandler<T> resultHandler;

    public HttpWorker(Handler h, String url, HttpResultHandler<T> resultHandler)
    {
        super(h);
        this.url = url;
        this.resultHandler = resultHandler;
    }

    public HttpWorker(Handler h, Account acc, String url, HttpResultHandler<T> resultHandler)
    {
        super(h);
        this.account = acc;
        this.url = url;
        this.resultHandler = resultHandler;
    }

    public T call()
    {
        Log.i(TAG, "Running");

        DefaultHttpClient client = new DefaultHttpClient();
        try {
            Log.d(TAG, "performing get " + url );
            sendUpdate("Fetching page ...");

            URI u = new URI(url);

            if (account != null) {
                client.getCredentialsProvider().setCredentials(
                        new AuthScope(u.getHost(), u.getPort()),
                        new UsernamePasswordCredentials(account.getUsername(), account.getPassword()));
            }

            HttpGet method = new HttpGet(u);
            HttpResponse response = client.execute(method);
            if ( response != null ) {

                sendUpdate("Parsing XML ...");
                return resultHandler.parse(response.getEntity().getContent());

            } else {
                Log.i(TAG, "got a null response" );
                return null;
            }

        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return null;
        } catch (URISyntaxException e) {
            Log.e(TAG, "Error parsing URL" + e.getMessage());
            return null;
            
        } finally {
            client.getConnectionManager().shutdown();
        }

    }


}
