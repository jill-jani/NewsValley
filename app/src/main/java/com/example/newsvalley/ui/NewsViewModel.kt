package com.example.newsvalley.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsvalley.NewsApplication
import com.example.newsvalley.models.Article
import com.example.newsvalley.models.NewsResponse
import com.example.newsvalley.repository.NewsRepository
import com.example.newsvalley.util.Resources
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.Response

class NewsViewModel(
    val app: Application,
    val newsRepository: NewsRepository
): AndroidViewModel(app) {
    val topHeadlines: MutableLiveData<Resources<NewsResponse>> = MutableLiveData()
    var topHeadlinesPage = 1
    var topHeadlinesResponse: NewsResponse? = null

    val searchResult: MutableLiveData<Resources<NewsResponse>> = MutableLiveData()
    var searchResultPage = 1
    var searchResultResponse: NewsResponse? = null

    init {
        getTopHeadlines("in")
    }

    fun getTopHeadlines(countryCode: String) = viewModelScope.launch {
        safeTopHeadlinesCall(countryCode)
    }

    fun getSearchResult(searchQuery: String) = viewModelScope.launch {
        safeSearchNewsCall(searchQuery)
    }

    private fun handleTopHeadlinesResponse(response: Response<NewsResponse>): Resources<NewsResponse> {
        if(response.isSuccessful) {
            response.body()?.let { resultResponse ->
                topHeadlinesPage++
                if(topHeadlinesResponse == null) {
                    topHeadlinesResponse = resultResponse
                } else {
                    val oldArticles = topHeadlinesResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resources.Success(topHeadlinesResponse ?: resultResponse)
            }
        }
        return Resources.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resources<NewsResponse> {
        if(response.isSuccessful) {
            response.body()?.let { resultResponse ->
                searchResultPage++
                if(searchResultResponse == null) {
                    searchResultResponse = resultResponse
                } else {
                    val oldArticles = searchResultResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resources.Success(searchResultResponse ?: resultResponse)
            }
        }
        return Resources.Error(response.message())
    }

    fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepository.upsertArticle(article)
    }

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }

    fun getSavedArticles() = newsRepository.getSavedNews()

    private suspend fun safeTopHeadlinesCall(countryCode: String) {
        topHeadlines.postValue(Resources.Loading())
        try {
            if(hasInternetConnection()) {
                val response = newsRepository.getTopHeadlines(countryCode, topHeadlinesPage)
                topHeadlines.postValue(handleTopHeadlinesResponse(response))
            } else {
                topHeadlines.postValue(Resources.Error("No internet connection"))
            }
        } catch (t:Throwable) {
            when(t) {
                is IOException -> topHeadlines.postValue(Resources.Error("Network Failure"))
                else -> topHeadlines.postValue(Resources.Error("Conversion Error"))
            }
        }
    }

    private suspend fun safeSearchNewsCall(searchQuery: String) {
        searchResult.postValue(Resources.Loading())
        try {
            if(hasInternetConnection()) {
                val response = newsRepository.searchForNews(searchQuery,searchResultPage)
                searchResult.postValue(handleSearchNewsResponse(response))
            } else {
                searchResult.postValue(Resources.Error("No internet connection"))
            }
        } catch (t:Throwable) {
            when(t) {
                is IOException -> searchResult.postValue(Resources.Error("Network Failure"))
                else -> searchResult.postValue(Resources.Error("Conversion Error"))
            }
        }
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<NewsApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when(type) {
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