package com.example.carousellnews.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "News")
class News
{
    @PrimaryKey(autoGenerate = true)
    var _id : Int = 0
    var title: String? = null
    var description: String? = null
    var banner_url: String? = null
    var time_created: Long = 0
    var rank: Int = 0
}