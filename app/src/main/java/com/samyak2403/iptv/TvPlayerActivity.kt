package com.samyak2403.iptv

import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AdapterView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView

class TvPlayerActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_VIDEO_URL = "extra_video_url"
    }

    private lateinit var toolbar: Toolbar
    private lateinit var playerView: PlayerView
    private lateinit var player: ExoPlayer
    private lateinit var trackSelector: DefaultTrackSelector
    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private var scaleFactor = 1.0f
    private var isToolbarVisible = false // Initially hide the toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tv_player)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Enable back arrow

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        playerView = findViewById(R.id.player_view)
        val videoUrl = intent.getStringExtra(EXTRA_VIDEO_URL) ?: return

        setupPlayer(videoUrl)
        hideSystemUI()

        // Initialize scale gesture detector
        scaleGestureDetector = ScaleGestureDetector(this, ScaleListener())

        // Set touch listener on player view
        playerView.setOnTouchListener { _, event ->
            scaleGestureDetector.onTouchEvent(event)
            toggleVisibility() // Toggle visibility of toolbar and video controls on touch
            true
        }

        setupQualitySelector()

        // Initially hide the toolbar and video controls
        toolbar.visibility = View.GONE
//        playerView.controllerVisibility = View.GONE
    }

    private fun setupPlayer(videoUrl: String) {
        val trackSelectionFactory = AdaptiveTrackSelection.Factory()
        trackSelector = DefaultTrackSelector(this, trackSelectionFactory)

        player = ExoPlayer.Builder(this).setTrackSelector(trackSelector).build()
        playerView.player = player

        val mediaItem = MediaItem.fromUri(Uri.parse(videoUrl))
        player.setMediaItem(mediaItem)
        player.prepare()
        player.playWhenReady = true
    }

    private fun setupQualitySelector() {
        val qualitySelector: Spinner = findViewById(R.id.quality_selector)
        val qualityOptions = arrayOf("144p", "360p", "720p", "1080p")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, qualityOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        qualitySelector.adapter = adapter

        qualitySelector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedQuality = qualityOptions[position]
                setVideoQuality(selectedQuality)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setVideoQuality(quality: String) {
        val parametersBuilder = trackSelector.buildUponParameters()
        when (quality) {
            "144p" -> parametersBuilder.setMaxVideoSizeSd()
            "360p" -> parametersBuilder.setMaxVideoSize(480, 360)
            "720p" -> parametersBuilder.setMaxVideoSize(1280, 720)
            "1080p" -> parametersBuilder.setMaxVideoSize(1920, 1080)
        }
        trackSelector.setParameters(parametersBuilder.build())
    }

    private fun hideSystemUI() {
        val windowInsetsController = ViewCompat.getWindowInsetsController(window.decorView)
        windowInsetsController?.let {
            it.hide(WindowInsetsCompat.Type.systemBars())
            it.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun toggleVisibility() {
        if (isToolbarVisible) {
            toolbar.animate().alpha(0f).setDuration(300).withEndAction {
                toolbar.visibility = View.GONE
//                playerView.controllerVisibility = View.GONE // Hide video controls
            }
        } else {
            toolbar.visibility = View.VISIBLE
            toolbar.alpha = 0f
            toolbar.animate().alpha(1f).setDuration(300)
//            playerView.controllerVisibility = View.VISIBLE // Show video controls
        }
        isToolbarVisible = !isToolbarVisible
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor
            scaleFactor = scaleFactor.coerceIn(1.0f, 5.0f)  // Limit the scale factor to prevent excessive zoom
            playerView.scaleX = scaleFactor
            playerView.scaleY = scaleFactor
            return true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            onBackPressed()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}


//package com.samyak2403.iptv
//
//import android.content.Intent
//import android.net.Uri
//import android.os.Bundle
//import android.view.MenuItem
//import android.view.ScaleGestureDetector
//import android.view.View
//import android.widget.ArrayAdapter
//import android.widget.AdapterView
//import android.widget.ImageButton
//import android.widget.Spinner
//import androidx.appcompat.app.AppCompatActivity
//import androidx.appcompat.widget.Toolbar
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import androidx.core.view.WindowInsetsControllerCompat
//
//
//import com.google.android.exoplayer2.ExoPlayer
//import com.google.android.exoplayer2.MediaItem
//import com.google.android.exoplayer2.SimpleExoPlayer
//import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
//import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
//import com.google.android.exoplayer2.ui.PlayerView
//
//class TvPlayerActivity : AppCompatActivity() {
//    companion object {
//        const val EXTRA_VIDEO_URL = "extra_video_url"
//    }
//
//    private lateinit var backButton: ImageButton
//
//    private lateinit var playerView: PlayerView
//    private lateinit var player: ExoPlayer
//    private lateinit var trackSelector: DefaultTrackSelector
//    private lateinit var scaleGestureDetector: ScaleGestureDetector
//    private var scaleFactor = 1.0f
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_tv_player)
//
//        val toolbar: Toolbar = findViewById(R.id.toolbar)
//        setSupportActionBar(toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Enable back arrow
//
//        toolbar.setNavigationOnClickListener {
//            onBackPressed()
//        }
//
//        playerView = findViewById(R.id.player_view)
//        val videoUrl = intent.getStringExtra(EXTRA_VIDEO_URL) ?: return
//
//        setupPlayer(videoUrl)
//        hideSystemUI()
//
//        // Initialize scale gesture detector
//        scaleGestureDetector = ScaleGestureDetector(this, ScaleListener())
//
//        // Set touch listener on player view
//        playerView.setOnTouchListener { _, event ->
//            scaleGestureDetector.onTouchEvent(event)
//            true
//        }
//
//        setupQualitySelector()
//
//
//
//
//
//
//
//
//
//
//
//    }
//
//
//
//    private fun setupPlayer(videoUrl: String) {
//        val trackSelectionFactory = AdaptiveTrackSelection.Factory()
//        trackSelector = DefaultTrackSelector(this, trackSelectionFactory)
//
//        player = ExoPlayer.Builder(this).setTrackSelector(trackSelector).build()
//        playerView.player = player
//
//        val mediaItem = MediaItem.fromUri(Uri.parse(videoUrl))
//        player.setMediaItem(mediaItem)
//        player.prepare()
//        player.playWhenReady = true
//    }
//
//    private fun setupQualitySelector() {
//        val qualitySelector: Spinner = findViewById(R.id.quality_selector)
//        val qualityOptions = arrayOf("144p", "360p", "720p", "1080p")
//        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, qualityOptions)
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        qualitySelector.adapter = adapter
//
//        qualitySelector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                val selectedQuality = qualityOptions[position]
//                setVideoQuality(selectedQuality)
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>?) {}
//        }
//    }
//
//    private fun setVideoQuality(quality: String) {
//        val parametersBuilder = trackSelector.buildUponParameters()
//        when (quality) {
//            "144p" -> parametersBuilder.setMaxVideoSizeSd()
//            "360p" -> parametersBuilder.setMaxVideoSize(480, 360)
//            "720p" -> parametersBuilder.setMaxVideoSize(1280, 720)
//            "1080p" -> parametersBuilder.setMaxVideoSize(1920, 1080)
//        }
//        trackSelector.setParameters(parametersBuilder.build())
//    }
//
//    private fun hideSystemUI() {
//        val windowInsetsController = ViewCompat.getWindowInsetsController(window.decorView)
//        windowInsetsController?.let {
//            it.hide(WindowInsetsCompat.Type.systemBars())
//            it.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        player.release()
//    }
//
//    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
//        override fun onScale(detector: ScaleGestureDetector): Boolean {
//            scaleFactor *= detector.scaleFactor
//            scaleFactor = scaleFactor.coerceIn(1.0f, 5.0f)  // Limit the scale factor to prevent excessive zoom
//            playerView.scaleX = scaleFactor
//            playerView.scaleY = scaleFactor
//            return true
//        }
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return if (item.itemId == android.R.id.home) {
//            onBackPressed()
//            true
//        } else {
//            super.onOptionsItemSelected(item)
//        }
//    }
//}
//
