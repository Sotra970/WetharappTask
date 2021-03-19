package com.weatherapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.weatherapp.domain.entities.ImageEntity

class GalleryAdapter constructor(private val callback :GalleryListItemCallback): RecyclerView.Adapter<GalleryAdapter.BudgetViewHolder>() {
    private val items: MutableList<ImageEntity> = mutableListOf()
    companion object: DiffUtil.ItemCallback<ImageEntity>() {
        override fun areItemsTheSame(oldItem: ImageEntity, newItem: ImageEntity): Boolean = oldItem === newItem
        override fun areContentsTheSame(oldItem: ImageEntity, newItem: ImageEntity): Boolean = oldItem.id == newItem.id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ImageItemBinding.inflate(inflater , parent , false)
        return BudgetViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: BudgetViewHolder, position: Int) {
        val item = items.get(position)
        holder.binding.item = item
        holder.binding.callback =callback
        Glide.with(holder.binding.galleryImage.context)
            .load(item.uri)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .apply( RequestOptions().centerCrop())
            .transition( DrawableTransitionOptions().crossFade())
            .into(holder.binding.galleryImage)
        holder.binding.executePendingBindings()
    }
    fun add(data : List<ImageEntity>) {
        items.clear()
        items.addAll(data)
        notifyDataSetChanged()
    }
    class BudgetViewHolder(val binding: ImageItemBinding) : RecyclerView.ViewHolder(binding.root)




}