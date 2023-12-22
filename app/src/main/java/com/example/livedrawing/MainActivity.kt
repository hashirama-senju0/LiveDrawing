package com.example.livedrawing

import android.app.Activity
import android.content.res.Resources
import android.os.Bundle

class MainActivity : Activity() {
    private lateinit var liveDrawingView: LiveDrawingView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val x = Resources.getSystem().getDisplayMetrics().widthPixels;
        liveDrawingView= LiveDrawingView(this,x)
        setContentView(liveDrawingView)
    }

    override fun onPause() {
        super.onPause()
        liveDrawingView.pause()
    }

    override fun onResume() {
        super.onResume()
        liveDrawingView.resume()
    }
}