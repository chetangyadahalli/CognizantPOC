package com.cognizant.poc.networking;

import com.cognizant.poc.model.NewsResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface NewsApi {
    @GET("facts.json")
    Call<NewsResponse> getNewsList();
}
