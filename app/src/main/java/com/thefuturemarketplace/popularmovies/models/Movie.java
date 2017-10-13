package com.thefuturemarketplace.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by imKiran
 */

public class Movie implements Parcelable{
    private static String DATE_FORMAT = "yyyy-MM-dd";
    private String originaltitle;
    private String posterPath;
    private String overview;
    private Double voteAverage;
    private String releaseDate;

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
    }

    private Movie(Parcel in) {
        originaltitle = in.readString();
        posterPath = in.readString();
        overview = in.readString();
        voteAverage = (Double) in.readValue(Double.class.getClassLoader());
        releaseDate = in.readString();
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