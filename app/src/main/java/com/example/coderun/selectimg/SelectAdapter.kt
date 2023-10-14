package com.example.coderun.selectimg

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.coderun.R
import com.example.coderun.databinding.ItemImageSlideBinding

class SelectAdapter(
    private val listData: List<ImageObject>,
    val context: Context
) : RecyclerView.Adapter<SelectAdapter.GlideVH>() {
    var onItemLongClick: ((Int) -> Unit)? = null
    var onItemSelected: ((Int) -> Unit)? = null
    var onMode: Boolean = false

    inner class GlideVH(private val binding: ItemImageSlideBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ImageObject, position: Int) {
            binding.ivImageSlide.setImageResource(item.avatar)

            if (onMode) {
                binding.lnRootChooseItemImgSlide.visibility = View.VISIBLE
                if (!item.isSelected) {
                    binding.rlBackGroundSelectImageSlide.background = ContextCompat.getDrawable(
                        context,
                        R.drawable.bg_circle_unchecked
                    )
                    binding.ivImageSlide.alpha = 1f
                } else {
                    binding.rlBackGroundSelectImageSlide.background = ContextCompat.getDrawable(
                        context,
                        R.drawable.bg_circle_checked
                    )
                    binding.ivImageSlide.alpha = 0.5f
                }
            } else {
                binding.lnRootChooseItemImgSlide.visibility = View.GONE
            }
            binding.root.setOnLongClickListener {
                if (!item.isSelected) {
                    onItemLongClick?.invoke(position)
                }
                false
            }
            binding.lnRootChooseItemImgSlide.setOnClickListener {
                onItemSelected?.invoke(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectAdapter.GlideVH {
        val vh = ItemImageSlideBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GlideVH(vh)
    }

    override fun onBindViewHolder(holder: SelectAdapter.GlideVH, position: Int) {
        holder.bind(listData[position], position)
    }

    override fun getItemCount(): Int = listData.size
}