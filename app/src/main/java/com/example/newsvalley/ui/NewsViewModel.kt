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
    val newRepository: NewsRepository
): ViewModel() {
    val topHeadlines: MutableLiveData<Resources<NewsResponse>> = MutableLiveData()
    val topHeadlinesPage = 1

    init {
        getTopHeadlines("us")
    }

    fun getTopHeadlines(countryCode: String) = viewModelScope.launch {
        topHeadlines.postValue(Resources.Loading())
        val response = newRepository.getTopHeadlines(countryCode, topHeadlinesPage)
        topHeadlines.postValue(handleTopHeadlinesResponse(response))
    }

    private fun handleTopHeadlinesResponse(response: Response<NewsResponse>): Resources<NewsResponse> {
        if(response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resources.Success(resultResponse)
            }
        }
        return Resources.Error(response.message())
    }
}