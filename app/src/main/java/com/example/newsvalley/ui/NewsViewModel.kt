package com.example.newsvalley.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsvalley.models.NewsResponse
import com.example.newsvalley.repository.NewsRepository
import com.example.newsvalley.util.Resources
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel(
    val newsRepository: NewsRepository
): ViewModel() {
    val topHeadlines: MutableLiveData<Resources<NewsResponse>> = MutableLiveData()
    val topHeadlinesPage = 1

    val searchResult: MutableLiveData<Resources<NewsResponse>> = MutableLiveData()
    val searchResultPage = 1

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
                return Resources.Success(resultResponse)
            }
        }
        return Resources.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resources<NewsResponse> {
        if(response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resources.Success(resultResponse)
            }
        }
        return Resources.Error(response.message())
    }
}