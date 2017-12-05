package com.thefuturemarketplace.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.thefuturemarketplace.popularmovies.models.Movie;
import com.thefuturemarketplace.popularmovies.models.MovieReviews;
import com.thefuturemarketplace.popularmovies.models.Sort;
import com.thefuturemarketplace.popularmovies.utils.HelperMethods;
import com.thefuturemarketplace.popularmovies.utils.NetworkUtils;

import java.net.MalformedURLException;
import java.net.URL;

public class ReviewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<MovieReviews[]>{

    private Movie movie;
    private static final int POPULAR_MOVIES_ASYNKTASK_ID = 7;

    private RecyclerView recyclerView;
    private MovieReviewsAdapter movieReviewsAdapter;

    private TextView errorMessageDisplay;

    private ProgressBar loadingIndicator;

    private String CURRENT_SCROLL_POSITION = "current_scroll_position";

    private Parcelable currentScrollPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_reviews);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerview_reviews);

        errorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setHasFixedSize(true);

        movieReviewsAdapter = new MovieReviewsAdapter();

        recyclerView.setAdapter(movieReviewsAdapter);

        loadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);


        final Intent intent = getIntent();
        movie = intent.getParcelableExtra(getString(R.string.parcel_movie));

        getSupportLoaderManager().initLoader(POPULAR_MOVIES_ASYNKTASK_ID,null,this);
        getMoviesReviewsFromTMDb(getString(R.string.tmdb_movie_teaser_url),movie.getmovieId());
    }

    private void getMoviesReviewsFromTMDb(String reviewsUrl, String movieId){
        if (new HelperMethods(this).isNetworkAvailable()) {
            // API Key needed to get data from TMDb
            String apiKey = BuildConfig.API_KEY;
            URL theMovieDBReviewsApiCall = null;
            try {
                theMovieDBReviewsApiCall =  new NetworkUtils(apiKey).getTheMovieReviewsUrl(reviewsUrl,movieId);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            Bundle bundle  = new Bundle();
            bundle.putString(getString(R.string.url_key),theMovieDBReviewsApiCall.toString());

            LoaderManager loaderManager = getSupportLoaderManager();
            Loader<String> popularMoviesLoader = loaderManager.getLoader(POPULAR_MOVIES_ASYNKTASK_ID);
            if(popularMoviesLoader==null){
                loaderManager.initLoader(POPULAR_MOVIES_ASYNKTASK_ID,bundle,this);
            }else {
                loaderManager.restartLoader(POPULAR_MOVIES_ASYNKTASK_ID,bundle,this);
            }

        } else {
            Toast.makeText(this, getString(R.string.error_need_internet), Toast.LENGTH_LONG).show();
        }
    }

    private void showErrorMessage() {
        recyclerView.setVisibility(View.INVISIBLE);
        errorMessageDisplay.setVisibility(View.VISIBLE);
    }

    private void showWeatherDataView() {
        /* First, make sure the error is invisible */
        errorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(CURRENT_SCROLL_POSITION,recyclerView.getLayoutManager().onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState!=null) {
            currentScrollPosition = savedInstanceState.getParcelable(CURRENT_SCROLL_POSITION);
        }
    }


    @Override
    public Loader<MovieReviews[]> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<MovieReviews[]>(this) {

            MovieReviews[] theMoviesDBResponse;

            @Override
            protected void onStartLoading() {
                if (args == null) {
                    return;
                }if(theMoviesDBResponse!=null){
                    deliverResult(theMoviesDBResponse);
                }
                else {
                    loadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Override
            public MovieReviews[] loadInBackground() {
                String urlString = args.getString(getString(R.string.url_key));
                if (urlString == null || TextUtils.isEmpty(urlString)) {
                    Log.d("no data in background: ",urlString);
                    return null;
                }
                try {
                    URL url = new URL(urlString);
                    String response = NetworkUtils.getResponseFromHttpUrl(url);
                    return NetworkUtils.getMoviesReviewsIdFromJson(response);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<MovieReviews[]> loader, MovieReviews[] data) {
        loadingIndicator.setVisibility(View.INVISIBLE);
        movieReviewsAdapter.setMoviesReviews(data);
        recyclerView.getLayoutManager().onRestoreInstanceState(currentScrollPosition);
        if (null == data) {
            showErrorMessage();
            Toast.makeText(this, getString(R.string.no_data), Toast.LENGTH_LONG).show();
        } else {
            showWeatherDataView();
        }
    }

    @Override
    public void onLoaderReset(Loader<MovieReviews[]> loader) {
    }
}
