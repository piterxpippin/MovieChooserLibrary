package moviefinder.dao.elasticsearch;

import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import moviefinder.dao.MovieSource;
import moviefinder.domain.Movie;
import moviefinder.domain.MovieType;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.fieldvaluefactor.FieldValueFactorFunctionBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Created by andrzej on 20.04.2016.
 */
public class ElasticSearchMovieSource implements MovieSource {

    private String host;
    private Client client = null;

    public ElasticSearchMovieSource(String host, int port) {
        this.host = host;
        try {
            client = TransportClient.builder().build()
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port));
        } catch (UnknownHostException e) {
            Throwables.propagate(e);
        }

    }

    public Movie getMovieById(long movieId) {
        throw new NotImplementedException();
    }

    public Movie getMovieByTitle(String title) {
        throw new NotImplementedException();
    }

    public List<Movie> getMoviesWithGivenGenre(MovieType movieTypes, int page, int itemsPerPage) {
        SearchResponse movies = getMovies(movieTypes, 0, page, itemsPerPage);

        return mapResponseToMovieList(movies);
    }

    private List<Movie> mapResponseToMovieList(SearchResponse movies) {
        List<Movie> result = Lists.newArrayList();

        for (SearchHit hits : movies.getHits()) {
            Map<String, SearchHitField> fields = hits.getFields();
            hits.getScore();
            long id = Long.parseLong(Iterables.getOnlyElement(fields.get("id").getValues()).toString());
            String title = Iterables.getOnlyElement(fields.get("title").getValues()).toString();
            Set<MovieType> types = new HashSet<MovieType>();
            for (Object val: fields.get("genres").getValues()){
                types.add(MovieType.valueOf(val.toString().replace('-','_').toUpperCase()));
            }


            Movie movie = new Movie(types,hits.getScore(),id,title);
            result.add(movie);


        }
        return result;
    }

    private SearchResponse getMovies(MovieType movieTypes, int numberOfRatings, int page, int itemsPerPage) {
        try {
            return client.prepareSearch()
                    .setQuery(QueryBuilders.boolQuery()
                            .must(QueryBuilders.hasChildQuery("rating", QueryBuilders.functionScoreQuery().add(new FieldValueFactorFunctionBuilder("value").factor(1.0f)))
                                    .minChildren(numberOfRatings).scoreMode("avg").queryName("value"))
                            .must(QueryBuilders.matchQuery("genres", Strings.toCamelCase(movieTypes.toString())).boost(0))
                    ).addField("title").addField("genres").addField("id").setFrom(page * itemsPerPage).setSize(itemsPerPage).setTypes("movie")
                    .execute().get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }


    private SearchResponse getMoviesSortedByNumberOFRatings(MovieType movieTypes, int numberOfRatings, int page, int itemsPerPage) {
        try {
            return client.prepareSearch()
                    .setQuery(QueryBuilders.boolQuery()
                            .must(QueryBuilders.hasChildQuery("rating", QueryBuilders.matchAllQuery())
                                    .minChildren(numberOfRatings).scoreMode("sum").queryName("value"))
                            .must(QueryBuilders.matchQuery("genres", Strings.toCamelCase(movieTypes.toString())).boost(0))
                    ).addField("title").addField("genres").addField("id").setFrom(page * itemsPerPage).setSize(itemsPerPage).setTypes("movie")
                    .execute().get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Movie> getMoviesWithGivenGenreAndMinimumNumberOfRatings(MovieType movieTypes, int minNumberOfRatings, int page, int itemsPerPage) {
        SearchResponse movies = getMovies(movieTypes, minNumberOfRatings, page, itemsPerPage);
        return mapResponseToMovieList(movies);
    }

    public List<Movie> getMoviesWithGivenGenreSortedByNumberOfVotes(MovieType movieTypes, int minNumberOfRatings, int page, int itemsPerPage) {
        SearchResponse movies = getMoviesSortedByNumberOFRatings(movieTypes, minNumberOfRatings, page, itemsPerPage);
        return mapResponseToMovieList(movies);
    }

    public List<Movie> getMoviesWithGivenGenreAndAverageRatingStratingFrom(MovieType movieTypes, float minValue, int page, int itemsPerPage, int minNumberOfRatings) {
        SearchResponse movies = getMovies(movieTypes, minValue, minNumberOfRatings, page, itemsPerPage);
        return mapResponseToMovieList(movies);
    }


    private SearchResponse getMovies(MovieType movieTypes, float minscore, int numberOfRatings, int page, int itemsPerPage) {
        try {
            return client.prepareSearch()
                    .setQuery(QueryBuilders.boolQuery()
                            .must(QueryBuilders.hasChildQuery("rating", QueryBuilders.functionScoreQuery().add(new FieldValueFactorFunctionBuilder("value").factor(1.0f)))
                                    .minChildren(numberOfRatings).scoreMode("avg").queryName("value"))
                            .must(QueryBuilders.matchQuery("genres", Strings.toCamelCase(movieTypes.toString())).boost(0))
                    ).addField("title").addField("genres").addField("id").setFrom(page * itemsPerPage).setSize(itemsPerPage).setMinScore(minscore).setTypes("movie")
                    .execute().get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
