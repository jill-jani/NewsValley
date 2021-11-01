package com.example.newsvalley.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsvalley.R
import com.example.newsvalley.adapters.NewsAdapter
import com.example.newsvalley.ui.MainActivity
import com.example.newsvalley.ui.NewsViewModel
import com.example.newsvalley.util.Constants.Companion.SEARCH_NEWS_WAIT_TIME
import com.example.newsvalley.util.Resources.*
import kotlinx.android.synthetic.main.fragment_search_news.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchNewsFragment: Fragment(R.layout.fragment_search_news) {
    lateinit var  viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    val TAG = "SearchNewsFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
        setUpRecyclerView()

        var job: Job? = null
        etSearch.addTextChangedListener { editableSearchQuery ->
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_NEWS_WAIT_TIME)
                editableSearchQuery?.let {
                    if(editableSearchQuery.toString().isNotEmpty()) {
                        viewModel.getSearchResult(editableSearchQuery.toString())
                    }
                }
            }
        }

        viewModel.searchResult.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles)
                    }
                }
                is Error -> {
                    hideProgressBar()
                    response.message?.let { message->
                        Log.e(TAG, "An error occurred: $message")
                    }
                }
                is Loading -> {
                    showProgressBar()
                }
            }
        })
    }
    private fun hideProgressBar() {
        paginationProgressBar.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        paginationProgressBar.visibility = View.VISIBLE
    }

    private fun setUpRecyclerView() {
        newsAdapter = NewsAdapter()
        rvSearchResult.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}