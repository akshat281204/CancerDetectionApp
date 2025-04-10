package com.example.cancerdetection.model

data class Article(
    val title: String,
    val description: String,
    val image: String?,
    val publishedAt: String,
    val source: Source,
    val url: String,
)

data class Source(
    val name: String,
    val url: String
)