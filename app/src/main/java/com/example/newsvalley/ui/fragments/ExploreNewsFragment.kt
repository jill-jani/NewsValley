package com.example.newsvalley.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsvalley.R
import com.example.newsvalley.adapters.NewsAdapter
import com.example.newsvalley.ui.MainActivity
import com.example.newsvalley.ui.NewsViewModel
import com.example.newsvalley.util.Constants
import com.example.newsvalley.util.Resources
import kotlinx.android.synthetic.main.fragment_explore_news.*
import kotlinx.android.synthetic.main.fragment_explore_news.paginationProgressBar

class ExploreNewsFragment: Fragment(R.layout.fragment_explore_news) {

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    val TAG = "BreakingNewsFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
        setUpRecyclerView()
        Log.d("Explore News Fragment","Item clicked")

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(
                R.id.action_exploreNewsFragment_to_articleFragment,
                bundle
            )
        }
        viewModel.topHeadlines.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resources.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults / Constants.QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.topHeadlinesPage == totalPages
                        if(isLastPage) {
                            rvExploreNews.setPadding(0,0,0,0)
                        }
                    }
                }
                is Resources.Error -> {
                    hideProgressBar()
                    response.message?.let { message->
                        Toast.makeText(activity, "An error occurred: $message",Toast.LENGTH_LONG).show()
                    }
                }
                is Resources.Loading -> {
                    showProgressBar()
                }
            }
        })
    }

    private fun hideProgressBar() {
        paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling

            if(shouldPaginate) {
                viewModel.getTopHeadlines("in")
                isScrolling = false
            }
        }
    }

    private fun setUpRecyclerView() {
        newsAdapter = NewsAdapter()
        rvExploreNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@ExploreNewsFragment.scrollListener)
        }
    }
}