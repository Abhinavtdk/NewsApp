package com.androiddevs.mvvmnewsapp.app.repositories

import com.androiddevs.mvvmnewsapp.app.api.RetrofitInstance
import com.androiddevs.mvvmnewsapp.app.db.ArticleDatabase

class NewsRepository (
    val db: ArticleDatabase
) {

    suspend fun getBreakingNews(countryCode: String, pageNumber : Int)  =

        RetrofitInstance.api.getBreakingNews(countryCode,pageNumber)


}