package com.androiddevs.mvvmnewsapp.app.models

import com.androiddevs.mvvmnewsapp.app.models.Article

data class NewsResponse(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)