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
    private float rating;
    private final long imdbId;
    private final long tmdbId;

    public Movie(long movieId, String title, Set<MovieType> movieTypes, long imdbId, long tmdbId) {
        this.movieId = movieId;
        this.title = title;
        this.movieTypes = movieTypes;
        this.imdbId = imdbId;
        this.tmdbId = tmdbId;
    }
    public Movie(Set<MovieType> movieTypes, float rating, long movieId, String title, long imdbId, long tmdbId) {
        this.movieTypes = movieTypes;
        this.rating = rating;
        this.movieId = movieId;
        this.title = title;
        this.imdbId = imdbId;
        this.tmdbId = tmdbId;
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
