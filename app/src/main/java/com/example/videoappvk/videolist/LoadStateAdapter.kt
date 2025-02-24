package com.example.videoappvk.videolist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.example.videoappvk.R

class LoadStateAdapter(
    private val retry: () -> Unit
) : androidx.paging.LoadStateAdapter<LoadStateAdapter.LoadStateViewHolder>() {

    inner class LoadStateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val progressBar: View = itemView.findViewById(R.id.progress_bar)
        private val retryButton: View = itemView.findViewById(R.id.retry_button)
        private val errorMessage: TextView = itemView.findViewById(R.id.error_message)

        fun bind(loadState: LoadState) {
            when (loadState) {
                is LoadState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    retryButton.visibility = View.GONE
                    errorMessage.visibility = View.GONE
                }

                is LoadState.Error -> {
                    progressBar.visibility = View.GONE
                    retryButton.visibility = View.VISIBLE
                    errorMessage.visibility = View.VISIBLE
                }

                else -> {
                    progressBar.visibility = View.GONE
                    retryButton.visibility = View.GONE
                    errorMessage.visibility = View.GONE
                }
            }

            retryButton.setOnClickListener {
                retry()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.load_state_item, parent, false)
        return LoadStateViewHolder(view)
    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }
}