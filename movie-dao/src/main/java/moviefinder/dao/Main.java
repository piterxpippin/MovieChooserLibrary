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
        
    }
}
