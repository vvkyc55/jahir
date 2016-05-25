package com.example.kalio.adapterview;

import com.example.kalio.adapterview.models.News;
import com.example.kalio.adapterview.models.Rss;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RestApi {
    @GET("/api/get_recent_summary")
    Call<News> getNewsInfo();

    @GET("/ajax/services/feed/load")
    Call<Rss> getRssInfo(
            @Query("v") String version,
            @Query("q") String query);
}
