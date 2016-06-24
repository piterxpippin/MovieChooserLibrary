package moviefinder.domain;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by andrzej on 20.04.2016.
 */
public class Movie implements Serializable {


    private final long movieId;
    private final String title;
    private final Set<MovieType> movieTypes;
    private final String imdbId;
    private final String tmdbId;
    private float rating;

    public Movie(long movieId, String title, Set<MovieType> movieTypes, String imdbId, String tmdbId) {
        this.movieId = movieId;
        this.title = title;
        this.movieTypes = movieTypes;
        this.imdbId = imdbId;
        this.tmdbId = tmdbId;

    }

    public Movie(Set<MovieType> movieTypes, float rating, long movieId, String title, String imdbId, String tmdbId) {
        this.movieTypes = movieTypes;
        this.rating = rating;
        this.movieId = movieId;
        this.title = title;
        this.imdbId = imdbId;
        this.tmdbId = tmdbId;
    }

    public String getImdbId() {
        return imdbId;
    }

    public String getTmdbId() {
        return tmdbId;
    }

    public long getMovieId() {
        return movieId;
    }

    public String getTitle() {
        return title;
    }

    public Set<MovieType> getMovieTypes() {
        return movieTypes;
    }

    public float getRating() {
        return rating;
    }
}
