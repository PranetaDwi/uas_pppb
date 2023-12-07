package com.neta.uas_pppb

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.neta.uas_pppb.databinding.ItemAdminmovieBinding

class MovieadminAdapter(private val context: Context, private var listMovie: MutableList<Movies>): RecyclerView.Adapter<MovieadminAdapter.ItemAdminmovieViewHolder>() {

    inner class ItemAdminmovieViewHolder(private val binding: ItemAdminmovieBinding):
            RecyclerView.ViewHolder(binding.root){

                fun bindItem(data: Movies){
                    with(binding){
                        Glide.with(context).load(data.image).into(txtImage)
                        txtTitle.text = data.title
                        txtDetail.text = data.detail
                        txtDirector.text = data.director
                        txtRating.text = data.rate
                    }
                }
            }

    fun setData(newData: List<Movies>) {
        listMovie.clear()
        listMovie.addAll(newData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemAdminmovieViewHolder {
        val binding = ItemAdminmovieBinding.inflate(
            LayoutInflater.from(
                parent.context
            ), parent, false
        )
        return ItemAdminmovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemAdminmovieViewHolder, position: Int) {
        holder.bindItem(listMovie[position])
    }

    override fun getItemCount(): Int = listMovie.size
}