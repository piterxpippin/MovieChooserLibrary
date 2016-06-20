package moviefinder.dao;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import moviefinder.domain.Movie;
import moviefinder.domain.MovieType;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by andrzej on 09.06.2016.
 */
public class RestSource implements MovieSource {

    private final Retrofit retrofit;
    private final Foo apiService;
    String basicUrl;

    public RestSource(String basicUrl) {
        this.basicUrl = basicUrl;
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .build();
        retrofit = new Retrofit.Builder()
                .baseUrl(basicUrl)
                .client(client)
                .build();

        apiService =
                retrofit.create(Foo.class);
    }

    @Override
    public Movie getMovieById(long movieId) {
        String jsonNodeHttpResponse = getJsonNodeHttpResponse(Long.toString(movieId), "id");
        return convertBodyToMovies(jsonNodeHttpResponse).get(0);
    }

    private String getJsonNodeHttpResponse(String value, String field) {

        String strRequestBody = String.format("{\n" +
                "  \"query\" : {\n" +
                "    \"bool\" : {\n" +
                "      \"must\" : [ {\n" +
                "        \"has_child\" : {\n" +
                "          \"query\" : {\n" +
                "            \"function_score\" : {\n" +
                "              \"functions\" : [ {\n" +
                "                \"field_value_factor\" : {\n" +
                "                  \"field\" : \"value\",\n" +
                "                  \"factor\" : 1.0\n" +
                "                }\n" +
                "              } ]\n" +
                "            }\n" +
                "          },\n" +
                "          \"child_type\" : \"rating\",\n" +
                "          \"score_mode\" : \"avg\",\n" +
                "          \"_name\" : \"value\"\n" +
                "        }\n" +
                "      }, {\n" +
                "        \"match\" : {\n" +
                "          \"%s\" : {\n" +
                "            \"query\" : \"%s\",\n" +
                "            \"type\" : \"boolean\",\n" +
                "            \"boost\" : 0.0\n" +
                "          }\n" +
                "        }\n" +
                "      } ]\n" +
                "    }\n" +
                "  },\n" +
                "  \"fields\" : [ \"title\", \"tmdbId\", \"imdbId\", \"genres\", \"id\" ]\n" +
                "}", field, value);
        return getRespAsString(strRequestBody);

    }

