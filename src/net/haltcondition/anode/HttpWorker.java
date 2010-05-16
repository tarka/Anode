package net.haltcondition.anode;

import android.os.Handler;
import android.util.Log;
import org.apache.http.*;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import sun.net.www.protocol.http.AuthCache;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

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

    private class AuthHandler implements HttpRequestInterceptor
    {
        private URI uri;

        private AuthHandler(URI uri) {
            this.uri = uri;
        }

        @Override
        public void process(HttpRequest httpRequest, HttpContext context) throws HttpException, IOException {
            Log.i(TAG, "Interceptor invoked!");
            AuthState authState = (AuthState) context.getAttribute(ClientContext.TARGET_AUTH_STATE);

            CredentialsProvider credsProvider = (CredentialsProvider) context.getAttribute(ClientContext.CREDS_PROVIDER);

            if (authState.getAuthScheme() == null) {
                AuthScope authScope = new AuthScope(uri.getHost(), AuthScope.ANY_PORT);
                Credentials creds = credsProvider.getCredentials(authScope);
                Log.i(TAG, "CREDS: "+creds);
                if (creds != null) {
                    authState.setAuthScheme(new BasicScheme());
                    authState.setCredentials(creds);
                }
            }
        }
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

                client.getCredentialsProvider().setCredentials(
                        new AuthScope(u.getHost(), AuthScope.ANY_PORT, "internode-api"),
                        new UsernamePasswordCredentials(account.getUsername(), account.getPassword()));
                client.addRequestInterceptor(new AuthHandler(u));
            }

            HttpResponse response = client.execute(method);
            if ( response == null){
                Log.i(TAG, "got a null response" );
                sendError("Error Retrieving Service Data");

            } else if (response.getStatusLine().getStatusCode() == 200) {
                Log.d(TAG, "Status: "+response.getStatusLine());


                byte b[] = new byte[1];
                ArrayList<Byte> ch = new ArrayList<Byte>();
                InputStream in = response.getEntity().getContent();
                while (in.available() > 0) {
                    in.read(b, 0, 1);
                    ch.add(b[0]);
                }
                Log.i(TAG, "CONTENT: "+ch.toString());
                
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
