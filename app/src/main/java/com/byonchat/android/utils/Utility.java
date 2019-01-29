package com.byonchat.android.utils;

/**
 * Created by Iman Firmansyah on 1/9/2015.
 */

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.communication.MyJobService;
import com.byonchat.android.provider.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class Utility {

    public static final SimpleDateFormat dateInfoFormat = new SimpleDateFormat(
            "dd/MM/yyyy", Locale.getDefault());
    public static final SimpleDateFormat hourInfoFormat = new SimpleDateFormat(
            "HH:mm", Locale.getDefault());
    public static String SQL_SELECT_TOTAL_MESSAGES_UNREAD = "SELECT count(*) total FROM "
            + Message.TABLE_NAME
            + " WHERE ("
            + Message.DESTINATION
            + "=? OR "
            + Message.SOURCE + "=? )"
            + " AND status = 12";

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

    //Range
    public static String sideRange(Location Office, Location People, double rangeInMeters) {
        double distance = Office.distanceTo(People);
        String insideOrOutside = null;

        if (distance <= rangeInMeters) {
            insideOrOutside = "Inside";
        } else {
            insideOrOutside = "Outside";
        }

        return insideOrOutside;
    }

    //Distance
    public static double distanceInMeters(Location from, Location to) {
        double distance = from.distanceTo(to);

        return distance;
    }

    public static String formatPhoneNumber(String number) {
        if (number.length() > 12) {
            number = number.substring(0, number.length() - 4) + "-" + number.substring(number.length() - 4, number.length());
            if (number.length() > 9) {
                number = number.substring(0, number.length() - 9) + "-" + number.substring(number.length() - 9, number.length());
                if (number.length() > 13) {
                    number = number.substring(0, number.length() - 13) + " " + number.substring(number.length() - 13, number.length());
                }
            }
        } else {
            if (number.length() > 4) {
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

    public static String roomName(Context context, String rooms, boolean getName) {
        String roomsName = "";
        if (getName) {
            roomsName = new GetRealNameRoom().getInstance(context).getName(rooms);
        } else {
            String name[] = rooms.split("_");

            if (name[0].length() == 1) {
                roomsName = rooms.substring(2, rooms.length()).replace("_", " ");
            } else {
                roomsName = rooms.replace("_", " ");
            }
        }
        return roomsName;
    }

    public static String roomType(String rooms) {
        String name[] = rooms.split("_");
        String roomsName = "";
        if (name[0].length() == 1) {
            roomsName = rooms.substring(0, 1);
        }
        return roomsName;
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


    public static Date convertStringToDate(String dateInString) {

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

    public static String jsonResultType(String json, String type) {
        String hasil = "";
        JSONObject jObject = null;
        try {
            jObject = new JSONObject(json);
        } catch (JSONException e) {
            hasil = "error";
            e.printStackTrace();
        }
        if (jObject != null) {
            try {
                hasil = jObject.getString(type);
            } catch (JSONException e) {
                hasil = "error";
                e.printStackTrace();
            }
        }

        if (type.equalsIgnoreCase("e") && hasil.equalsIgnoreCase("error")) {
            hasil = "https://" + MessengerConnectionService.HTTP_SERVER;
        }

        return hasil;
    }

    public static int generateRandomInt() {
        final int min = 1;
        final int max = 9999999;
        final int random = new Random().nextInt((max - min) + 1) + min;

        return random;
    }

    @TargetApi(23)
    public static void scheduleJob(Context context) {
        ComponentName serviceComponent = new ComponentName(context, MyJobService.class);
//        JobInfo.Builder builder = new JobInfo.Builder(123123, serviceComponent);
        JobInfo.Builder builder = new JobInfo.Builder(generateRandomInt(), serviceComponent);
        builder.setMinimumLatency(1 * 1000); // wait at least
        builder.setOverrideDeadline(3 * 1000); // maximum delay
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED); // require unmetered network
        builder.setRequiresDeviceIdle(true); // device should be idle
        builder.setRequiresCharging(false); // we don't care if the device is charging or not
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        jobScheduler.schedule(builder.build());
    }

    public static String capitalizer(String word) {
        String[] words = word.split(" ");
        StringBuilder sb = new StringBuilder();
        if (words[0].length() > 0) {
            sb.append(Character.toUpperCase(words[0].charAt(0)) + words[0].subSequence(1, words[0].length()).toString());
            for (int i = 1; i < words.length; i++) {
                sb.append(" ");
                sb.append(Character.toUpperCase(words[i].charAt(0)) + words[i].subSequence(1, words[i].length()).toString());
            }
        }
        return sb.toString();

    }
}
