package net.haltcondition.anode;

import android.content.Context;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;

import java.io.InputStream;
import java.security.KeyStore;

/**
 * Copyright Steve Smith (tarkasteve@gmail.com): 04/09/2010
 *
 * Custom HttpClient allowing a custom keystore.  This is
 * unfortunately necessary as the GeoStore global CA is not
 * included in Android currently.
 *
 * Based on here: http://crazybob.org/2010/02/android-trusting-ssl-certificates.html
 * See geostore.sh for the keystore initialisation.
 *
 */
public class WebtoolsHttpClient extends DefaultHttpClient
{

    final Context context;

    public WebtoolsHttpClient(Context context)
    {
        this.context = context;
    }

    @Override
    protected ClientConnectionManager createClientConnectionManager()
    {
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(
            new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        registry.register(new Scheme("https", newSslSocketFactory(), 443));
        return new SingleClientConnManager(getParams(), registry);
    }

    private SSLSocketFactory newSslSocketFactory() {
        try {
            KeyStore trusted = KeyStore.getInstance("BKS");
            InputStream in = context.getResources().openRawResource(R.raw.geotrust);
            try {
                trusted.load(in, "dummypass".toCharArray());
            } finally {
                in.close();
            }
            return new SSLSocketFactory(trusted);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

}
