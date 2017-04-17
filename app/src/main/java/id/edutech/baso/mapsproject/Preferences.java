package id.edutech.baso.mapsproject;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Baso on 10/17/2016.
 */
public class Preferences {
    private static final String SHARED_PREF="hispot";
    public static void setStringPreferences(String prefName, String value, Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE).edit();
        editor.putString(prefName, value);
        editor.apply();
    }
    public static String getStringPreferences(String prefName,Context context){
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        return preferences.getString(prefName, "");
    }
    public static void setIntPreferences(String prefName, int value, Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE).edit();
        editor.putInt(prefName, value);
        editor.apply();
    }
    public static int getIntPreferences(String prefName,Context context){
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        return preferences.getInt(prefName, 0);
    }
    public static void setBooleanPreferences(String prefName, boolean value, Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE).edit();
        editor.putBoolean(prefName, value);
        editor.apply();
    }
    public static Boolean getBooleanPreferences(String prefName,Context context){
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        return preferences.getBoolean(prefName, false);
    }
}
