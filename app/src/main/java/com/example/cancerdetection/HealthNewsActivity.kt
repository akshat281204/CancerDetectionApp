package com.example.cancerdetection

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cancerdetection.adapter.NewsAdapter
import com.example.cancerdetection.api.NewsApiService
import com.example.cancerdetection.model.Article
import com.example.cancerdetection.model.NewsResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HealthNewsActivity : AppCompatActivity() {

    private lateinit var newsRecyclerView: RecyclerView
    private lateinit var newsAdapter: NewsAdapter
    private val newsItems = mutableListOf<Article>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.health_news)

        // Initialize RecyclerView
        newsRecyclerView = findViewById(R.id.news_recycler_view)
        newsRecyclerView.layoutManager = LinearLayoutManager(this)

        // Set up adapter
        newsAdapter = NewsAdapter(newsItems)
        newsRecyclerView.adapter = newsAdapter

        // Fetch real news data
        if (!loadCachedNewsIfValid()) {
            fetchNewsFromApi()
        }
    }

    private fun fetchNewsFromApi() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://gnews.io/api/v4/") // Replace with the actual base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(NewsApiService::class.java)

        val call = apiService.getHealthNews("52fc8f036a5e10d5d6c3850b3a7adfd5", "en", "health")

        call.enqueue(object : Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    android.util.Log.d("NEWS_API", "Articles received: ${body?.articles?.size}")
                    response.body()?.articles?.let { articles ->
                        newsItems.clear()
                        newsItems.addAll(articles)
                        newsAdapter.notifyDataSetChanged()
                    }
                } else {
                    android.util.Log.e("NEWS_API", "Failed response: ${response.errorBody()?.string()}")
                }
            }


            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                // Handle error
                t.printStackTrace()
            }
        })
    }

    private fun cacheNewsLocally(articles: List<Article>) {
        val sharedPreferences = getSharedPreferences("news_cache", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Convert articles list to JSON
        val gson = Gson()
        val json = gson.toJson(articles)

        // Save JSON and current timestamp
        editor.putString("cached_news", json)
        editor.putLong("last_updated", System.currentTimeMillis())
        editor.apply()
    }

    private fun loadCachedNewsIfValid(): Boolean {
        val sharedPreferences = getSharedPreferences("news_cache", MODE_PRIVATE)
        val lastUpdated = sharedPreferences.getLong("last_updated", 0L)
        val currentTime = System.currentTimeMillis()

        // Allow update only every 12 hours
        val twelveHours = 12 * 60 * 60 * 1000

        return if (currentTime - lastUpdated < twelveHours) {
            val cachedJson = sharedPreferences.getString("cached_news", null)
            cachedJson?.let {
                val type = object : TypeToken<List<Article>>() {}.type
                val cachedArticles: List<Article> = Gson().fromJson(it, type)

                newsItems.clear()
                newsItems.addAll(cachedArticles)
                newsAdapter.notifyDataSetChanged()
                true
            } ?: false
        } else {
            false
        }
    }
}