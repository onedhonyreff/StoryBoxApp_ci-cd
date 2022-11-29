package com.fikri.submissionstoryappbpai.custom_component

import android.content.Context
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.fikri.submissionstoryappbpai.R
import com.fikri.submissionstoryappbpai.other_class.resolveColorAttr

class PasswordFormField : AppCompatEditText, View.OnTouchListener {
    private var visibilityButtonImage: Drawable =
        ContextCompat.getDrawable(context, R.drawable.ic_visibility_on) as Drawable
    private var visibilityState = false
    var isValid = false

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
        isSingleLine = true
        setTextVisibility(visibilityState)
        setOnTouchListener(this)

        addTextChangedListener(onTextChanged = { p0, _, _, _ ->
            isValid = if (p0.toString().trim().length < 6) {
                setError(
                    resources.getString(
                        R.string.input_password_error,
                        p0.toString().trim().length
                    ), null
                )
                false
            } else {
                true
            }
        })
    }

    override fun onTouch(p0: View?, event: MotionEvent): Boolean {
        val visibilityButtonStart: Float
        val visibilityButtonEnd: Float
        var isVisibilityButtonClicked = false
        if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
            visibilityButtonEnd = (visibilityButtonImage.intrinsicWidth + paddingStart).toFloat()
            when {
                event.x < visibilityButtonEnd -> isVisibilityButtonClicked = true
            }
        } else {
            visibilityButtonStart =
                (width - paddingEnd - visibilityButtonImage.intrinsicWidth).toFloat()
            when {
                event.x > visibilityButtonStart -> isVisibilityButtonClicked = true
            }
        }
        return if (isVisibilityButtonClicked) {
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    setTextVisibility(!visibilityState)
                    requestFocus()
                    setSelection(text.toString().length)
                    true
                }
                else -> false
            }
        } else false
    }

    private fun setTextVisibility(state: Boolean) {
        visibilityState = state
        visibilityButtonImage = if (state)
            ContextCompat.getDrawable(context, R.drawable.ic_visibility_off) as Drawable
        else
            ContextCompat.getDrawable(context, R.drawable.ic_visibility_on) as Drawable
        inputType = InputType.TYPE_CLASS_TEXT or
                if (state) InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD else InputType.TYPE_TEXT_VARIATION_PASSWORD

        setCompoundDrawablesWithIntrinsicBounds(
            null,
            null,
            visibilityButtonImage,
            null
        )
    }

    private fun setDrawablesTintColor() {
        @ColorInt
        val color = context.resolveColorAttr(android.R.attr.textColorSecondary)
        for (drawable in compoundDrawables) {
            if (drawable != null) {
                drawable.colorFilter = PorterDuffColorFilter(
                    color,
                    PorterDuff.Mode.SRC_IN
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        hint = resources.getString(R.string.password_hint)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        typeface = resources.getFont(R.font.poppins)
        setDrawablesTintColor()
    }
}