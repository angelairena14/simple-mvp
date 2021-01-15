package com.timeromannews.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.timeromannews.R
import com.timeromannews.databinding.ItemNewsBinding
import com.timeromannews.model.newsresponse.News
import java.text.SimpleDateFormat
import java.time.Instant

class NewsListAdapter : RecyclerView.Adapter<NewsListAdapter.NewsListHolder>() {
    private var items = ArrayList<News>()
    private val requestOptions = RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.ALL)
    var onItemClicked: (url: String) -> Unit = { _ -> }
    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsListHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        return NewsListHolder(
            DataBindingUtil.inflate(
                layoutInflater, R.layout.item_news,
                parent, false
            ) as ItemNewsBinding
        )
    }

    override fun onBindViewHolder(holder: NewsListHolder, position: Int) {
        var context = holder.binding.root.context
        var listItem = items[position]
        holder.binding.let {
            try {
                val sdformat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                val formatted = SimpleDateFormat("dd MMM yyyy")
                var date = sdformat.parse(listItem.created_at)
                it.tvDate.text = formatted.format(date)
            } catch (e: Exception) {
                it.tvDate.text = "-"
            }
            it.model = items[position]
            Glide.with(context).asDrawable()
                .apply(requestOptions)
                .placeholder(ContextCompat.getDrawable(context, R.drawable.img_not_available))
                .load(listItem.cover_image)
                .into(it.ivNewsCover)
        }
        holder.binding.cardParent.setOnClickListener { onItemClicked(listItem.url) }
        holder.binding.executePendingBindings()
    }

    override fun onViewRecycled(holder: NewsListHolder) {
        holder.binding.ivNewsCover.setImageBitmap(null)
    }

    fun setNews(news: ArrayList<News>) {
        this.items.clear()
        this.items = news
        notifyDataSetChanged()
    }

    inner class NewsListHolder(val binding: ItemNewsBinding) : RecyclerView.ViewHolder(binding.root)
}
