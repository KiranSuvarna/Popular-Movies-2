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
package com.thefuturemarketplace.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thefuturemarketplace.popularmovies.models.MovieReviews;

public class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewsAdapter.MovieReviewsAdapterViewHolder> {

    private MovieReviews[] movieReviews;

    public class MovieReviewsAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        public final TextView textviewAuthor;
        public final TextView textviewReview;

        public MovieReviewsAdapterViewHolder(View view) {
            super(view);
            textviewAuthor = (TextView) view.findViewById(R.id.tv_movie_review_author);
            textviewReview = (TextView) view.findViewById(R.id.tv_movie_review);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }
    }

    @Override
    public MovieReviewsAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.activity_movie_review_items;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new MovieReviewsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieReviewsAdapterViewHolder holder, int position) {
        MovieReviews review = movieReviews[position];
        holder.textviewAuthor.setText(review.getAuthor());
        holder.textviewReview.setText(review.getComment());
    }

    @Override
    public int getItemCount() {
        if (null == movieReviews) return 0;
        return movieReviews.length;
    }


    public void setMoviesReviews(MovieReviews[] movieReviews) {
        this.movieReviews = movieReviews;
        notifyDataSetChanged();
    }
}