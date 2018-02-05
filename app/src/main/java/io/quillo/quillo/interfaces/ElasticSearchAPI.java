package io.quillo.quillo.interfaces;

import java.util.Map;
import io.quillo.quillo.utils.HitsObject;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Query;

/**
 * Created by Tom on 2018/01/28.
 */

public interface ElasticSearchAPI {

    @GET("_search/")
    Call<HitsObject> search(
            @HeaderMap Map<String, String> headers,
            @Query("default_operator") String operator,
            @Query("from") int from,
            @Query("size") int size,
            @Query("sort") String sortBy,
            @Query("q") String query


    );
}
