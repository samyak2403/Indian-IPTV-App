package com.samyak2403.iptv

import com.google.android.exoplayer2.C


import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup

import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.MappingTrackSelector

class QualitySelectorDialog(
    context: Context,
    private val player: ExoPlayer,
    private val trackSelector: DefaultTrackSelector
) : Dialog(context) {

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_quality_selector, null)
        setContentView(view)

        val radioGroup = findViewById<RadioGroup>(R.id.quality_radio_group)
        populateQualityOptions(radioGroup)
    }

    private fun populateQualityOptions(radioGroup: RadioGroup) {
        val mappedTrackInfo = trackSelector.currentMappedTrackInfo ?: return

        for (i in 0 until mappedTrackInfo.rendererCount) {
            if (mappedTrackInfo.getRendererType(i) == C.TRACK_TYPE_VIDEO) {
                val trackGroups = mappedTrackInfo.getTrackGroups(i)
                for (j in 0 until trackGroups.length) {
                    val radioButton = RadioButton(context).apply {
                        text = "Quality $j"  // Customize with actual quality labels
                        tag = j
                    }
                    radioGroup.addView(radioButton)

                    radioButton.setOnClickListener {
                        val trackIndex = it.tag as Int
                        val override = DefaultTrackSelector.SelectionOverride(j, trackIndex)
                        trackSelector.setParameters(
                            trackSelector.buildUponParameters()
                                .setSelectionOverride(i, trackGroups, override)
                        )
                        dismiss()
                    }
                }
            }
        }
    }
}