    private String getRespAsString(String strRequestBody) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), strRequestBody);
        Call<ResponseBody> call = apiService.update(requestBody);


        Response<ResponseBody> response = null;
        try {
            response = call.execute();
            if (response.isSuccessful()) {
                String strResponseBody = response.body().string();
                return strResponseBody;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Movie> getMovieByTitle(String title) {

        String s = getJsonNodeHttpResponse(title, "title");
        return convertBodyToMovies(s);


    }

    private List<Movie> convertBodyToMovies(String body) {

        Gson gson = new Gson();
        JsonElement element = gson.fromJson(body, JsonElement.class);
        JsonObject jsonObj = element.getAsJsonObject();
        List<Movie> movies = convertToMovies(jsonObj);
        return movies;
    }

    private List<Movie> convertToMovies(JsonObject jsonObj) {
        List<Movie> movies = new ArrayList<Movie>();
        JsonObject hits = jsonObj.getAsJsonObject("hits");
        int total = hits.get("total").getAsInt();
        JsonArray hits1 = hits.getAsJsonArray("hits");

        for (int i = 0; i < hits1.size(); i++) {
            JsonObject jsonElement = hits1.get(i).getAsJsonObject().get("fields").getAsJsonObject();
            long movieId = jsonElement.get("id").getAsJsonArray().get(0).getAsLong();
            String title = jsonElement.get("title").getAsJsonArray().get(0).getAsString();
            Set<MovieType> types = new HashSet<MovieType>();
            JsonArray genres = jsonElement.get("genres").getAsJsonArray();
            for (int j = 0; j < genres.size(); j++) {
                types.add(MovieType.valueOf(genres.get(j).getAsString().replace('-', '_').toUpperCase()));
            }
            long imdbId = jsonElement.get("imdbId").getAsJsonArray().get(0).getAsLong();
            JsonArray tmdbId1 = jsonElement.get("tmdbId").getAsJsonArray();
            long tmdbId = tmdbId1.size() > 0 ? tmdbId1.get(0).getAsLong() : 0;
            movies.add(new Movie(movieId, title, types, imdbId, tmdbId));
        }


        return movies;
    }

    @Override
    public List<Movie> getMoviesWithGivenGenre(MovieType movieTypes, int page, int itemsPerPage) {
        return getMovies(movieTypes, 0, page, itemsPerPage);
    }

    private List<Movie> getMovies(MovieType movieTypes, int i, int page, int itemsPerPage) {
        String request = String.format("{\n" +
                "  \"from\" : %s,\n" +
                "  \"size\" : %s,\n" +
                "  \"query\" : {\n" +
                "    \"bool\" : {\n" +
                "      \"must\" : [ {\n" +
                "        \"has_child\" : {\n" +
                "          \"query\" : {\n" +
                "            \"function_score\" : {\n" +
                "              \"functions\" : [ {\n" +
                "                \"field_value_factor\" : {\n" +
                "                  \"field\" : \"value\",\n" +
                "                  \"factor\" : 1.0\n" +
                "                }\n" +
                "              } ]\n" +
                "            }\n" +
                "          },\n" +
                "          \"child_type\" : \"rating\",\n" +
                "          \"score_mode\" : \"avg\",\n" +
                "          \"min_children\" : %s,\n" +
                "          \"_name\" : \"value\"\n" +
                "        }\n" +
                "      }, {\n" +
                "        \"match\" : {\n" +
                "          \"genres\" : {\n" +
                "            \"query\" : \"%s\",\n" +
                "            \"type\" : \"boolean\",\n" +
                "            \"boost\" : 0.0\n" +
                "          }\n" +
                "        }\n" +
                "      } ]\n" +
                "    }\n" +
                "  },\n" +
                "  \"fields\" : [ \"title\", \"tmdbId\", \"imdbId\", \"genres\", \"id\" ]\n" +
                "}", Integer.toString(itemsPerPage * page), Integer.toString(itemsPerPage), Integer.toString(i), (movieTypes.toString()));
        String response = getRespAsString(request);
        return convertBodyToMovies(response);
    }


    public List<Movie> getMoviesWithGivenGenreAndMinimumNumberOfRatings(MovieType movieTypes, int minNumberOfRatings, int page, int itemsPerPage) {
        return getMovies(movieTypes, minNumberOfRatings, page, itemsPerPage);

    }

    public List<Movie> getMoviesWithGivenGenreSortedByNumberOfVotes(MovieType movieTypes, int minNumberOfRatings, int page, int itemsPerPage) {
        return getMoviesSortedByNumberOFRatings(movieTypes, minNumberOfRatings, page, itemsPerPage);

    }

    private List<Movie> getMoviesSortedByNumberOFRatings(MovieType movieTypes, int minNumberOfRatings, int page, int itemsPerPage) {
        String query = String.format("{\n" +
                "  \"from\" : %s,\n" +
                "  \"size\" : %s,\n" +
                "  \"query\" : {\n" +
                "    \"bool\" : {\n" +
                "      \"must\" : [ {\n" +
                "        \"has_child\" : {\n" +
                "          \"query\" : {\n" +
                "            \"match_all\" : { }\n" +
                "          },\n" +
                "          \"child_type\" : \"rating\",\n" +
                "          \"score_mode\" : \"sum\",\n" +
                "          \"min_children\" : %s,\n" +
                "          \"_name\" : \"value\"\n" +
                "        }\n" +
                "      }, {\n" +
                "        \"match\" : {\n" +
                "          \"genres\" : {\n" +
                "            \"query\" : \"%s\",\n" +
                "            \"type\" : \"boolean\",\n" +
                "            \"boost\" : 0.0\n" +
                "          }\n" +
                "        }\n" +
                "      } ]\n" +
                "    }\n" +
                "  },\n" +
                "  \"fields\" : [ \"title\", \"tmdbId\", \"imdbId\", \"genres\", \"id\" ]\n" +
                "}", Integer.toString(itemsPerPage * page), Integer.toString(itemsPerPage), Integer.toString(minNumberOfRatings), (movieTypes.toString()));
        String response = getRespAsString(query);
        return convertBodyToMovies(response);


    }

    public List<Movie> getMoviesWithGivenGenreAndAverageRatingStratingFrom(MovieType movieTypes, float minValue, int page, int itemsPerPage, int minNumberOfRatings) {
        return getMovies(movieTypes, minValue, minNumberOfRatings, page, itemsPerPage);

    }

    private List<Movie> getMovies(MovieType movieTypes, float minValue, int minNumberOfRatings, int page, int itemsPerPage) {
        String request = String.format("{\n" +
                "  \"from\" : %s,\n" +
                "  \"size\" : %s,\n" +
                "  \"query\" : {\n" +
                "    \"bool\" : {\n" +
                "      \"must\" : [ {\n" +
                "        \"has_child\" : {\n" +
                "          \"query\" : {\n" +
                "            \"function_score\" : {\n" +
                "              \"functions\" : [ {\n" +
                "                \"field_value_factor\" : {\n" +
                "                  \"field\" : \"value\",\n" +
                "                  \"factor\" : 1.0\n" +
                "                }\n" +
                "              } ]\n" +
                "            }\n" +
                "          },\n" +
                "          \"child_type\" : \"rating\",\n" +
                "          \"score_mode\" : \"avg\",\n" +
                "          \"min_children\" : %s,\n" +
                "          \"_name\" : \"value\"\n" +
                "        }\n" +
                "      }, {\n" +
                "        \"match\" : {\n" +
                "          \"genres\" : {\n" +
                "            \"query\" : \"%s\",\n" +
                "            \"type\" : \"boolean\",\n" +
                "            \"boost\" : 0.0\n" +
                "          }\n" +
                "        }\n" +
                "      } ]\n" +
                "    }\n" +
                "  },\n" +
                "  \"min_score\" : %s,\n" +
                "  \"fields\" : [ \"title\", \"tmdbId\", \"imdbId\", \"genres\", \"id\" ]\n" +
                "}", Integer.toString(itemsPerPage * page), Integer.toString(itemsPerPage), Integer.toString(minNumberOfRatings), movieTypes.toString().replace("_", "-"), minValue);

        String response = getRespAsString(request);
        return convertBodyToMovies(response);
    }
}
