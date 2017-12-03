package com.thefuturemarketplace.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.thefuturemarketplace.popularmovies.database.MoviesContract;
import com.thefuturemarketplace.popularmovies.database.MoviesOpenHelper;
import com.thefuturemarketplace.popularmovies.models.Movie;
import com.thefuturemarketplace.popularmovies.models.Sort;
import com.thefuturemarketplace.popularmovies.utils.HelperMethods;
import com.thefuturemarketplace.popularmovies.utils.NetworkUtils;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Movie[]>{

    private static final int POPULAR_MOVIES_ASYNKTASK_ID = 5;

    private GridView gridView;

    private Sort mSort = Sort.POPULAR;

    private SQLiteDatabase sqLiteDatabase;

    private String CURRENT_SCROLL_POSITION = "current_scroll_position";

    private int currentScrollPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridView = (GridView)findViewById(R.id.gridView);
        gridView.setOnItemClickListener(moviePosterClickListener);

        MoviesOpenHelper moviesOpenHelper = new MoviesOpenHelper(this);
        sqLiteDatabase = moviesOpenHelper.getWritableDatabase();

        getSupportLoaderManager().initLoader(POPULAR_MOVIES_ASYNKTASK_ID,null,this);
        getMoviesFromTMDb(new HelperMethods(this).getSortMethod());
    }

    private final GridView.OnItemClickListener moviePosterClickListener = new GridView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Movie movie = (Movie) parent.getItemAtPosition(position);

            Intent intent = new Intent(getApplicationContext(), MovieDetailsActivity.class);
            intent.putExtra(getResources().getString(R.string.parcel_movie), movie);
            startActivity(intent);
        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override public boolean onPrepareOptionsMenu(Menu menu) {
        switch (mSort) {
            case POPULAR:
                menu.findItem(R.id.sort_by_popularity).setChecked(true);
                break;
            case TOP_RATED:
                menu.findItem(R.id.sort_by_rating).setChecked(true);
                break;
            case FAVORITE:
                menu.findItem(R.id.sort_by_favorite).setChecked(true);
                break;
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_by_popularity:
                item.setChecked(!item.isChecked());
                onSortChanged(Sort.POPULAR);
                new HelperMethods(this).updateSharedPrefs(getString(R.string.tmdb_sort_popular));
                getMoviesFromTMDb(new HelperMethods(this).getSortMethod());
                return true;
            case R.id.sort_by_rating:
                item.setChecked(!item.isChecked());
                onSortChanged(Sort.TOP_RATED);
                new HelperMethods(this).updateSharedPrefs(getString(R.string.tmdb_sort_toprated));
                getMoviesFromTMDb(new HelperMethods(this).getSortMethod());
                return true;
            case R.id.sort_by_favorite:
                item.setChecked(!item.isChecked());
                onSortChanged(Sort.FAVORITE);
                new HelperMethods(this).updateSharedPrefs(getString(R.string.pref_sort_favorite_key));
                Cursor favoriteMoviesFromDb = getFavoriteMovies(new HelperMethods(this).getSortMethod());
                if(favoriteMoviesFromDb!=null) {
                    ArrayList<Movie> movies = getFavoriteMoviesArrayObject(favoriteMoviesFromDb);
                    Movie[] moviesArray = movies.toArray(new Movie[movies.size()]);
                    gridView.setAdapter(new ImageAdapter(getApplicationContext(), moviesArray));
                }
                return true;
            default:
        }

        return super.onOptionsItemSelected(item);
    }

    private void onSortChanged(Sort sort) {
        mSort = sort;
    }

    private void getMoviesFromTMDb(String sortMethod) {
        if (new HelperMethods(this).isNetworkAvailable()) {
            // Key needed to get data from TMDb
            String apiKey = BuildConfig.API_KEY;
            URL theMovieDBApiCall = null;
            try {
               theMovieDBApiCall =  new NetworkUtils(apiKey).getApiUrl(sortMethod);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            Bundle bundle  = new Bundle();
            bundle.putString(getString(R.string.url_key),theMovieDBApiCall.toString());

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

    public Cursor getFavoriteMovies(String sortMethod){
        Cursor cursor = null;
        if(sortMethod.equals(getString(R.string.pref_sort_favorite_key))) {
            cursor = sqLiteDatabase.query(true,
                    MoviesContract.MoviesEntry.TABLE_MOVIES,
                    new String[]{MoviesContract.MoviesEntry.MOVIE_ID,
                            MoviesContract.MoviesEntry.MOVIE_ORIGINAL_TITLE,
                            MoviesContract.MoviesEntry.MOVIE_OVERVIEW,
                            MoviesContract.MoviesEntry.MOVIE_POSTER_PATH,
                            MoviesContract.MoviesEntry.MOVIE_RELEASE_DATE,
                            MoviesContract.MoviesEntry.MOVIE_VOTE_AVERAGE},
                    null,
                    null,
                    null,
                    null,
                    null,
                    null);
        }
        return cursor.equals("") ? null : cursor;
    }

    private ArrayList<Movie> getFavoriteMoviesArrayObject(Cursor cursor){
        try{
            ArrayList<Movie> moviesArray  = new ArrayList<Movie>();
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                Movie movie = null;
                int moviesId = cursor.getColumnIndexOrThrow("movies_id");
                int moviePosterPath = cursor.getColumnIndexOrThrow("movie_poster_path");
                int movieOverview = cursor.getColumnIndexOrThrow("movie_overview");
                int movieReleasePath = cursor.getColumnIndexOrThrow("movie_release_date");
                int movieOriginaltitle = cursor.getColumnIndexOrThrow("movie_original_title");
                int movieVoteAverage = cursor.getColumnIndexOrThrow("movie_vote_average");

                movie = new Movie();
                movie.setmovieId(cursor.getString(moviesId));
                URI uri = new URI(cursor.getString(moviePosterPath));
                String[] path = uri.getPath().split("/");
                String actualPosterId = path[path.length-1];
                movie.setPosterPath("/"+actualPosterId);
                movie.setOverview(cursor.getString(movieOverview));
                movie.setReleaseDate(cursor.getString(movieReleasePath));
                movie.setOriginaltitle(cursor.getString(movieOriginaltitle));
                movie.setVoteAverage(cursor.getDouble(movieVoteAverage));
                cursor.moveToNext();
                moviesArray.add(movie);
            }
            cursor.close();
            return moviesArray;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        currentScrollPosition = gridView.getFirstVisiblePosition();
        outState.putInt(CURRENT_SCROLL_POSITION,currentScrollPosition);
            Log.d("onSaveInstanceState",String.valueOf(currentScrollPosition));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState!=null) {
            currentScrollPosition = savedInstanceState.getInt(CURRENT_SCROLL_POSITION);
            Log.d("onRestoreInstanceState",String.valueOf(currentScrollPosition));
        }
    }

    @Override
    public Loader<Movie[]> onCreateLoader(int id, final Bundle args) {
            return new AsyncTaskLoader<Movie[]>(this) {

                Movie[] theMoviesDBResponse;

                @Override
                protected void onStartLoading() {
                    if(args == null){
                        return;
                    }
                    if(theMoviesDBResponse!=null){
                        deliverResult(theMoviesDBResponse);
                    }   else{
                        forceLoad();
                    }
                }

                @Override
                public Movie[] loadInBackground() {
                    String urlString = args.getString(getString(R.string.url_key));
                    if(urlString==null || TextUtils.isEmpty(urlString)){
                        return null;
                    }
                    try {
                        URL url = new URL(urlString);
                        String response = NetworkUtils.getResponseFromHttpUrl(url);
                        return NetworkUtils.getMoviesDataFromJson(response);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                public void deliverResult(Movie[] data) {
                    theMoviesDBResponse = data;
                    super.deliverResult(data);
                }
            };
    }

    @Override
    public void onLoadFinished(Loader<Movie[]> loader, Movie[] data) {
        if (null == data) {
            Toast.makeText(this, getString(R.string.no_data), Toast.LENGTH_LONG).show();
        } else {
            gridView.setAdapter(new ImageAdapter(getApplicationContext(),data));
            gridView.setSelection(currentScrollPosition);
            Toast.makeText(this, getString(R.string.data_found), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<Movie[]> loader) {

    }
}
