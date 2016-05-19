package moviefinder.domain;

import java.util.Set;

/**
 * Created by andrzej on 20.04.2016.
 */
public class Movie {


    private final long movieId;
    private final String title;
    private final Set<MovieType> movieTypes;
    private float rating;

    public Movie(long movieId, String title, Set<MovieType> movieTypes) {
        this.movieId = movieId;
        this.title = title;
        this.movieTypes = movieTypes;
    }
    public Movie(Set<MovieType> movieTypes, float rating, long movieId, String title) {
        this.movieTypes = movieTypes;
        this.rating = rating;
        this.movieId = movieId;
        this.title = title;
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
