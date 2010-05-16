package net.haltcondition.anode;

import android.util.Log;
import android.util.Xml;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;

/**
 * Copyright Steve Smith (tarkasteve@gmail.com): 15/05/2010
 */
public class ServiceParser
    extends DefaultHandler
    implements HttpResultHandler<Service>
{
    private static final String TAG = "ServiceParser";

    private Service service = null;
    private boolean inElement = false;

    public Service parse(InputStream in)
    {
        try {
            Xml.parse(in, Xml.Encoding.UTF_8, this);
        } catch (IOException e) {
            Log.e(TAG, "IOException " + e.getMessage() );
            return null;
        } catch (SAXException e) {
            Log.e(TAG, "SAXException " + e.getMessage() );
            return null;
        }
        return service;
    }

    @Override
    public void startElement(String  uri, String  localName, String  qName, Attributes  attributes)
        throws SAXException
    {
        Log.d(TAG, "Element Start: "+localName);
        if (localName.equals("service")) {
            inElement = true;
        }
    }

    @Override
    public void  endElement(String  uri, String  localName, String  qName)
        throws SAXException
    {
        Log.d(TAG, "Element End: "+localName);
        if (localName.equals("service")) {
            inElement = false;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)
        throws SAXException
    {
        Log.d(TAG, "Chars: "+ch.toString());
        if (inElement) {
            service = new Service(new String(ch).trim());
        }
    }
}
