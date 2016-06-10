package moviefinder.dao;


import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

interface Foo {
  @POST("_search")
  Call<ResponseBody> update(@Body RequestBody requestBody);
}