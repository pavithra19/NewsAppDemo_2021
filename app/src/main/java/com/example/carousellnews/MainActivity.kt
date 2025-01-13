package com.example.carousellnews

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.carousellnews.database.DatabaseAccessor
import com.example.carousellnews.database.DatabaseExecutor
import com.example.carousellnews.model.News
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity()
{
    private lateinit var dbAccessor: DatabaseAccessor
    private lateinit var dbExecutors : DatabaseExecutor

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        init()

        setupViews()

        fetchNewsListFromAPI() // Fetch and show updated News in the List view.
    }

    private fun init()
    {
        dbExecutors = DatabaseExecutor()
        dbAccessor = DatabaseAccessor.getInstance(this)
    }

    private fun setupViews()
    {
        recyclerView?.layoutManager = LinearLayoutManager(this)
        swipe_refresh_layout?.setOnRefreshListener { fetchNewsListFromAPI() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean
    {
        // Include different filter options for viewing the News list.
        menu.clear()
        menu.add(Menu.NONE, 1, Menu.NONE, R.string.action_recent).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
        menu.add(Menu.NONE, 2, Menu.NONE, R.string.action_popular).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        when (item.itemId)
        {
            1 -> updateListView(1)
            2 -> updateListView(2)
        }

        return super.onOptionsItemSelected(item)
    }

    // Based on the filter mode, News list is fetched from DB and set in the recyclerview adapter.
    private fun updateListView(filterMode: Int = 0)
    {
        when (filterMode)
        {
            1 ->
            {
                recyclerView?.adapter = NewsListAdapter(this@MainActivity, dbAccessor.newsDao().loadRecentNews())
            }

            2 ->
            {
                recyclerView?.adapter = NewsListAdapter(this@MainActivity, dbAccessor.newsDao().loadPopularNews())
            }

            else ->
            {
                recyclerView?.adapter = NewsListAdapter(this@MainActivity, dbAccessor.newsDao().loadAllNews())
            }
        }
    }

    private fun fetchNewsListFromAPI()
    {
        swipe_refresh_layout?.isRefreshing = true

        // Check if network connection is available to initiate API call.
        if (CarousellUtil.haveNetworkConnection(this))
        {
            val queue = Volley.newRequestQueue(this)
            val url = "https://storage.googleapis.com/carousell-interview-assets/android/carousell_news.json"

            val stringRequest = StringRequest(Request.Method.GET, url,
                { response ->

                    swipe_refresh_layout?.isRefreshing = false

                    val model = Gson().fromJson(response, Array<News>::class.java).toList()

                    dbExecutors.diskIO.execute { // Switched to secondary thread to perform DB operations.

                        dbAccessor.newsDao().deleteNewsList() // Refresh DB once updated list is fetched from API.
                        dbAccessor.newsDao().storeNewsList(model) // Save the newly received API response in DB.

                        dbExecutors.mainThread.execute { // Switched to Main thread to perform UI operations.

                            updateListView()
                        }
                    }
                },
                { error ->

                    swipe_refresh_layout?.isRefreshing = false
                    Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()}) // Error message is shown in Toast message, if API call is failed.

            queue.add(stringRequest)
        }
        // Show News list if data available in DB for offline case.
        else
        {
            swipe_refresh_layout?.isRefreshing = false
            Toast.makeText(this, R.string.check_your_network_connection_message, Toast.LENGTH_SHORT).show()

            updateListView()
        }
    }
}