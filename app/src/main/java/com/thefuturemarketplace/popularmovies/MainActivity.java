package com.thefuturemarketplace.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.thefuturemarketplace.popularmovies.models.Movie;
import com.thefuturemarketplace.popularmovies.utils.HelperMethods;
import com.thefuturemarketplace.popularmovies.utils.NetworkUtils;

import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Movie[]>{

    private static final int POPULAR_MOVIES_ASYNKTASK_ID = 5;

    private GridView gridView;

    private Menu mMenu;

    private ImageAdapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridView = (GridView)findViewById(R.id.gridView);
        gridView.setOnItemClickListener(moviePosterClickListener);

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
        getMenuInflater().inflate(R.menu.sort_methods, mMenu);

        // Make menu items accessible
        mMenu = menu;

        // Add menu items
        mMenu.add(Menu.NONE, // No group
                R.string.pref_sort_toprated_key, // ID
                Menu.NONE, // Sort order: not relevant
                null) // No text to display
                .setVisible(false)
                .setIcon(R.drawable.star3x)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        // Same settings as the one above
        mMenu.add(Menu.NONE,
                R.string.pref_sort_popular_key,
                Menu.NONE, null)
                .setVisible(false)
                .setIcon(R.drawable.people3x)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        // Update menu to show relevant items
        updateMenu();

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.string.pref_sort_popular_key:
                new HelperMethods(this).updateSharedPrefs(getString(R.string.tmdb_sort_popular));
                updateMenu();
                getMoviesFromTMDb(new HelperMethods(this).getSortMethod());
                return true;
            case R.string.pref_sort_toprated_key:
                new HelperMethods(this).updateSharedPrefs(getString(R.string.tmdb_sort_toprated));
                updateMenu();
                getMoviesFromTMDb(new HelperMethods(this).getSortMethod());
                return true;
            default:
        }

        return super.onOptionsItemSelected(item);
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

    private void updateMenu() {
        String sortMethod = new HelperMethods(this).getSortMethod();

        if (sortMethod.equals(getString(R.string.tmdb_sort_popular))) {
            mMenu.findItem(R.string.pref_sort_popular_key).setVisible(false);
            mMenu.findItem(R.string.pref_sort_toprated_key).setVisible(true);
        } else {
            mMenu.findItem(R.string.pref_sort_toprated_key).setVisible(false);
            mMenu.findItem(R.string.pref_sort_popular_key).setVisible(true);
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
            Toast.makeText(this, getString(R.string.data_found), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<Movie[]> loader) {

    }
}
