package com.yusufaydin.pinsoftchallenge.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.yusufaydin.pinsoftchallenge.databinding.MoviesRowBinding
import com.yusufaydin.pinsoftchallenge.model.Search
import com.yusufaydin.pinsoftchallenge.util.loadImage


class MoviesAdapter() :
    RecyclerView.Adapter<MoviesAdapter.RowHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<Search>() {
        override fun areItemsTheSame(oldItem: Search, newItem: Search): Boolean {
            return oldItem.poster == newItem.poster
        }

        override fun areContentsTheSame(oldItem: Search, newItem: Search): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    inner class RowHolder(val binding: MoviesRowBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowHolder {
        val binding = MoviesRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RowHolder(binding)
    }

    private var onItemClickListener: ((Search) -> Unit)? = null

    override fun onBindViewHolder(holder: RowHolder, position: Int) {
        val movie = differ.currentList[position]
        holder.binding.apply {
            movieRowImageView.loadImage(movie.poster)
            movieRowTitleTextView.text = movie.title
            movieRowYearTextView.text = movie.year
        }
        holder.itemView.setOnClickListener {
            onItemClickListener?.let { it(movie) }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    fun setOnItemCLickListener(listener: (Search) -> Unit) {
        onItemClickListener = listener
    }
}