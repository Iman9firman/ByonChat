package com.byonchat.android.utils;

/**
 * Created by Iman Firmansyah on 1/9/2015.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utility {
    // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    public static Bitmap getPhoto(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public static String formatPhoneNumber(String number){
        if (number.length()>12){
            number  =   number.substring(0, number.length()-4) + "-" + number.substring(number.length()-4, number.length());
            if (number.length()>9){
                number  =   number.substring(0,number.length()-9)+"-"+number.substring(number.length()-9,number.length());
                if (number.length()>13){
                    number  =   number.substring(0, number.length()-13)+" "+number.substring(number.length()-13, number.length());
                }
            }
        }else{
            if (number.length()>4) {
                number = number.substring(0, number.length() - 4) + "-" + number.substring(number.length() - 4, number.length());
                if (number.length() > 8) {
                    number = number.substring(0, number.length() - 8) + "-" + number.substring(number.length() - 8, number.length());
                    if (number.length() > 12) {
                        number = number.substring(0, number.length() - 12) + " " + number.substring(number.length() - 12, number.length());
                    }
                }
            }
        }
        return number;
    }
    
    public static String roomName(Context context , String rooms,boolean getName){
        String roomsName = "";
        if(getName){
            roomsName = new GetRealNameRoom().getInstance(context).getName(rooms);
        }else{
            String name[] = rooms.split("_");

            if(name[0].length()==1){
                roomsName = rooms.substring(2,rooms.length()).replace("_"," ");
            }else{
                roomsName = rooms.replace("_"," ");
            }
        }
        return  roomsName;
    }

    public static String roomType(String rooms){
        String name[] = rooms.split("_");
        String roomsName = "";
        if(name[0].length()==1){
            roomsName = rooms.substring(0,1);
        }
        return  roomsName;
    }

    public static String parseDateToddMMyyyy(String time) {
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "HH:mm:ss dd/MM/yyyy";

        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = time;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }


    public static Date convertStringToDate(String dateInString){

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        try {

            Date date = formatter.parse(dateInString);
            System.out.println(date);
            System.out.println(formatter.format(date));

            return date;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    public static void hideKeyboard(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
