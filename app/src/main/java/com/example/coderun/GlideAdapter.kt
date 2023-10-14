package com.example.coderun

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.coderun.databinding.ItemImageSlideBinding

class GlideAdapter(
    val listData: List<String>,
    val context: Context
) : RecyclerView.Adapter<GlideAdapter.GlideVH>() {

    private var imageLoader: ImageLoader? = null

    init {
        this.imageLoader = ImageLoader(context)
    }

    inner class GlideVH(val binding: ItemImageSlideBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            imageLoader?.DisplayImage(item, this.binding.ivImageSlide)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GlideAdapter.GlideVH {
        val vh = ItemImageSlideBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GlideVH(vh)
    }

    override fun onBindViewHolder(holder: GlideAdapter.GlideVH, position: Int) {
        holder.bind(listData[position])
    }

    override fun getItemCount(): Int = listData.size
}