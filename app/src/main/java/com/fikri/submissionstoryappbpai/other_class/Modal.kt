package com.fikri.submissionstoryappbpai.other_class

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.fikri.submissionstoryappbpai.R
import com.fikri.submissionstoryappbpai.custom_component.ButtonApp

class ResponseModal {
    companion object {
        const val TYPE_GENERAL = "general"
        const val TYPE_ERROR = "error"
        const val TYPE_FAILED = "failed"
        const val TYPE_SUCCESS = "success"
        const val TYPE_MISTAKE = "mistake"
    }

    private var modal: Dialog? = null

    fun showResponseModal(
        context: Context,
        type: String = TYPE_GENERAL,
        message: String?,
        callback: (() -> Unit)? = null
    ) {
        modal = Dialog(context)
        modal?.setContentView(R.layout.response_modal)
        val ivIllustration = modal?.findViewById<ImageView>(R.id.iv_illustration)
        val tvMessage = modal?.findViewById<TextView>(R.id.tv_message)
        val btnClose = modal?.findViewById<ButtonApp>(R.id.btn_close)

        ivIllustration?.setImageDrawable(
            when (type) {
                TYPE_GENERAL -> ContextCompat.getDrawable(context, R.drawable.il_emotion_general)
                TYPE_ERROR -> ContextCompat.getDrawable(context, R.drawable.il_emotion_confused)
                TYPE_FAILED -> ContextCompat.getDrawable(context, R.drawable.il_emotion_sad)
                TYPE_SUCCESS -> ContextCompat.getDrawable(context, R.drawable.il_emotion_happy)
                TYPE_MISTAKE -> ContextCompat.getDrawable(context, R.drawable.il_emotion_angry)
                else -> ContextCompat.getDrawable(context, R.drawable.il_emotion_general)
            }
        )
        tvMessage?.text = message

        btnClose?.setOnClickListener {
            callback?.invoke()
        }

        modal?.apply {
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCancelable(false)
            show()
        }
    }

    fun dismiss() {
        if (modal != null && modal!!.isShowing) {
            modal?.dismiss()
        }
    }
}

class RefreshModal {
    companion object {
        const val TYPE_GENERAL = "general"
        const val TYPE_ERROR = "error"
        const val TYPE_FAILED = "failed"
        const val TYPE_MISTAKE = "mistake"
    }

    private var modal: Dialog? = null

    fun showRefreshModal(
        context: Context,
        type: String? = TYPE_GENERAL,
        message: String?,
        onRefreshClicked: (() -> Unit)? = null,
        onCloseClicked: (() -> Unit)? = null
    ) {
        modal = Dialog(context)
        modal?.setContentView(R.layout.refresh_question_modal)
        val ivIllustration = modal?.findViewById<ImageView>(R.id.iv_illustration)
        val tvMessage = modal?.findViewById<TextView>(R.id.tv_message)
        val btnRefresh = modal?.findViewById<ButtonApp>(R.id.btn_refresh)
        val btnClose = modal?.findViewById<ButtonApp>(R.id.btn_close)

        ivIllustration?.setImageDrawable(
            when (type) {
                TYPE_GENERAL -> ContextCompat.getDrawable(
                    context,
                    R.drawable.il_emotion_general
                )
                TYPE_ERROR -> ContextCompat.getDrawable(
                    context,
                    R.drawable.il_emotion_confused
                )
                TYPE_FAILED -> ContextCompat.getDrawable(
                    context,
                    R.drawable.il_emotion_sad
                )
                TYPE_MISTAKE -> ContextCompat.getDrawable(
                    context,
                    R.drawable.il_emotion_angry
                )
                else -> ContextCompat.getDrawable(context, R.drawable.il_emotion_general)
            }
        )
        tvMessage?.text = message

        btnRefresh?.setOnClickListener {
            onRefreshClicked?.invoke()
        }
        btnClose?.setOnClickListener {
            onCloseClicked?.invoke()
        }

        modal?.apply {
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCancelable(false)
            show()
        }
    }

    fun dismiss() {
        if (modal != null && modal!!.isShowing) {
            modal?.dismiss()
        }
    }
}

class LoadingModal {
    companion object {
        const val TYPE_GENERAL = "general"
    }

    private var modal: Dialog? = null

    fun showLoadingModal(
        context: Context,
        type: String = TYPE_GENERAL,
        message: String?
    ) {
        modal = Dialog(context)
        modal?.setContentView(R.layout.common_loading)
        val ivIllustration = modal?.findViewById<ImageView>(R.id.iv_illustration)
        val tvMessage = modal?.findViewById<TextView>(R.id.tv_message)

        if (ivIllustration != null) {
            Glide.with(context).load(
                when (type) {
                    TYPE_GENERAL -> ContextCompat.getDrawable(context, R.drawable.il_loading)
                    else -> ContextCompat.getDrawable(context, R.drawable.il_loading)
                }
            ).into(ivIllustration)
        }
        tvMessage?.text = message

        modal?.apply {
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCancelable(false)
            show()
        }
    }

    fun dismiss() {
        if (modal != null && modal!!.isShowing) {
            modal?.dismiss()
        }
    }
}