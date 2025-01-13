package com.example.carousellnews.model

import androidx.room.*

@Dao
interface NewsDao
{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun storeNewsList(list: List<News>)

    @Query("DELETE FROM `News`")
    fun deleteNewsList()

    @Query("select * from `News`")
    fun loadAllNews(): List<News>

    @Query("select * from `News` ORDER BY time_created DESC")
    fun loadRecentNews(): List<News>

    @Query("select * from `News` ORDER BY rank ASC, time_created DESC")
    fun loadPopularNews(): List<News>
}