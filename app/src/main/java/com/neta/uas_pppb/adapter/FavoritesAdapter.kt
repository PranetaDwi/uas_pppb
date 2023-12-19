package com.neta.uas_pppb.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.neta.uas_pppb.databinding.ItemFavoritBinding
import com.neta.uas_pppb.firebase.Movies

class FavoritesAdapter (private val context: Context, private var listMovie: MutableList<Movies>): RecyclerView.Adapter<FavoritesAdapter.ItemFavoritsViewHolder>() {


    inner class ItemFavoritsViewHolder(private val binding: ItemFavoritBinding):

        RecyclerView.ViewHolder(binding.root){

        fun bindItem(data: Movies){
            with(binding){
                Glide.with(context).load(data.image).into(txtImage)
                txtTitle.text = data.title
                txtDirector.text = data.director
                txtRating.text = data.rate
                txtDuration.text = data.duration
                txtGenre.text = data.genre
            }
        }
    }

    fun setData(newData: List<Movies>) {
        listMovie.clear()
        listMovie.addAll(newData)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemFavoritsViewHolder {
        val binding = ItemFavoritBinding.inflate(
            LayoutInflater.from(
                parent.context
            ), parent, false
        )
        return ItemFavoritsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemFavoritsViewHolder, position: Int) {
        holder.bindItem(listMovie[position])
    }

    override fun getItemCount(): Int = listMovie.size
}