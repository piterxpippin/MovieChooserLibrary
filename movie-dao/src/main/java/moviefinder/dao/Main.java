package moviefinder.dao;

import moviefinder.dao.elasticsearch.ElasticSearchMovieSource;
import moviefinder.domain.Movie;
import moviefinder.domain.MovieType;

import java.util.List;

/**
 * Created by andrzej on 21.04.2016.
 */
public class Main {


    public static void main(String[] args) {

        ElasticSearchMovieSource elasticSearchMovieSource= new ElasticSearchMovieSource("localhost",9300);


        elasticSearchMovieSource.getMoviesWithGivenGenreSortedByNumberOfVotes(MovieType.DRAMA,0,0,50);
        elasticSearchMovieSource.getMoviesWithGivenGenreAndMinimumNumberOfRatings(MovieType.DRAMA,50,0,50);
      /*  List<Movie> moviesWithGivenGenreAndAverageRatingStratingFrom = elasticSearchMovieSource.getMoviesWithGivenGenreAndAverageRatingStratingFrom(MovieType.DRAMA, 0.0f, 0, 50, 0);
        System.out.print(moviesWithGivenGenreAndAverageRatingStratingFrom);*/
    }
}
