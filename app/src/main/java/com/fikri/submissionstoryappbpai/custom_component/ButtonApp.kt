package com.fikri.submissionstoryappbpai.custom_component

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.fikri.submissionstoryappbpai.R

class ButtonApp : AppCompatButton {

    private lateinit var enableBackground: Drawable
    private lateinit var disableBackground: Drawable
    private lateinit var mForeground: Drawable
    private var txtColor: Int = 0

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        txtColor = ContextCompat.getColor(context, android.R.color.white)
        enableBackground = ContextCompat.getDrawable(context, R.drawable.bg_button) as Drawable
        disableBackground =
            ContextCompat.getDrawable(context, R.drawable.bg_button_disable) as Drawable
        mForeground = ContextCompat.getDrawable(context, R.drawable.fg_button) as Drawable
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        isAllCaps = false
        background = if (isEnabled) enableBackground else disableBackground
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            foreground = mForeground
        }
        setTextColor(txtColor)
        textSize = 16F
        gravity = Gravity.CENTER
    }
}