package com.example.coderun

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.coderun.adapter.GlideAdapter
import com.example.coderun.databinding.ActivityMainBinding
import com.example.coderun.lib.ImageLoader
import com.example.coderun.lib.ImageLoaderUtils
import com.example.coderun.model.ImageObject
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: GlideAdapter
    private var listData = mutableListOf<ImageObject>()
   private lateinit var gSon:Gson

    private var imageLoader: ImageLoader? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.initData()
        initView()
        initEvent()
         gSon= Gson()
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

    private fun getAllImage() {
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
        try {
            if (requestCode == 100 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getAllImage()
            }
        } catch (e: Exception) {
            e.printStackTrace()
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
        adapter.setOnclickDetailPhoto(object : GlideAdapter.OnClickDetailPhoto{
            override fun onClickDetailSelected(listNew: MutableList<Int>, position: Int) {
                val urlPath = listData[position]

                val intent = Intent(this@MainActivity, DetailPhotoActicity::class.java)
                intent.putExtra(Constants.URL_IMAGE, urlPath.avatar)
                this@MainActivity.startActivity(intent)
//                val list= mutableListOf<Int>()
//                list.addAll(listNew)
//                for (index in list) {
//                    Log.e("INdexURL",  listData[index].avatar)
//
//                }
            }

            override fun onClickDetail(item: ImageObject) {
                val intent = Intent(this@MainActivity, DetailPhotoActicity::class.java)
                intent.putExtra(Constants.URL_IMAGE, item.avatar)
                this@MainActivity.startActivity(intent)
            }


        })
    }

    override fun onBackPressed() {
        if (adapter.onMode) {
            adapter.onMode = false
            if (adapter.listManagerPosSelect.isNotEmpty()) {
                for (i in adapter.listManagerPosSelect) {
                    listData[i].isSelected = false
                    listData[i].valeStt = -1
                }
            }
            adapter.listManagerPosSelect.clear()
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