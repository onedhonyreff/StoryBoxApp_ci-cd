package com.fikri.submissionstoryappbpai.custom_component

import android.content.Context
import android.graphics.Canvas
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Patterns
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.addTextChangedListener
import com.fikri.submissionstoryappbpai.R

class EmailFormField : AppCompatEditText {

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
        addTextChangedListener(onTextChanged = { p0, _, _, _ ->
            isValid = if (TextUtils.isEmpty(p0) || !Patterns.EMAIL_ADDRESS.matcher(p0 ?: "")
                    .matches()
            ) {
                error = resources.getString(R.string.input_email_error)
                false
            } else {
                true
            }
        })
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        hint = resources.getString(R.string.email_hint)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }
}