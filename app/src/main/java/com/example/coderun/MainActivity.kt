package com.example.coderun

import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.coderun.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: GlideAdapter
    private var listData = mutableListOf<String>()

    private var imageLoader: ImageLoader? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.initData()
        initView()
        binding.btLoadData.setOnClickListener {
            GlobalScope.launch {
                val list = getLocalImagePaths()
                withContext(Dispatchers.Main) {
                    listData.addAll(list.toMutableList())
                    adapter.notifyDataSetChanged()
                }
            }
        }

    }

    private fun initData() {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        ImageLoader(this).setWithHeight(height, width)
        val size = width / 3
        ImageLoaderUtils.getInstance(this).setHeight(size).setWidth(size)
    }

    private fun initView() {
        adapter = GlideAdapter(listData, this)
        binding.rvMain.adapter = adapter
    }

    suspend fun getLocalImagePaths(): List<String> {
        val result = mutableListOf<String>()
        val uri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        contentResolver.query(uri, projection, null, null, null)?.use {
            while (it.moveToNext()) {
                val pathFile = it.getString(0)
                result.add(pathFile)
//                result.add(
//                    ContentUris.withAppendedId(
//                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                        it.getString(0)
//                    )
//                )
            }
        }
        return result
    }

}