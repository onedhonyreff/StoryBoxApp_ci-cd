package com.fikri.submissionstoryappbpai.custom_component

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.gms.maps.SupportMapFragment


class WorkaroundMapFragment : SupportMapFragment() {
    private lateinit var mListener: OnTouchListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val layout: View = super.onCreateView(inflater, container, savedInstanceState)
        val frameLayout = TouchableWrapper(activity as Context)
        @Suppress("DEPRECATION")
        frameLayout.setBackgroundColor(resources.getColor(android.R.color.transparent))
        (layout as ViewGroup).addView(
            frameLayout,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        return layout
    }

    fun setListener(listener: OnTouchListener) {
        mListener = listener
    }

    interface OnTouchListener {
        fun onTouch()
    }

    inner class TouchableWrapper(context: Context) : FrameLayout(context) {
        override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
            when (ev?.action) {
                MotionEvent.ACTION_DOWN -> mListener.onTouch()
                MotionEvent.ACTION_UP -> mListener.onTouch()
            }
            return super.dispatchTouchEvent(ev)
        }
    }
}