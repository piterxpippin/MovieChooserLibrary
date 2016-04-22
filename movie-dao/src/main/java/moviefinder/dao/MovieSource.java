package moviefinder.dao;

import moviefinder.domain.Movie;
import moviefinder.domain.MovieType;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by andrzej on 20.04.2016.
 */
public interface MovieSource {

    Movie getMovieById(long movieId);

    Movie getMovieByTitle(String title);

    List<Movie> getMoviesWithGivenGenre(MovieType movieTypes, int page, int itemsPerPage) throws ExecutionException, InterruptedException;

    List<Movie> getMoviesWithGivenGenreAndMinimumNumberOfRatings(MovieType movieTypes, int minNumberOfRatings, int page, int itemsPerPage) throws ExecutionException, InterruptedException;

    List<Movie> getMoviesWithGivenGenreSortedByNumberOfVotes(MovieType movieTypes, int minNumberOfRatings, int page, int itemsPerPage);

    List<Movie> getMoviesWithGivenGenreAndAverageRatingStratingFrom(MovieType movieTypes, float minValue, int page, int itemsPerPage, int minNumberOfRatings);

}
