package com.fikri.submissionstoryappbpai.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fikri.submissionstoryappbpai.R
import com.fikri.submissionstoryappbpai.databinding.PagingFooterBinding

class LoadingStateAdapter(private val context: Context, private val retry: () -> Unit) :
    LoadStateAdapter<LoadingStateAdapter.LoadingStateViewHolder>() {

    class LoadingStateViewHolder(private val binding: PagingFooterBinding, retry: () -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.btnRefresh.setOnClickListener {
                retry.invoke()
            }
        }

        fun bind(context: Context, loadState: LoadState) {
            binding.apply {
                if (loadState is LoadState.Error) {
                    tvErrorMsg.text = context.getString(R.string.paging_error)
                }
                pbLoading.isVisible = loadState is LoadState.Loading
                btnRefresh.isVisible = loadState is LoadState.Error
                tvErrorMsg.isVisible = loadState is LoadState.Error
            }
        }
    }

    override fun onBindViewHolder(
        holder: LoadingStateViewHolder,
        loadState: LoadState
    ) {
        holder.bind(context, loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): LoadingStateViewHolder {
        val binding =
            PagingFooterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadingStateViewHolder(binding, retry)
    }
}