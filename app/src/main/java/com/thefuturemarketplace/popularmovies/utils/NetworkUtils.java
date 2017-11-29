/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thefuturemarketplace.popularmovies.utils;

import android.net.Uri;
import android.util.Log;

import com.thefuturemarketplace.popularmovies.models.Movie;
import com.thefuturemarketplace.popularmovies.models.MovieReviews;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the network.
 */
public class NetworkUtils {

    private String mApiKey = "";
    final String API_KEY_PARAM = "api_key";

    public NetworkUtils(String apiKey) {
        super();
        mApiKey = apiKey;
    }

    public URL getApiUrl(String parameters) throws MalformedURLException {
        final String TMDB_BASE_URL = parameters;
        //final String SORT_BY_PARAM = "sort_by";
        final String ADULT_CONTENT = "include_adult";

        Uri builtUri = Uri.parse(TMDB_BASE_URL).buildUpon()
                //.appendQueryParameter(SORT_BY_PARAM, parameters[0])
                .appendQueryParameter(API_KEY_PARAM, mApiKey)
                .appendQueryParameter(ADULT_CONTENT, "false")
                .build();

        return new URL(builtUri.toString());
    }

    public URL getTheMovieTeaserUrl(String url, String movieId) throws MalformedURLException {
        final String TMDB_MOVIE_TEASER_URL = url;
        Uri builtUri = Uri.parse(TMDB_MOVIE_TEASER_URL).buildUpon()
                .appendEncodedPath(movieId)
                .appendEncodedPath("videos")
                .appendQueryParameter(API_KEY_PARAM, mApiKey)
                .build();
        return new URL(builtUri.toString());
    }

    public URL getTheMovieReviewsUrl(String url, String movieId) throws MalformedURLException {
        final String TMDB_MOVIE_TEASER_URL = url;
        Uri builtUri = Uri.parse(TMDB_MOVIE_TEASER_URL).buildUpon()
                .appendEncodedPath(movieId)
                .appendEncodedPath("reviews")
                .appendQueryParameter(API_KEY_PARAM, mApiKey)
                .build();
        return new URL(builtUri.toString());
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static Movie[] getMoviesDataFromJson(String moviesJsonStr) throws JSONException {
        // JSON tags
        final String TAG_RESULTS = "results";
        final String TAG_ORIGINAL_TITLE = "original_title";
        final String TAG_POSTER_PATH = "poster_path";
        final String TAG_OVERVIEW = "overview";
        final String TAG_VOTE_AVERAGE = "vote_average";
        final String TAG_RELEASE_DATE = "release_date";
        final String TAG_MOVIE_ID = "id";

        // Get the array containing hte movies found
        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        JSONArray resultsArray = moviesJson.getJSONArray(TAG_RESULTS);

        // Create array of Movie objects that stores data from the JSON string
        Movie[] movies = new Movie[resultsArray.length()];

        // Traverse through movies one by one and get data
        for (int i = 0; i < resultsArray.length(); i++) {
            // Initialize each object before it can be used
            movies[i] = new Movie();

            // Object contains all tags we're looking for
            JSONObject movieInfo = resultsArray.getJSONObject(i);

            // Store data in movie object
            movies[i].setOriginaltitle(movieInfo.getString(TAG_ORIGINAL_TITLE));
            movies[i].setPosterPath(movieInfo.getString(TAG_POSTER_PATH));
            movies[i].setOverview(movieInfo.getString(TAG_OVERVIEW));
            movies[i].setVoteAverage(movieInfo.getDouble(TAG_VOTE_AVERAGE));
            movies[i].setReleaseDate(movieInfo.getString(TAG_RELEASE_DATE));
            movies[i].setmovieId(movieInfo.getString(TAG_MOVIE_ID));
        }

        return movies;
    }

    public static String getMoviesTeaserIdFromJson(String moviesJsonStr) throws JSONException {
        // JSON tags
        final String TAG_RESULTS = "results";
        final String TAG_MOVIE_ID = "key";

        // Get the array containing hte movies found
        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        JSONArray resultsArray = moviesJson.getJSONArray(TAG_RESULTS);

        JSONObject firstTeaser = (JSONObject) resultsArray.opt(0);
        return firstTeaser.get(TAG_MOVIE_ID).toString();
    }

    public static MovieReviews[] getMoviesReviewsIdFromJson(String moviesJsonStr) throws JSONException {
        // JSON tags
        final String TAG_RESULTS = "results";
        final String TAG_AUTHOR = "author";
        final String TAG_CONTENT = "content";

        // Get the array containing hte movies found
        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        JSONArray resultsArray = moviesJson.getJSONArray(TAG_RESULTS);

        MovieReviews[] movieReviews = new MovieReviews[resultsArray.length()];

        for (int i = 0; i < resultsArray.length(); i++) {
            movieReviews[i] = new MovieReviews();

            JSONObject moviereviews = resultsArray.getJSONObject(i);
            movieReviews[i].setAuthor(moviereviews.getString(TAG_AUTHOR));
            movieReviews[i].setComment(moviereviews.getString(TAG_CONTENT));
        }

        return movieReviews;
    }
}