package com.thefuturemarketplace.popularmovies.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thefuturemarketplace.popularmovies.database.MoviesContract;
import com.thefuturemarketplace.popularmovies.models.Movie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by imKiran
 */
public class LocalStoreUtil {
    private static GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss");
    private static Gson gson = gsonBuilder.create();

    public static final String PREF_FILE_NAME = "com.thefuturemarketplace.popularmovies";
    public static final String PREF_FAVORITE_MOVIES = "favorite_movies";


    public static void addToFavorites(final Context context, String movieId) {
        SharedPreferences sp = null;
        try {
            sp = getSharedPreference(context);
            Set<String> set = sp.getStringSet(PREF_FAVORITE_MOVIES, null);
            if (set == null) set = new HashSet<>();
            set.add(movieId);

            SharedPreferences.Editor editor = getSharedEditor(context);
            editor.clear();

            editor.putStringSet(PREF_FAVORITE_MOVIES, set).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeFromFavorites(final Context context, String movieId) {
        SharedPreferences sp = null;
        try {
            sp = getSharedPreference(context);
            Set<String> set = sp.getStringSet(PREF_FAVORITE_MOVIES, null);
            if (set == null) set = new HashSet<>();
            set.remove(movieId);

            SharedPreferences.Editor editor = getSharedEditor(context);
            editor.clear();

            editor.putStringSet(PREF_FAVORITE_MOVIES, set).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static SharedPreferences getSharedPreference(Context context)
            throws Exception {
        if (context == null) {
            throw new Exception("Context null Exception");
        }
        return context.getSharedPreferences(PREF_FILE_NAME, 0);
    }

    private static SharedPreferences.Editor getSharedEditor(Context context)
            throws Exception {
        if (context == null) {
            throw new Exception("Context null Exception");
        }
        return getSharedPreference(context).edit();
    }
}
