package com.thefuturemarketplace.popularmovies.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.thefuturemarketplace.popularmovies.database.MoviesContract;
import com.thefuturemarketplace.popularmovies.models.Movie;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by imkiran
 */

public class DBStoreUtil {

    public static void addToFavorites(SQLiteDatabase db, final Context context, Movie movie) {
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

    public static void deleteFavorite(SQLiteDatabase db, final Context context, Movie movie) {
        if(db == null){
            return;
        }
        try{
            db.beginTransaction();
            db.delete(MoviesContract.MoviesEntry.TABLE_MOVIES,"movies_id=?",new String[] {movie.getmovieId()});
            db.setTransactionSuccessful();
        }catch (SQLException e){
            e.printStackTrace();
        }finally{
            db.endTransaction();
        }
    }

}
