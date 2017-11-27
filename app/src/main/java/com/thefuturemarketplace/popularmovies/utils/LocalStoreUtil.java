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


    public static void addToFavorites(SQLiteDatabase db,final Context context, Movie movie) {
        if(db == null){
            return;
        }
        List<ContentValues> list = new ArrayList<ContentValues>();

        ContentValues cv = new ContentValues();
        cv.put(MoviesContract.MoviesEntry.MOVIE_ID, movie.getmovieId());
        cv.put(MoviesContract.MoviesEntry.MOVIE_ORIGINAL_TITLE, movie.getOriginaltitle());
        cv.put(MoviesContract.MoviesEntry.MOVIE_OVERVIEW,movie.getOverview());
        cv.put(MoviesContract.MoviesEntry.MOVIE_POSTER_PATH, movie.getPosterPath());
        cv.put(MoviesContract.MoviesEntry.MOVIE_RELEASE_DATE, movie.getReleaseDate());
        cv.put(MoviesContract.MoviesEntry.MOVIE_VOTE_AVERAGE, movie.getVoteAverage());
        list.add(cv);

        try{
            db.beginTransaction();
            for(ContentValues c : list){
                db.insert(MoviesContract.MoviesEntry.TABLE_MOVIES,null,c);
            }
            db.setTransactionSuccessful();
        }catch (SQLException e){
            e.printStackTrace();
        }finally{
            db.endTransaction();
        }
    }
}
