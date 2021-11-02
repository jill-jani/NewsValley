package com.example.newsvalley.repository

import com.example.newsvalley.api.RetrofitInstance
import com.example.newsvalley.db.ArticleDatabase
import com.example.newsvalley.models.Article
import retrofit2.Retrofit

class NewsRepository(
    val db: ArticleDatabase
) {
    suspend fun getTopHeadlines(countryCode: String, pageNumber: Int) =
        RetrofitInstance.api.getTopHeadlines(countryCode,pageNumber)

    suspend fun searchForNews(searchQuery: String, pageNumber: Int) =
        RetrofitInstance.api.searchNews(searchQuery,pageNumber)

    suspend fun upsertArticle(article: Article) = db.getDaoArticle().upsert(article)

    fun getSavedNews() = db.getDaoArticle().getAllArticles()

    suspend fun deleteArticle(article: Article) = db.getDaoArticle().deleteArticle(article)
}