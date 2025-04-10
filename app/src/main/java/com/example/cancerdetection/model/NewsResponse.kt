package com.example.cancerdetection.model

data class NewsResponse(
    val totalArticles: Int,
    val articles: List<Article>
)
