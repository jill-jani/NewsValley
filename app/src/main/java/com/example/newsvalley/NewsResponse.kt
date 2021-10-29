package com.example.newsvalley

data class NewsResponse(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)