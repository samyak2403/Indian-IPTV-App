package com.samyak2403.iptv.adapter

import android.content.Context
import android.content.Intent
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
import java.io.InputStreamReader

class TVLogoAdapter(private val context: Context, private val category: String) : RecyclerView.Adapter<TVLogoAdapter.ViewHolder>() {

    private val tvChannels = mutableListOf<TVChannel>()
    private val filteredTvChannels = mutableListOf<TVChannel>()

    init {
        // Load data from JSON file
        val inputStream = context.assets.open("tv_channels.json") // Place your JSON file in the assets folder
        val reader = InputStreamReader(inputStream)
        val type = object : TypeToken<List<TVChannel>>() {}.type
        val channels: List<TVChannel> = Gson().fromJson(reader, type)
        reader.close()

        // Filter channels by category
        for (channel in channels) {
            if (channel.category == category) {
                tvChannels.add(channel)
            }
        }

        // Initialize the filtered list
        filter("")
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

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.card_view)
        val imageView: ImageView = itemView.findViewById(R.id.card_image)
    }
}
