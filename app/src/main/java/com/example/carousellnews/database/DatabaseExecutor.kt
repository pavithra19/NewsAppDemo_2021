package com.example.carousellnews.database

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors

open class DatabaseExecutor constructor(val diskIO : Executor = DiskIOThreadExecutor(), val mainThread : Executor = MainThreadExecutor())
{
    // Main Thread to perform UI operations
    private class MainThreadExecutor : Executor
    {
        private val mainThreadHandler = Handler(Looper.getMainLooper())

        override fun execute(command: Runnable)
        {
            mainThreadHandler.post(command)
        }
    }

    // Secondary thread to perform DB operations
    class DiskIOThreadExecutor : Executor
    {
        private val diskIO = Executors.newSingleThreadExecutor()

        override fun execute(command: Runnable) { diskIO.execute(command) }
    }
}