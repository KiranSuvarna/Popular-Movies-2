package com.thefuturemarketplace.popularmovies.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.thefuturemarketplace.popularmovies.models.Movie;

/**
 * Created by imKiran
 */
public class MoviesOpenHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MOVIES.DB";
    public static final int DATABASE_VERSION = 1;

    public MoviesOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static final String CREATE_TABLE =
            "create table " + MoviesContract.MoviesEntry.TABLE_MOVIES + " ("
                    + MoviesContract.MoviesEntry._ID + " integer primary key autoincrement, "
                    + MoviesContract.MoviesEntry.MOVIE_ID + " integer , "
                    + MoviesContract.MoviesEntry.MOVIE_POSTER_PATH + " text , "
                    + MoviesContract.MoviesEntry.MOVIE_OVERVIEW + " text , "
                    + MoviesContract.MoviesEntry.MOVIE_RELEASE_DATE + " text , "
                    + MoviesContract.MoviesEntry.MOVIE_ORIGINAL_TITLE + " text , "
                    + MoviesContract.MoviesEntry.MOVIE_VOTE_AVERAGE + " text ) ;";


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("MoviesDBHelper", "Upgrading database from version " + oldVersion + " to " + newVersion + ". OLD DATA WILL BE DESTROYED");

        // Drop the table
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MoviesEntry.TABLE_MOVIES);
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + MoviesContract.MoviesEntry.TABLE_MOVIES + "'");

        // re-create database
        onCreate(db);
    }

    public static ContentValues getMovieContentValues(Movie movies) {

        ContentValues values = new ContentValues();
        values.put(MoviesContract.MoviesEntry.MOVIE_ID, movies.getmovieId());
        values.put(MoviesContract.MoviesEntry.MOVIE_POSTER_PATH, movies.getPosterPath());
        values.put(MoviesContract.MoviesEntry.MOVIE_OVERVIEW, movies.getOverview());
        values.put(MoviesContract.MoviesEntry.MOVIE_RELEASE_DATE, movies.getReleaseDate());
        values.put(MoviesContract.MoviesEntry.MOVIE_VOTE_AVERAGE, movies.getVoteAverage());

        return values;
    }
}
