package com.honda.android.communication;

import org.jivesoftware.smackx.delay.provider.DelayInformationProvider;
import org.jxmpp.util.XmppDateTime;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by bayufa on 9/8/2015.
 */
public class MyDelayInformationProvider extends DelayInformationProvider {

    @Override
    protected Date parseDate(String string) throws ParseException {
        return XmppDateTime.parseDate(string);
        //return super.parseDate(string);
    }
}
