package com.samyak2403.iptv

import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.samyak2403.iptv.adapter.TVLogoAdapter


class TvActivity : AppCompatActivity() {


    private lateinit var searchBar: EditText
    private lateinit var adapters: Map<Int, TVLogoAdapter>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tv) // Assume you create activity_tv layout

        // Initialize searchBar
        searchBar = findViewById(R.id.search_bar)

        val categories = mapOf(
            R.id.kids_recyclerview to "Kids",
            R.id.movies_recyclerview to "Movies",
            R.id.sports_recyclerview to "Sports",
            R.id.entertainment_recyclerview to "Entertainment",
            R.id.music_recyclerview to "Music",
            R.id.news_recyclerview to "News"
        )

        // Determine the span count based on device type and orientation
        val isTv = isTvDevice()
        val orientation = resources.configuration.orientation

        val spanCount = when {
            isTv -> 5
            orientation == Configuration.ORIENTATION_LANDSCAPE -> 4
            else -> 3
        }

        adapters = categories.map { (recyclerViewId, category) ->
            val recyclerView: RecyclerView = findViewById(recyclerViewId)
            recyclerView.layoutManager = GridLayoutManager(this, spanCount)

            // Create instance of TVLogoAdapter
            val adapter = TVLogoAdapter(this, category)

            // Set the TVLogoAdapter as the adapter for the RecyclerView
            recyclerView.adapter = adapter

            recyclerViewId to adapter
        }.toMap()

        // Set up the search functionality
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // No implementation needed
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No implementation needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                adapters.values.forEach { it.filter(query) }
            }
        })
    }

    // Function to determine if the device is a TV
    private fun isTvDevice(): Boolean {
        return resources.configuration.uiMode and Configuration.UI_MODE_TYPE_MASK == Configuration.UI_MODE_TYPE_TELEVISION
    }
}
