package com.example.accessibilityauto.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class SP {
    private static SharedPreferences sharedPreferences;

    public static void init(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static String getString(String key) {
        return sharedPreferences.getString(key, "");
    }
    public static void putString(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }
    public static void putStringN(String key,String value){
        sharedPreferences.edit().putString(key,value).apply();

    }
    public static boolean getBoolean(String key){
        return sharedPreferences.getBoolean(key,false);
    }
    public static void putBoolean(String key,boolean value){
        sharedPreferences.edit().putBoolean(key,value).apply();

    }

}
