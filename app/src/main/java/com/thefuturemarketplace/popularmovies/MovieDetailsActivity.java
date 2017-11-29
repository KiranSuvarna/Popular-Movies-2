package com.thefuturemarketplace.popularmovies;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.thefuturemarketplace.popularmovies.database.MoviesOpenHelper;
import com.thefuturemarketplace.popularmovies.models.Movie;
import com.thefuturemarketplace.popularmovies.models.MovieVideos;
import com.thefuturemarketplace.popularmovies.utils.DBStoreUtil;
import com.thefuturemarketplace.popularmovies.utils.HelperMethods;
import com.thefuturemarketplace.popularmovies.utils.LocalStoreUtil;
import com.thefuturemarketplace.popularmovies.utils.NetworkUtils;
import com.thefuturemarketplace.popularmovies.utils.ViewUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by imKiran
 */

public class MovieDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>{

    private final String LOG_TAG = MovieDetailsActivity.class.getSimpleName();

    private static final int POPULAR_MOVIES_ASYNKTASK_ID = 6;

    @BindView(R.id.imageview_moview) ImageView imageViewPoster;
    @BindView(R.id.textview_release_date_title) TextView textViewReleaseDateTitle;
    @BindView(R.id.textview_release_date_example)  TextView textViewReleasedate;
    @BindView(R.id.textview_vote_average_title)  TextView textViewVoteAverageTitle;
    @BindView(R.id.textview_vote_average_example) TextView textViewVoteAverage;
    @BindView(R.id.textview_original_title_example)  TextView textViewOriginalTitle;
    @BindView(R.id.textview_overview_example)  TextView textViewOverview;
    @BindView(R.id.imageButton_teaser) ImageButton imageButtonTeaser;
    @BindView(R.id.favButton) FloatingActionButton favoriteButton;
    @BindView(R.id.button_view_reviews) Button openReviewsButton;


    private  Movie movie;
    private SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        ButterKnife.bind(this);

        final Intent intent = getIntent();
        movie = intent.getParcelableExtra(getString(R.string.parcel_movie));

        textViewOriginalTitle.setText(movie.getOriginaltitle());

        Picasso.with(this)
                .load(movie.getPosterPath())
                .resize(getResources().getInteger(R.integer.tmdb_poster_w185_width),
                        getResources().getInteger(R.integer.tmdb_poster_w185_height))
                .error(R.drawable.not_found)
                .placeholder(R.drawable.searching)
                .into(imageViewPoster);

        String overView = movie.getOverview();
        if (overView == null) {
            textViewOverview.setTypeface(null, Typeface.ITALIC);
            overView = getResources().getString(R.string.no_summary_found);
        }
        textViewOverview.setText(overView);

        textViewVoteAverage.setText(movie.getDetailedVoteAverage());

        String releaseDate = movie.getReleaseDate();
        if(releaseDate != null) {
            try {
                releaseDate = DateTimeHelper.getLocalizedDate(this,
                        releaseDate, movie.getDateFormat());
            } catch (ParseException e) {
                Log.e(LOG_TAG, "Error with parsing movie release date", e);
            }
        } else {
            textViewReleasedate.setTypeface(null, Typeface.ITALIC);
            releaseDate = getResources().getString(R.string.no_release_date_found);
        }
        textViewReleasedate.setText(releaseDate);

        getSupportLoaderManager().initLoader(POPULAR_MOVIES_ASYNKTASK_ID,null,this);
        getMoviesTeaserFromTMDb(getString(R.string.tmdb_movie_teaser_url),movie.getmovieId());

        MoviesOpenHelper moviesOpenHelper = new MoviesOpenHelper(this);
        sqLiteDatabase = moviesOpenHelper.getWritableDatabase();

        inflateData();

        openReviewsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ReviewsActivity.class);
                intent.putExtra(getResources().getString(R.string.parcel_movie), movie);
                startActivity(intent);
            }
        });
    }

    private void getMoviesTeaserFromTMDb(String teasersUrl,String movieId) {
        if (new HelperMethods(this).isNetworkAvailable()) {
            // API Key needed to get data from TMDb
            String apiKey = BuildConfig.API_KEY;
            URL theMovieDBTeasersApiCall = null;
            try {
                theMovieDBTeasersApiCall =  new NetworkUtils(apiKey).getTheMovieTeaserUrl(teasersUrl,movieId);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            Bundle bundle  = new Bundle();
            bundle.putString(getString(R.string.url_key),theMovieDBTeasersApiCall.toString());

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

      private void inflateData(){
        favoriteButton.setSelected(movie.isFavorite());
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    LocalStoreUtil.addToFavorites(MovieDetailsActivity.this,movie.getmovieId());
                    DBStoreUtil.addToFavorites(sqLiteDatabase,MovieDetailsActivity.this, movie);
                    ViewUtils.showToast(getResources().getString(R.string.added_favorite),MovieDetailsActivity.this);
                    movie.setFavorite(true);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {

        if(movie!=null)
            savedInstanceState.putParcelable(Movie.TAG_MOVIES, movie);

        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(Movie.TAG_MOVIES)) {
                movie = (Movie) savedInstanceState.getParcelable(Movie.TAG_MOVIES);
            }
        }

        super.onRestoreInstanceState(savedInstanceState);
    }


    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String>(this) {

            String theMoviesDBResponse;

            @Override
            protected void onStartLoading() {
                if (args == null) {
                    return;
                }
                if (theMoviesDBResponse != null) {
                    deliverResult(theMoviesDBResponse);
                }
                 else {
                    forceLoad();
                }
            }

            @Override
            public String loadInBackground() {
                String urlString = args.getString(getString(R.string.url_key));
                if (urlString == null || TextUtils.isEmpty(urlString)) {
                    Log.d("no data in background: ",urlString);
                    return null;
                }
                try {
                    URL url = new URL(urlString);
                    String response = NetworkUtils.getResponseFromHttpUrl(url);
                    String movieId = NetworkUtils.getMoviesTeaserIdFromJson(response);
                    return movieId;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(String data) {
                theMoviesDBResponse = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, final String data) {
        if (null == data) {
            Toast.makeText(this, getString(R.string.no_data), Toast.LENGTH_LONG).show();
        } else {
            final HelperMethods helperMethods = new HelperMethods(getApplicationContext());
            imageButtonTeaser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    helperMethods.watchTeaser(getApplicationContext(),data);
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }
}
