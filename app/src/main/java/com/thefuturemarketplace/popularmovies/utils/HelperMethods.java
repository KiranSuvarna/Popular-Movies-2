package com.thefuturemarketplace.popularmovies.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.thefuturemarketplace.popularmovies.R;

import java.net.URI;

/**
 * Created by Admin on 20-Oct-17.
 */

public class HelperMethods extends ContextWrapper{

    public HelperMethods(Context base) {
        super(base);
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public String getSortMethod() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        return prefs.getString(getString(R.string.pref_sort_method_key),
                getString(R.string.tmdb_sort_popular));
    }

    public void updateSharedPrefs(String sortMethod) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.pref_sort_method_key), sortMethod);
        editor.apply();
    }

    public void watchTeaser(Context context, String id){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.teaser_app)+id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(getString(R.string.teaser_web)+id));
        try {
            if(appIntent.resolveActivity(getPackageManager())!=null){
                startActivity(appIntent);
            }else if(webIntent.resolveActivity(getPackageManager())!=null){
                startActivity(webIntent);
            }
        } catch (ActivityNotFoundException ex) {
            ex.printStackTrace();
        }
    }


}
