package com.example.acmcovidapplication.db;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class SharedPreferenceManager {

    private static SharedPreferences sharedPreferences = null;
    private SharedPreferenceManager(){

    }

    public static SharedPreferences getSharedPreference(String name,Context context){
        if(sharedPreferences == null )
        {
            synchronized (SharedPreferenceManager.class){
                if(sharedPreferences == null ) sharedPreferences =  context.getSharedPreferences(name, MODE_PRIVATE);
            }

        }

        return sharedPreferences;

    }
}
