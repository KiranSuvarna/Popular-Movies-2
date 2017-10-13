package com.thefuturemarketplace.popularmovies;


import com.thefuturemarketplace.popularmovies.models.Movie;

/**
 * Created by imKiran
 * <p/>
 * Based on http://stackoverflow.com/questions/9963691/android-asynctask-sending-callbacks-to-ui
 */
interface OnTaskCompleted {
    void onFetchMoviesTaskCompleted(Movie[] movies);
}
