package awais.instagrabber.repositories;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface GraphQLRepository {
    @GET("/graphql/query/")
    Call<String> fetch(@QueryMap(encoded = true) Map<String, String> queryParams);

    @GET("/{username}/?__a=1")
    Call<String> getUser(@Path("username") String username);
}
