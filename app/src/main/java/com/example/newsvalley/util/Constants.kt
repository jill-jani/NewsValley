package com.example.newsvalley.util

import com.example.newsvalley.BuildConfig

class Constants {
    companion object {
        const val API_KEY = BuildConfig.API_KEY
        const val BASE_URL = "https://newsapi.org"
        const val SEARCH_NEWS_WAIT_TIME = 500L
        const val QUERY_PAGE_SIZE = 20
    }
}