package com.thefuturemarketplace.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by imKiran
 */

public class Movie implements Parcelable{
    private String movieId;
    private static String DATE_FORMAT = "yyyy-MM-dd";
    private String originaltitle;
    private String posterPath;
    private String overview;
    private Double voteAverage;
    private String releaseDate;
    private boolean favorite = false;


    public Movie() {

    }

    public static void setDateFormat(String dateFormat) {
        DATE_FORMAT = dateFormat;
    }

    public void setOriginaltitle(String originaltitle) {
        this.originaltitle = originaltitle;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public static String getDateFormat() {

        return DATE_FORMAT;
    }

    public String getOriginaltitle() {
        return originaltitle;
    }

    public String getPosterPath() {
        final String TMDB_POSTER_BASE_URL = "https://image.tmdb.org/t/p/w185";

        return TMDB_POSTER_BASE_URL + posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getDetailedVoteAverage() {
        return String.valueOf(getVoteAverage()) + "/10";
    }

    public void setmovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getmovieId() {

        return movieId;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(originaltitle);
        dest.writeString(posterPath);
        dest.writeString(overview);
        dest.writeValue(voteAverage);
        dest.writeString(releaseDate);
        dest.writeString(movieId);
        dest.writeByte((byte) (favorite ? 1 : 0));
    }

    private Movie(Parcel in) {
        originaltitle = in.readString();
        posterPath = in.readString();
        overview = in.readString();
        voteAverage = (Double) in.readValue(Double.class.getClassLoader());
        releaseDate = in.readString();
        movieId = in.readString();
        favorite = in.readByte()!=0;
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}