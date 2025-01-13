package com.example.carousellnews.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.carousellnews.model.News
import com.example.carousellnews.model.NewsDao

@Database(entities = [News::class], version = 1, exportSchema = false) // DB Tables and DB version info.
abstract class DatabaseAccessor : RoomDatabase()
{
    companion object
    {
        private var INSTANCE: DatabaseAccessor? = null

        fun getInstance(context: Context): DatabaseAccessor
        {
            if (INSTANCE == null) {

                synchronized(Any())
                {
                    if (INSTANCE == null)
                    {
                        INSTANCE = Room.databaseBuilder(context.applicationContext, DatabaseAccessor::class.java, "CarousellNews.db") // Application's Database name.
                                .allowMainThreadQueries()
                                .build()
                    }
                }
            }

            return INSTANCE as DatabaseAccessor
        }
    }

    abstract fun newsDao(): NewsDao // Abstract function to access data from DB.
}