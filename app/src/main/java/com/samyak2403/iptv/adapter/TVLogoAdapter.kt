package com.samyak2403.iptv.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.samyak2403.iptv.R
import com.samyak2403.iptv.TvPlayerActivity
import com.samyak2403.iptv.model.TVChannel
import okhttp3.*
import java.io.IOException

class TVLogoAdapter(private val context: Context, private val category: String) : RecyclerView.Adapter<TVLogoAdapter.ViewHolder>() {

    private val tvChannels = mutableListOf<TVChannel>()
    private val filteredTvChannels = mutableListOf<TVChannel>()

    init {
        // Fetch data from the URL using OkHttp
        fetchData("https://drfiles.github.io/IPTV-ORG/api/channels.json")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tv_logo, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val channel = filteredTvChannels[position]

        // Load image into ImageView using Glide
        Glide.with(context).load(channel.imageUrl).into(holder.imageView)

        // Set click listener for the card view
        holder.cardView.setOnClickListener {
            // Launch TvPlayerActivity with the corresponding video URL
            val intent = Intent(context, TvPlayerActivity::class.java)
            intent.putExtra(TvPlayerActivity.EXTRA_VIDEO_URL, channel.videoUrl)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return filteredTvChannels.size
    }

    fun filter(query: String) {
        filteredTvChannels.clear()
        if (query.isEmpty()) {
            filteredTvChannels.addAll(tvChannels)
        } else {
            val lowercaseQuery = query.lowercase()
            for (channel in tvChannels) {
                if (channel.name.lowercase().contains(lowercaseQuery)) {
                    filteredTvChannels.add(channel)
                }
            }
        }
        notifyDataSetChanged()
    }

    private fun fetchData(url: String) {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("TVLogoAdapter", "Failed to fetch data: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let { responseBody ->
                    val reader = responseBody.charStream()
                    val type = object : TypeToken<List<TVChannel>>() {}.type
                    val channels: List<TVChannel> = Gson().fromJson(reader, type)
                    reader.close()

                    // Update the UI on the main thread
                    (context as Activity).runOnUiThread {
                        for (channel in channels) {
                            if (channel.category == category) {
                                tvChannels.add(channel)
                            }
                        }
                        filter("")
                    }
                }
            }
        })
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.card_view)
        val imageView: ImageView = itemView.findViewById(R.id.card_image)
    }
}


//package com.samyak2403.iptv.adapter
//
//import android.content.Context
//import android.content.Intent
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import androidx.cardview.widget.CardView
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//
//import com.google.gson.Gson
//import com.google.gson.reflect.TypeToken
//import com.samyak2403.iptv.R
//import com.samyak2403.iptv.TvPlayerActivity
//import com.samyak2403.iptv.model.TVChannel
//import java.io.InputStreamReader
//
//class TVLogoAdapter(private val context: Context, private val category: String) : RecyclerView.Adapter<TVLogoAdapter.ViewHolder>() {
//
//    private val tvChannels = mutableListOf<TVChannel>()
//    private val filteredTvChannels = mutableListOf<TVChannel>()
//
//    init {
//        // Load data from JSON file
//        val inputStream = context.assets.open("channels.json") // Place your JSON file in the assets folder
//        val reader = InputStreamReader(inputStream)
//        val type = object : TypeToken<List<TVChannel>>() {}.type
//        val channels: List<TVChannel> = Gson().fromJson(reader, type)
//        reader.close()
//
//        // Filter channels by category
//        for (channel in channels) {
//            if (channel.category == category) {
//                tvChannels.add(channel)
//            }
//        }
//
//        // Initialize the filtered list
//        filter("")
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tv_logo, parent, false)
//        return ViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val channel = filteredTvChannels[position]
//
//        // Load image into ImageView using Glide
//        Glide.with(context).load(channel.imageUrl).into(holder.imageView)
//
//        // Set click listener for the card view
//        holder.cardView.setOnClickListener {
//            // Launch TvPlayerActivity with the corresponding video URL
////            val intent = Intent(context, TvPlayerActivity::class.java)
////            intent.putExtra(TvPlayerActivity.EXTRA_VIDEO_URL, channel.videoUrl)
////            context.startActivity(intent)
//            val intent = Intent(context, TvPlayerActivity::class.java)
//            intent.putExtra(TvPlayerActivity.EXTRA_VIDEO_URL, channel.videoUrl)
//            context.startActivity(intent)
//
//        }
//    }
//
//    override fun getItemCount(): Int {
//        return filteredTvChannels.size
//    }
//
//    fun filter(query: String) {
//        filteredTvChannels.clear()
//        if (query.isEmpty()) {
//            filteredTvChannels.addAll(tvChannels)
//        } else {
//            val lowercaseQuery = query.lowercase()
//            for (channel in tvChannels) {
//                if (channel.name.lowercase().contains(lowercaseQuery)) {
//                    filteredTvChannels.add(channel)
//                }
//            }
//        }
//        notifyDataSetChanged()
//    }
//
//    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val cardView: CardView = itemView.findViewById(R.id.card_view)
//        val imageView: ImageView = itemView.findViewById(R.id.card_image)
//    }
//}
