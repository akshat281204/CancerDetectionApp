package com.example.cancerdetection.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cancerdetection.model.Article
import com.example.cancerdetection.R

class NewsAdapter(private val articles: List<Article>) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    class NewsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.newsTitle)
        val desc: TextView = view.findViewById(R.id.newsDesc)
        val image: ImageView = view.findViewById(R.id.newsImage)
        val date: TextView = itemView.findViewById(R.id.newsDate)
        val category: TextView = itemView.findViewById(R.id.newsCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news_card, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = articles[position]
        holder.title.text = article.title
        holder.desc.text = article.description
        holder.date.text = article.publishedAt
        holder.category.text = article.source.name

        Glide.with(holder.itemView.context)
            .load(article.image)
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.placeholder_image)
            .into(holder.image)

        holder.itemView.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(article.url))
            holder.itemView.context.startActivity(browserIntent)
        }
    }

    override fun getItemCount() = articles.size
}
