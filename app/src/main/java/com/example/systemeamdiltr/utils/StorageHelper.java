package com.example.systemeamdiltr.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public abstract class StorageHelper {
    public static SharedPreferences sharedPreferences;
    public static Context context;
    public static SharedPreferences.Editor editor;

    public static void prepare(Context appcontext) {
        context = appcontext;
        loadSharedPreferences();
    }

    public static void loadSharedPreferences() {
        sharedPreferences=
                PreferenceManager.getDefaultSharedPreferences(context /* Activity context */);
        editor = sharedPreferences.edit();
    }



//    public static <T> void storeValue(String key, T value) {
//
//    }
}