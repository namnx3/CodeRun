package com.example.coderun

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.coderun.adapter.GlideAdapter
import com.example.coderun.databinding.ActivityMainBinding
import com.example.coderun.lib.ImageLoader
import com.example.coderun.lib.ImageLoaderUtils
import com.example.coderun.model.ImageObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: GlideAdapter
    private var listData = mutableListOf<ImageObject>()

    private var imageLoader: ImageLoader? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.initData()
        initView()
        initEvent()

        if (checkPermission()) {
            getAllImage()
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                arrayOf(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        Manifest.permission.READ_MEDIA_IMAGES
                    } else {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }
                ), 100
            )
        }

    }

    private fun getAllImage(){
        GlobalScope.launch {
            val list = getLocalImagePaths()
            for (item in list) {
                listData.add(ImageObject(item))
            }
            withContext(Dispatchers.Main) {
                adapter.notifyDataSetChanged()
            }
        }
    }


    private fun checkPermission(): Boolean {
        val permissionString = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            return true
        }

        if (checkSelfPermission(permissionString) == PackageManager.PERMISSION_DENIED) {
            return false
        }

        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getAllImage()
        }
    }

    private fun initEvent() {
        adapter.onItemLongClick = {
            adapter.onMode = true
            adapter.notifyDataSetChanged()
        }
        adapter.onItemSelected = { posSelect ->
            listData[posSelect].isSelected = !listData[posSelect].isSelected
            if (listData[posSelect].isSelected) {
                adapter.listManagerPosSelect.add(posSelect)
                listData[posSelect].valeStt =
                    adapter.listManagerPosSelect.indexOf(posSelect) + 1
                adapter.notifyItemChanged(posSelect)
            } else {
                listData[posSelect].valeStt = -1
                adapter.notifyItemChanged(posSelect)
                val indexStart = adapter.listManagerPosSelect.indexOf(posSelect)
                adapter.listManagerPosSelect.remove(posSelect)
                for (i in indexStart until adapter.listManagerPosSelect.size) {
                    listData[adapter.listManagerPosSelect[i]].valeStt = i + 1
                    adapter.notifyItemChanged(adapter.listManagerPosSelect[i])
                }
            }
        }
    }

    override fun onBackPressed() {
        if (adapter.onMode) {
            adapter.onMode = false
            adapter.notifyDataSetChanged()
        } else {
            super.onBackPressed()
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