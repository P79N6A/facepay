package com.tencent.wxpay.imagefacesign;

import android.util.Xml;

import com.tencent.wxpay.utils.L;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;

public class ReturnXMLParser {

    private static final String TAG = "ReturnXMLParser";

    public static String parseGetAuthInfoXML(InputStream is){
        String result = null;
        try{
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(is, "UTF-8");

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (parser.getName().equals("authinfo")) {
                            eventType = parser.next();
                            result = parser.getText();
                        }
                        if (parser.getName().equals("return_msg")) {
                            eventType = parser.next();
                            result = parser.getText();
                        }
                }
                eventType = parser.next();
            }
        } catch (Exception e){
            L.d(TAG, "e.getMessage()===" + e.getMessage());
            L.d(TAG, "e.toString()===" + e.toString());
            e.printStackTrace();
        }
        return result;
    }
}
