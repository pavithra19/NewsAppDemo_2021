package com.example.carousellnews

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.carousellnews.model.News
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.news_layout_item.view.*
import java.util.*
import java.util.concurrent.TimeUnit

class NewsListAdapter(private val context: Context, private val news: List<News>?) : RecyclerView.Adapter<NewsListAdapter.NewsViewHolder>()
{
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): NewsViewHolder
    {
        return NewsViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.news_layout_item, viewGroup, false))
    }

    override fun onBindViewHolder(viewHolder: NewsViewHolder, i: Int)
    {
        Picasso.with(context).load(news?.get(i)?.banner_url).fit().centerCrop().into(viewHolder.banner_imageview) // Load image using Picasso in the News list view.

        viewHolder.news_title.text = news?.get(i)?.title
        viewHolder.news_description.text = news?.get(i)?.description
        viewHolder.news_posted_time.text = news?.get(i)?.time_created?.let { calculatePostedTime(it) } // Calculate readable time from the time in milliseconds format.
    }

    override fun getItemCount(): Int
    {
        return news?.size ?: 0
    }

    // Based on the Current time and News created time, time in readable format will be calculated.
    private fun calculatePostedTime(postedDate: Long): String
    {
        val days = TimeUnit.MILLISECONDS.toDays(Calendar.getInstance().timeInMillis - postedDate)

        when
        {
            days <= 1 ->
            {
                val hours = TimeUnit.MILLISECONDS.toHours(Calendar.getInstance().timeInMillis - postedDate)

                if (hours >= 1)
                {
                    return "$hours ${context.getString(R.string.hours_ago)}"
                }
                else
                {
                    val minutes = TimeUnit.MILLISECONDS.toMinutes(Calendar.getInstance().timeInMillis - postedDate)
                    return if (minutes > 1) "$minutes ${context.getString(R.string.minutes_ago)}" else context.getString(R.string.seconds_ago)
                }
            }
            days < 7 ->
            {
                return "$days ${context.getString(R.string.days_ago)}"
            }
            days <= 31 ->
            {
                val weeksCount = days/7
                return if (weeksCount > 1) "$weeksCount ${context.getString(R.string.weeks_ago)}" else "$weeksCount ${context.getString(R.string.week_ago)}"
            }
            days <= 366 ->
            {
                val monthsCount = days/12
                return if (monthsCount > 1) "$monthsCount ${context.getString(R.string.months_ago)}" else "$monthsCount ${context.getString(R.string.month_ago)}"
            }
            else ->
            {
                val yearsCount = days/366
                return if (yearsCount > 1) "$yearsCount ${context.getString(R.string.years_ago)}" else "$yearsCount ${context.getString(R.string.year_ago)}"
            }
        }
    }

    // Initializing News viewholder layout
    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var banner_imageview: ImageView = itemView.banner_imageview
        var news_title: TextView = itemView.news_title
        var news_description: TextView = itemView.news_description
        var news_posted_time: TextView = itemView.news_posted_time
    }

}