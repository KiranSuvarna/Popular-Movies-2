package com.thefuturemarketplace.popularmovies.event;

/**
 * Created by imKiran
 */
public class FavoriteChangeEvent {

    public final boolean isFavorite;

    public FavoriteChangeEvent(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public boolean isFavoriteChanged() {
        return isFavorite;
    }

}
