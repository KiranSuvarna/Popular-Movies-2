package com.thefuturemarketplace.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.thefuturemarketplace.popularmovies.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private GridView gridView;

    private Menu mMenu;

    private ImageAdapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridView = (GridView)findViewById(R.id.gridView);
        gridView.setOnItemClickListener(moviePosterClickListener);

        getMoviesFromTMDb(getSortMethod());
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
                R.string.pref_sort_pop_desc_key, // ID
                Menu.NONE, // Sort order: not relevant
                null) // No text to display
                .setVisible(false)
                .setIcon(R.drawable.star3x)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        // Same settings as the one above
        mMenu.add(Menu.NONE, R.string.pref_sort_vote_avg_desc_key, Menu.NONE, null)
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
            case R.string.pref_sort_pop_desc_key:
                updateSharedPrefs(getString(R.string.tmdb_sort_pop_desc));
                updateMenu();
                getMoviesFromTMDb(getSortMethod());
                return true;
            case R.string.pref_sort_vote_avg_desc_key:
                updateSharedPrefs(getString(R.string.tmdb_sort_vote_avg_desc));
                updateMenu();
                getMoviesFromTMDb(getSortMethod());
                return true;
            default:
        }

        return super.onOptionsItemSelected(item);
    }

    private void getMoviesFromTMDb(String sortMethod) {
        if (isNetworkAvailable()) {
            // Key needed to get data from TMDb
            String apiKey = getString(R.string.key_themoviedb);

            // Listener for when AsyncTask is ready to update UI
            OnTaskCompleted taskCompleted = new OnTaskCompleted() {
                @Override
                public void onFetchMoviesTaskCompleted(Movie[] movies) {
                    gridView.setAdapter(new ImageAdapter(getApplicationContext(), movies));
                }
            };

            // Execute task
            new FetchMovieAsyncTask(taskCompleted,apiKey).execute(sortMethod);
        } else {
            Toast.makeText(this, getString(R.string.error_need_internet), Toast.LENGTH_LONG).show();
        }
    }

    public  boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private String getSortMethod() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        return prefs.getString(getString(R.string.pref_sort_method_key),
                getString(R.string.tmdb_sort_pop_desc));
    }

    private void updateMenu() {
        String sortMethod = getSortMethod();

        if (sortMethod.equals(getString(R.string.tmdb_sort_pop_desc))) {
            mMenu.findItem(R.string.pref_sort_pop_desc_key).setVisible(false);
            mMenu.findItem(R.string.pref_sort_vote_avg_desc_key).setVisible(true);
        } else {
            mMenu.findItem(R.string.pref_sort_vote_avg_desc_key).setVisible(false);
            mMenu.findItem(R.string.pref_sort_pop_desc_key).setVisible(true);
        }
    }

    private void updateSharedPrefs(String sortMethod) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.pref_sort_method_key), sortMethod);
        editor.apply();
    }

    public class FetchMovieAsyncTask extends AsyncTask<String, Void, Movie[]> {

        private final String LOG_TAG = FetchMovieAsyncTask.class.getSimpleName();

        private  String mApiKey="";

        private  OnTaskCompleted mListener=null;

        public FetchMovieAsyncTask(OnTaskCompleted listener, String apiKey) {
            super();

            mListener = listener;
            mApiKey = apiKey;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Movie[] doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Holds data returned from the API
            String moviesJsonStr = null;

            try {
                URL url = getApiUrl(params);

                // Start connecting to get JSON
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder builder = new StringBuilder();

                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Adds '\n' at last line if not already there.
                    // This supposedly makes it easier to debug.
                    builder.append(line).append("\n");
                }

                if (builder.length() == 0) {
                    // No data found. Nothing more to do here.
                    return null;
                }

                moviesJsonStr = builder.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                // Tidy up: release url connection and buffered reader
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                // Make sense of the JSON data
                return getMoviesDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        /**
         * Extracts data from the JSON object and returns an Array of movie objects.
         *
         * @param moviesJsonStr JSON string to be traversed
         * @return Array of Movie objects
         * @throws JSONException
         */
        private Movie[] getMoviesDataFromJson(String moviesJsonStr) throws JSONException {
            // JSON tags
            final String TAG_RESULTS = "results";
            final String TAG_ORIGINAL_TITLE = "original_title";
            final String TAG_POSTER_PATH = "poster_path";
            final String TAG_OVERVIEW = "overview";
            final String TAG_VOTE_AVERAGE = "vote_average";
            final String TAG_RELEASE_DATE = "release_date";

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
            }

            return movies;
        }

        /**
         * Creates and returns an URL.
         *
         * @param parameters Parameters to be used in the API call
         * @return URL formatted with parameters for the API
         * @throws MalformedURLException
         */
        private URL getApiUrl(String[] parameters) throws MalformedURLException {
            final String TMDB_BASE_URL = "https://api.themoviedb.org/3/discover/movie?";
            final String SORT_BY_PARAM = "sort_by";
            final String API_KEY_PARAM = "api_key";
            final String ADULT_CONTENT = "include_adult";

            Uri builtUri = Uri.parse(TMDB_BASE_URL).buildUpon()
                    .appendQueryParameter(SORT_BY_PARAM, parameters[0])
                    .appendQueryParameter(API_KEY_PARAM, mApiKey)
                    .appendQueryParameter(ADULT_CONTENT,"false")
                    .build();

            return new URL(builtUri.toString());
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            super.onPostExecute(movies);
            mListener.onFetchMoviesTaskCompleted(movies);
        }
    }
}
