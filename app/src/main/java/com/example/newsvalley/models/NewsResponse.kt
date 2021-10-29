package com.example.newsvalley.models

import com.example.newsvalley.models.Article

data class NewsResponse(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)