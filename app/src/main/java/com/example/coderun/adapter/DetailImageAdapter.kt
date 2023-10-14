package com.example.coderun.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Matrix
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.coderun.databinding.DetailImageBinding
import com.example.coderun.model.Photo

class DetailImageAdapter(var context:Context,var listPhoto:MutableList<Photo>) : RecyclerView.Adapter<DetailImageAdapter.DetailViewHolder>() {
//    private var onclickDetailPhoto: DetailViewHolder.OnclickDetailPhoto?=null
    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private var scaleFactor = 1.0f

    companion object {
        private const val NONE = 0
        private const val DRAG = 1
        private const val ZOOM = 2
    }


    inner class DetailViewHolder(private var binding: DetailImageBinding) : RecyclerView.ViewHolder(binding.root) {

        private var matrix = Matrix()
        private var mode = NONE
        private var lastEvent: MotionEvent? = null
        private var prevX = 0f
        private var prevY = 0f

        @SuppressLint("ClickableViewAccessibility")
        fun onBind(photo: Photo?) {
            binding.imgPhoto.setImageResource(photo?.img!!)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        var binding=DetailImageBinding.inflate(LayoutInflater.from(context),parent,false)
        return DetailViewHolder(binding)
    }

    override fun getItemCount(): Int {
       return listPhoto.size
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
      holder.onBind(listPhoto[position])
    }
    fun  setOnclickDetailPhoto(onclickDetailPhoto: OnclickDetailPhoto){
//        this.onclickDetailPhoto=onclickDetailPhoto
    }
    interface OnclickDetailPhoto{
        fun onClickDetailPhoto()
    }

}