package net.haltcondition.anode;

import android.util.Log;
import android.util.Xml;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

/**
 * Copyright Steve Smith (tarkasteve@gmail.com): 15/05/2010
 */
public class UsageParser
    extends DefaultHandler
    implements HttpResultHandler<Usage>
{
    private static final String TAG = "UsageParser";

    private Usage usage = null;
    private boolean inElement = false;
    private static final String ELEMENT = "traffic"; 

    @Override
    public Usage parse(InputStream in)
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

        return usage;
    }

    @Override
    public void startElement(String  uri, String  localName, String  qName, Attributes  attributes)
        throws SAXException
    {
        Log.d(TAG, "Element Start: "+localName);
        if (localName.equals(ELEMENT)) {
            usage = new Usage();
            usage.setTotalQuota(attributes.getValue("quota"));
            try {
                usage.setRollOver(attributes.getValue("rollover"));
            } catch (ParseException e) {
                Log.e(TAG, "Error parsing date: "+e);
                usage = null;
            }
            inElement = true;
        }
    }

    @Override
    public void  endElement(String  uri, String  localName, String  qName)
        throws SAXException
    {
        Log.d(TAG, "Element End: "+localName);
        if (localName.equals(ELEMENT)) {
            inElement = false;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)
        throws SAXException
    {
        Log.d(TAG, "Chars: "+ch.toString());
        if (inElement && usage != null) {
            usage.setUsed(new String(ch));
        }
    }
}