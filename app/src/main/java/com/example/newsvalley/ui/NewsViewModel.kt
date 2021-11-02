package com.example.newsvalley.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsvalley.models.Article
import com.example.newsvalley.models.NewsResponse
import com.example.newsvalley.repository.NewsRepository
import com.example.newsvalley.util.Resources
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel(
    val newsRepository: NewsRepository
): ViewModel() {
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
        topHeadlines.postValue(Resources.Loading())
        val response = newsRepository.getTopHeadlines(countryCode, topHeadlinesPage)
        topHeadlines.postValue(handleTopHeadlinesResponse(response))
    }

    fun getSearchResult(searchQuery: String) = viewModelScope.launch {
        searchResult.postValue(Resources.Loading())
        val response = newsRepository.searchForNews(searchQuery,searchResultPage)
        searchResult.postValue(handleSearchNewsResponse(response))
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
}