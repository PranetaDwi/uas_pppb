package com.neta.uas_pppb.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.neta.uas_pppb.databinding.ItemHomemovieBinding
import com.neta.uas_pppb.firebase.Movies

class MovieUserAdapter(private val context: Context, private var listMovie: MutableList<Movies>): RecyclerView.Adapter<MovieUserAdapter.ItemUserMovieViewHolder>() {

    private var onItemClickListener: ((Movies) -> Unit)? = null

    fun setOnItemClickListener(listener: (Movies) -> Unit){
        onItemClickListener = listener
    }

    inner class ItemUserMovieViewHolder(private val binding: ItemHomemovieBinding):
        RecyclerView.ViewHolder(binding.root){

        fun bindItem(data: Movies){
            with(binding){
                Glide.with(context).load(data.image).into(txtImage)
                txtTitle.text = data.title
                txtDirector.text = data.director
                txtRating.text = data.rate
                itemView.setOnClickListener {
                    onItemClickListener?.invoke(data)
                }
            }
        }
    }

    fun setData(newData: List<Movies>) {
        listMovie.clear()
        listMovie.addAll(newData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemUserMovieViewHolder {
        val binding = ItemHomemovieBinding.inflate(
            LayoutInflater.from(
                parent.context
            ), parent, false
        )
        return ItemUserMovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemUserMovieViewHolder, position: Int) {
        holder.bindItem(listMovie[position])
    }

    override fun getItemCount(): Int = listMovie.size
}