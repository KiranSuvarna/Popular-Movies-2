package com.thefuturemarketplace.popularmovies.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;

import com.thefuturemarketplace.popularmovies.database.MoviesContract;
import com.thefuturemarketplace.popularmovies.models.Movie;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by imkiran
 */

public class DBStoreUtil extends AppCompatActivity{

    public static ContentValues getContentValues(Movie movie) {
        ContentValues cv = new ContentValues();
        cv.put(MoviesContract.MoviesEntry.MOVIE_ID, movie.getmovieId());
        cv.put(MoviesContract.MoviesEntry.MOVIE_ORIGINAL_TITLE, movie.getOriginaltitle());
        cv.put(MoviesContract.MoviesEntry.MOVIE_OVERVIEW,movie.getOverview());
        cv.put(MoviesContract.MoviesEntry.MOVIE_POSTER_PATH, movie.getPosterPath());
        cv.put(MoviesContract.MoviesEntry.MOVIE_RELEASE_DATE, movie.getReleaseDate());
        cv.put(MoviesContract.MoviesEntry.MOVIE_VOTE_AVERAGE, movie.getVoteAverage());
        return cv;
    }

}
