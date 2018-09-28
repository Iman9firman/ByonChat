package zharfan.com.cameralibrary;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ShrdPref {
    public static Integer START_CAMERA_LIB = 0;
    static final String FLAG_FRAGMENT = "flag";
    public static SharedPreferences getPref (Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setFlag(Context context,Integer id){
        SharedPreferences.Editor editor = getPref(context).edit();
        editor.putInt(FLAG_FRAGMENT,id);
        editor.apply();
    }
    public static Integer getFlag(Context context){
        return getPref(context).getInt(FLAG_FRAGMENT,0);
    }
    public static void removeFlag(Context context){
        SharedPreferences.Editor editor = getPref(context).edit();
        editor.remove(FLAG_FRAGMENT);
        editor.apply();
    }
}
