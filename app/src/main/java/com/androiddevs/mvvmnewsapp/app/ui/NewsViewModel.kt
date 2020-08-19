package com.androiddevs.mvvmnewsapp.app.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.core.content.getSystemService
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevs.mvvmnewsapp.app.NewsApplication
import com.androiddevs.mvvmnewsapp.app.models.Article
import com.androiddevs.mvvmnewsapp.app.models.NewsResponse
import com.androiddevs.mvvmnewsapp.app.repositories.NewsRepository
import com.androiddevs.mvvmnewsapp.app.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel (
    application: Application,
    val repository: NewsRepository
) : AndroidViewModel(application) {

    val breakingNews : MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage = 1
    var breakingNewsResponse : NewsResponse? =null

    val searchNews : MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var seacrhNewsResponse : NewsResponse? = null

    init {
        getBreakingNews("in")
    }

    fun getBreakingNews(countryCode : String) = viewModelScope.launch {
        safeBreakingNewsCall(countryCode)
    }

    fun searchNews(searchQuery : String) = viewModelScope.launch {
        safeSearchNewsCall(searchQuery)
    }


    private fun handleBreakingNewsResponse(response: Response<NewsResponse>) : Resource<NewsResponse> {
        if(response.isSuccessful) {
            response.body()?.let {resultResponse ->
                breakingNewsPage++
                if(breakingNewsResponse == null) {
                    breakingNewsResponse = resultResponse
                } else {
                    val oldArticles = breakingNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(breakingNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>) : Resource<NewsResponse> {
        if(response.isSuccessful) {
            response.body()?.let {resultResponse ->
                searchNewsPage++
                if(seacrhNewsResponse == null) {
                    seacrhNewsResponse = resultResponse
                } else {
                    val oldSearchArticles = seacrhNewsResponse?.articles
                    val newSearchArticles = resultResponse.articles
                    oldSearchArticles?.addAll(newSearchArticles)
                }
                return Resource.Success(seacrhNewsResponse?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun saveAricle(article: Article) = viewModelScope.launch {
        repository.upsert(article)
    }

    fun getSavedNews() = repository.getAllArticles()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        repository.deleteArticle(article)
    }


    private suspend fun safeBreakingNewsCall(countryCode: String){
        breakingNews.postValue(Resource.Loading())
        try {
            if(hasInternetConnection()) {
                val response = repository.getBreakingNews(countryCode, breakingNewsPage)
                breakingNews.postValue(handleBreakingNewsResponse(response = response))
            } else {
                breakingNews.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable){
            when(t) {
                is IOException -> breakingNews.postValue(Resource.Error("Network Failure"))
                else -> breakingNews.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private suspend fun safeSearchNewsCall(searchQuery: String){
        searchNews.postValue(Resource.Loading())
        try {
            if(hasInternetConnection()) {
                val response = repository.searchNews(searchQuery, searchNewsPage)
                searchNews.postValue(handleSearchNewsResponse(response = response))
            } else {
                searchNews.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable){
            when(t) {
                is IOException -> searchNews.postValue(Resource.Error("Network Failure"))
                else -> searchNews.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private fun hasInternetConnection() : Boolean {
        val connectivityManager = getApplication<NewsApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo.run {
                return  when(type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }

}