package com.androiddevs.mvvmnewsapp.app.api

import com.androiddevs.mvvmnewsapp.app.models.NewsResponse
import com.androiddevs.mvvmnewsapp.app.util.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsAPI  {

    @GET("v2/top-headlines")
    suspend fun getBreakingNews (
        @Query("country")
        countryCode : String = "in",
        @Query("page")
        pageNumber : Int = 1,   //only few articles are requested at once, if the next set of articles is required, another request is called
        @Query("apiKey")
        newsApiKey : String = API_KEY
    ) : Response<NewsResponse>

    @GET("v2/everything")
    suspend fun searchNews (
        @Query("q")
        searchQuery : String,
        @Query("page")
        pageNumber : Int = 1,   //only few articles are requested at once, if the next set of articles is required, another request is called
        @Query("apiKey")
        newsApiKey : String = API_KEY
    ) : Response<NewsResponse>
}