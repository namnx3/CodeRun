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


    companion object {
        private const val NONE = 0
        private const val DRAG = 1
        private const val ZOOM = 2
    }


    inner class DetailViewHolder(private var binding: DetailImageBinding) : RecyclerView.ViewHolder(binding.root) {


        @SuppressLint("ClickableViewAccessibility")
        fun onBind(photo: Photo?) {
            val scaleGestureDetector = ScaleGestureDetector(context, ScaleListener(binding.imgPhoto))
            binding.imgPhoto.setImageResource(photo?.img!!)
//            binding.imgPhoto.setOnTouchListener { v, event ->
//                scaleGestureDetector.onTouchEvent(event)
//                return@setOnTouchListener true
//            }

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
//    fun  setOnTouchDetailPhoto(onTouchDetailPhoto: OnTouchListenerCallback){
//        this.onTouchListenerCallback=onTouchDetailPhoto
//    }
//    interface OnTouchListenerCallback {
//        fun onItemTouched(v:View,  event:MotionEvent,photo: Photo?)
//    }

    inner class ScaleListener(private val imageView: AppCompatImageView) : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        private var scaleFactor = 1.0f

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 5.0f))
            imageView.scaleX = scaleFactor
            imageView.scaleY = scaleFactor
            return true
        }
    }


}