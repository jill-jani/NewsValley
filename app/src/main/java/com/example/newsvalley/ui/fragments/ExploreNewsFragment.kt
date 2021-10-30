package com.example.newsvalley.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.newsvalley.R
import com.example.newsvalley.ui.MainActivity
import com.example.newsvalley.ui.NewsViewModel

class ExploreNewsFragment: Fragment(R.layout.fragment_explore_news) {

    lateinit var viewModel: NewsViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
    }
}