package com.androiddevs.mvvmnewsapp.app.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.androiddevs.mvvmnewsapp.app.models.Article

@Dao
interface newsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: Article) : Long  //inserts or updates if already exists, returns id of the article

    @Query("SELECT * FROM articles")
    fun getAllArticles() : LiveData<List<Article>>

    @Delete
    suspend fun deleteArticle(article: Article)

}