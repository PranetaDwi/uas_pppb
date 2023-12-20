package com.neta.uas_pppb.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.neta.uas_pppb.database.CobaMovies
import com.neta.uas_pppb.databinding.ItemHomemovieBinding

class MovieUserOfflineAdapter (private val context:Context, private var listMovie: List<CobaMovies>): RecyclerView.Adapter<MovieUserOfflineAdapter.ItemUserMovieViewHolder>(){

    private var onItemClickListener: ((CobaMovies) -> Unit)? = null

    fun setOnItemClickListener(listener: (CobaMovies) -> Unit){
        onItemClickListener = listener
    }

    inner class ItemUserMovieViewHolder(private val binding: ItemHomemovieBinding):
        RecyclerView.ViewHolder(binding.root){
        fun bindItem(data: CobaMovies){

            with(binding){
                Glide.with(context).load(data.image).into(txtImage)
                txtTitle.text = data.title
                txtDirector.text = data.director
                txtRating.text = data.rate
                txtDuration.text = data.duration
                txtGenre.text = data.genre
                itemView.setOnClickListener {
                    onItemClickListener?.invoke(data)
                }
            }
        }
    }

    fun setData(newData: List<CobaMovies>) {
        listMovie = newData
        notifyDataSetChanged()
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
