package com.thefuturemarketplace.popularmovies;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.thefuturemarketplace.popularmovies.models.Movie;

import java.text.ParseException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by imKiran
 */

public class MovieDetailsActivity extends AppCompatActivity{

    private final String LOG_TAG = MovieDetailsActivity.class.getSimpleName();

    @BindView(R.id.imageview_moview) ImageView imageViewPoster;
    @BindView(R.id.textview_release_date_title) TextView textViewReleaseDateTitle;
    @BindView(R.id.textview_release_date_example)  TextView textViewReleasedate;
    @BindView(R.id.textview_vote_average_title)  TextView textViewVoteAverageTitle;
    @BindView(R.id.textview_vote_average_example) TextView textViewVoteAverage;
    @BindView(R.id.textview_original_title_example)  TextView textViewOriginalTitle;
    @BindView(R.id.textview_overview_example)  TextView textViewOverview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        Movie movie = intent.getParcelableExtra(getString(R.string.parcel_movie));

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
    }
}
