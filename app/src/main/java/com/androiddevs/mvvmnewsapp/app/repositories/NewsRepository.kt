package com.androiddevs.mvvmnewsapp.app.repositories

import com.androiddevs.mvvmnewsapp.app.api.RetrofitInstance
import com.androiddevs.mvvmnewsapp.app.db.ArticleDatabase
import com.androiddevs.mvvmnewsapp.app.models.Article

class NewsRepository(
    val db: ArticleDatabase
) {

    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        RetrofitInstance.api.getBreakingNews(countryCode, pageNumber)

    suspend fun searchNews(searchQuery: String, pageNumber: Int) =
        RetrofitInstance.api.searchNews(searchQuery, pageNumber)

    suspend fun upsert(article: Article) =
        db.getArticleDao().upsert(article)

    fun getAllArticles() =
        db.getArticleDao().getAllArticles()

    suspend fun deleteArticle(article: Article) =
        db.getArticleDao().deleteArticle(article)

}