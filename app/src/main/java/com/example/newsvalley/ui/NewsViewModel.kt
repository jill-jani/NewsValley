package com.example.newsvalley.ui

import androidx.lifecycle.ViewModel
import com.example.newsvalley.repository.NewsRepository

class NewsViewModel(
    val newRepository: NewsRepository
): ViewModel() {
}