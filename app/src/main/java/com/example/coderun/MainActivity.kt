package com.example.coderun

import android.content.ContentUris
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import androidx.recyclerview.widget.GridLayoutManager
import com.example.coderun.databinding.ActivityMainBinding
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: GlideAdapter
    private var listData = mutableListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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