package moviefinder.domain;

/**
 * Created by andrzej on 20.04.2016.
 */
public class Rating {

    private final double value;
    private final long movieId;
    private final long ratingId;
    private String user;

    public Rating(double value, long movieId, long ratingId) {
        this.value = value;
        this.movieId = movieId;
        this.ratingId = ratingId;
    }
}
