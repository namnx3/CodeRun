package com.example.coderun.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.coderun.lib.ImageLoader
import com.example.coderun.lib.ImageLoaderUtils
import com.example.coderun.R
import com.example.coderun.databinding.ItemImageSlideBinding
import com.example.coderun.model.ImageObject

class GlideAdapter(
    private val listData: List<ImageObject>,
    val context: Context
) : RecyclerView.Adapter<GlideAdapter.GlideVH>() {
    var onItemLongClick: ((Int) -> Unit)? = null
    var onItemSelected: ((Int) -> Unit)? = null
    var onMode: Boolean = false
    private var imageLoader: ImageLoader? = null
    var listManagerPosSelect = mutableListOf<Int>()
    private var onClickDetailPhoto: OnClickDetailPhoto? = null


    init {
        this.imageLoader = ImageLoader(context)
    }


    inner class GlideVH(val binding: ItemImageSlideBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ImageObject) {
            ImageLoaderUtils.getInstance(context).load(item.avatar, this.binding.ivImageSlide)
            if (onMode) {
                binding.lnRootChooseItemImgSlide.visibility = View.VISIBLE
                if (!item.isSelected) {
                    binding.tvNumberSelectImageSlide.visibility = View.GONE
                    binding.rlBackGroundSelectImageSlide.background = ContextCompat.getDrawable(
                        context,
                        R.drawable.bg_circle_unchecked
                    )
                    binding.ivImageSlide.alpha = 1f
                } else {
                    if (item.valeStt != -1) {
                        binding.tvNumberSelectImageSlide.visibility = View.VISIBLE
                        binding.tvNumberSelectImageSlide.text = item.valeStt.toString()
                    }
                    binding.lnRootChooseItemImgSlide.visibility = View.VISIBLE
                    binding.rlBackGroundSelectImageSlide.background = ContextCompat.getDrawable(
                        context,
                        R.drawable.bg_circle_checked
                    )
                    binding.ivImageSlide.alpha = 0.5f
                }
            } else {
                binding.lnRootChooseItemImgSlide.visibility = View.GONE
                binding.ivImageSlide.alpha = 1f
            }
            binding.root.setOnLongClickListener {
                if (!item.isSelected) {
                    onItemLongClick?.invoke(position)
                }
                false
            }
//            binding.root.setOnClickListener {
//                if (item.isSelected) {
//                    onClickDetailPhoto?.onClickDetailSelected(listManagerPosSelect, adapterPosition)
//                } else {
//                    onClickDetailPhoto?.onClickDetail(item)
//                }
//
//            }
//            binding.lnRootChooseItemImgSlide.setOnClickListener {
//                onItemSelected?.invoke(position)
//            }
            binding.root.setOnClickListener {
                if (onMode) {
                    onItemSelected?.invoke(position)
                } else {
                    if (item.isSelected) {
                        onClickDetailPhoto?.onClickDetailSelected(
                            listManagerPosSelect,
                            adapterPosition
                        )
                    } else {
                        onClickDetailPhoto?.onClickDetail(item)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GlideVH {
        val vh = ItemImageSlideBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GlideVH(vh)
    }

    override fun onBindViewHolder(holder: GlideVH, position: Int) {
        holder.bind(listData[position])
    }

    fun setOnclickDetailPhoto(onClickDetailPhoto: OnClickDetailPhoto) {
        this.onClickDetailPhoto = onClickDetailPhoto
    }

    override fun getItemCount(): Int = listData.size

    interface OnClickDetailPhoto {
        fun onClickDetailSelected(listData: MutableList<Int>, position: Int)
        fun onClickDetail(item: ImageObject)
    }
}