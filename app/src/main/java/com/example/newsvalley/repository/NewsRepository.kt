package com.example.newsvalley.repository

import com.example.newsvalley.api.RetrofitInstance
import com.example.newsvalley.db.ArticleDatabase
import retrofit2.Retrofit

class NewsRepository(
    val db: ArticleDatabase
) {
    suspend fun getTopHeadlines(countryCode: String, pageNumber: Int) =
        RetrofitInstance.api.getTopHeadlines(countryCode,pageNumber)
}