package com.byonchat.android.list;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;

import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.MessengerDatabaseHelper;

public class LocationDisplayHandler implements OnClickListener {
    private double latitude;
    private double longitude;
    private String name;
    private String address;
    private String jabberId;
    private String url;
    private Activity activity;

    public LocationDisplayHandler(Activity activity, String jid, double lat,
            double lng,String nm,String addr,String urls) {
        jabberId = jid;
        this.activity = activity;
        latitude = lat;
        longitude = lng;
        name = nm;
        address = addr;
        url = urls;
    }

    @Override
    public void onClick(View view) {
        String tag = "You";
        MessengerDatabaseHelper dbhelper = MessengerDatabaseHelper
                .getInstance(activity);
        if (!dbhelper.getMyContact().getJabberId().equals(jabberId)) {
            Contact c = dbhelper.getContact(jabberId);
            tag = "+" + jabberId;
            if (c != null) {
                tag = c.getName();
            }

        }
    /*    String loc = method(name)+", "+ method(address);
        if (address.contains(name)){
            loc =address;
        }*/

      /*  String coordinates = "http://maps.google.com/maps?daddr=" + latitude + "," + longitude;

        Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse(coordinates) );
        activity.startActivity(intent );*/

       // Uri gmmIntentUri = Uri.parse("https://www.google.com/maps/place/"+loc.replace(" ","+")+"/@"+latitude+","+longitude+",15z");
     /*   Uri gmmIntentUri = Uri.parse("geo:"+latitude+","
                +longitude+"?q=" + Uri.encode(loc));*/
       // Uri gmmIntentUri = Uri.parse("https://www.google.com/maps/preview/" + loc.replace(" ", "+") + "/@" + latitude + "," + longitude + ",15z");
    //    Log.w("asdad",String.valueOf(Uri.parse("https://www.google.com/maps/place/"+loc.replace(" ","+")+"/@"+latitude+","+longitude+",15z")));
        /*Uri gmmIntentUri = Uri.parse("geo:"+latitude+","
                +longitude+"?q="+name.replace(" ","+"));*/
     /*   Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        activity.startActivity(mapIntent);*/

       /* String uri = String.format(Locale.ENGLISH, "geo:%f,%f", latitude, longitude);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        activity.startActivity(intent);*/

       /* Intent intent;
            if(address.equalsIgnoreCase("")){
                intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?q=" + latitude + ","
                                + longitude + " (" + tag + ")"));
            }else {
                String loc = name+"+,+"+address;
                if (address.contains(name)){
                    loc =address;
                }
                intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("https://maps.google.com/?q="+address.replace(" ","+")+"&center="+latitude+","
                                +longitude+"&views=satellite,traffic&zoom=15"));
            }
        activity.startActivity(intent);*/

        Uri gmmIntentUri = null;
        if(address.equalsIgnoreCase("")){
            gmmIntentUri = Uri.parse("geo:0,0?q="+latitude + ","
                    + longitude+"("+tag+")");
        }else{
            String loc = name+"+,+"+address;
            gmmIntentUri = Uri.parse("geo:0,0?q="+latitude + ","
                    + longitude+"("+loc+")");
        }

        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        activity.startActivity(mapIntent);
    }

    public String method(String str) {
        if (str.length() > 0 && str.charAt(str.length()-1)=='x') {
            str = str.substring(0, str.length()-1);
        }
        return str;
    }
}
/*
String uri = String.format(Locale.ENGLISH, "geo:%f,%f", latitude, longitude);
Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
context.startActivity(intent);*/
