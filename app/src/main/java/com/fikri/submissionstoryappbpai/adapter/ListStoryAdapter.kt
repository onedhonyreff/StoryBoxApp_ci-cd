package com.fikri.submissionstoryappbpai.adapter

import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fikri.submissionstoryappbpai.R
import com.fikri.submissionstoryappbpai.data_model.Story
import com.fikri.submissionstoryappbpai.databinding.StoryItemBinding
import com.fikri.submissionstoryappbpai.other_class.dpToPx
import com.fikri.submissionstoryappbpai.other_class.toDate

class ListStoryAdapter(private val context: Context) :
    PagingDataAdapter<Story, ListStoryAdapter.ListViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(
                oldItem: Story,
                newItem: Story
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: Story,
                newItem: Story
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

    private lateinit var onItemClickCallback: OnItemClickCallback

    class ListViewHolder(private val binding: StoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            ctx: Context,
            data: Story,
            pos: Int,
            onClicked: ((value: Story, view: View) -> Unit)? = null
        ) {
            binding.apply {
                tvItemName.text = data.name
                tvItemName.contentDescription =
                    ctx.getString(R.string.written_by, data.name)
                tvDescription.text = data.description
                tvDate.text = ctx.getString(
                    R.string.upload_date,
                    DateUtils.getRelativeTimeSpanString(
                        data.createdAt.toDate("yyyy-MM-dd'T'HH:mm:ss.SS'Z'").time
                    ).toString()
                )
                tvDate.contentDescription = ctx.getString(
                    R.string.uploaded_on,
                    DateUtils.getRelativeTimeSpanString(
                        data.createdAt.toDate("yyyy-MM-dd'T'HH:mm:ss.SS'Z'").time
                    ).toString()
                )
                Glide.with(ctx)
                    .load(data.photoUrl)
                    .error(R.drawable.default_image)
                    .into(binding.ivItemPhoto)

                cvStoryItem.setOnClickListener {
                    onClicked?.invoke(data, ivItemPhoto)
                }

                val params = cvStoryItem.layoutParams as RecyclerView.LayoutParams
                params.setMargins(
                    params.leftMargin,
                    if (pos == 0) {
                        ctx.resources.getDimension(R.dimen.header_height).toInt() + dpToPx(
                            ctx,
                            4f
                        ).toInt()
                    } else {
                        dpToPx(ctx, 4f).toInt()
                    },
                    params.rightMargin,
                    params.bottomMargin
                )
                cvStoryItem.layoutParams = params
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            StoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(context, data, position, onClicked = { value, view ->
                onItemClickCallback.onClickedItem(
                    value,
                    view
                )
            })
        }
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onClickedItem(data: Story, imageThumbnailsView: View)
    }
}