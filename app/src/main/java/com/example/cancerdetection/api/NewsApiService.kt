package com.example.cancerdetection.api

import com.example.cancerdetection.model.NewsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
    @GET("top-headlines")
    fun getHealthNews(
        @Query("token") apiKey: String,
        @Query("lang") lang: String,
        @Query("topic") topic: String
    ): Call<NewsResponse>
}