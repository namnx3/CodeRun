package com.example.coderun

import android.graphics.Matrix
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import com.example.coderun.adapter.ImageDetailAdapter
import com.example.coderun.databinding.ActivityDetailPhotoActicityBinding
import com.example.coderun.model.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailPhotoActicity : AppCompatActivity() {
    private lateinit var binding:ActivityDetailPhotoActicityBinding
    private var detailImageAdapter:ImageDetailAdapter?=null
    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private var scaleFactor = 1.0f
    private var matrix = Matrix()
    private var mode = NONE
    private var lastEvent: MotionEvent? = null
    private var prevX = 0f
    private var prevY = 0f
    val listPhoto:MutableList<Photo> = mutableListOf()
    companion object {
        private const val NONE = 0
        private const val DRAG = 1
        private const val ZOOM = 2
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityDetailPhotoActicityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        detailImageAdapter=ImageDetailAdapter(this,getListPhoto())
        binding.vpDetailPhoto.adapter=detailImageAdapter;





    }

    private fun getListPhoto():MutableList<Photo>{
        val listPhoto:MutableList<Photo> = mutableListOf()
        listPhoto.add(Photo(R.drawable.img1))
        listPhoto.add(Photo(R.drawable.img2))
        listPhoto.add(Photo(R.drawable.img3))
        listPhoto.add(Photo(R.drawable.img4))
        return listPhoto
    }




}