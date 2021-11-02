package com.example.newsvalley.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.newsvalley.R
import com.example.newsvalley.ui.MainActivity
import com.example.newsvalley.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_article.*

class ArticleFragment: Fragment(R.layout.fragment_article) {

    lateinit var viewModel: NewsViewModel
    val args: ArticleFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
        val article = args.article
        Log.d("Article Fragment","making request")

        webView.apply {
            webViewClient = WebViewClient()
            article.url?.let { loadUrl(it) }
            Log.d("Article Fragment","request sent to: ${article.url}")
        }
        fab.setOnClickListener {
            viewModel.saveArticle(article)
            Snackbar.make(view,"Article saved to favourites",Snackbar.LENGTH_SHORT).show()
        }
    }
}