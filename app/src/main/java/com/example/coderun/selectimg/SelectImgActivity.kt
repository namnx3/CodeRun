package com.example.coderun.selectimg

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.coderun.R
import com.example.coderun.databinding.ActivityMainBinding
import com.example.coderun.databinding.ActivitySelectImgBinding
import com.example.coderun.databinding.ItemImageSlideBinding

class SelectImgActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectImgBinding
    private var listData = mutableListOf<ImageObject>()
    private lateinit var adapter: SelectAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectImgBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getListData()
        initView()
        initEvent()
    }

    private fun initEvent() {
        adapter.onItemLongClick = {
            adapter.onMode = true
            adapter.notifyDataSetChanged()
        }
        adapter.onItemSelected = { pos ->
            listData[pos].isSelected = !listData[pos].isSelected
            adapter.notifyItemChanged(pos)
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

    private fun initView() {
        adapter = SelectAdapter(listData, this)
        binding.rvSelect.adapter = adapter
    }

    fun getListData() {
        listData.add(ImageObject(R.drawable.img))
        listData.add(ImageObject(R.drawable.img_1))
        listData.add(ImageObject(R.drawable.img_2))
        listData.add(ImageObject(R.drawable.img_3))

        listData.add(ImageObject(R.drawable.img))
        listData.add(ImageObject(R.drawable.img_1))
        listData.add(ImageObject(R.drawable.img_2))
        listData.add(ImageObject(R.drawable.img_3))

        listData.add(ImageObject(R.drawable.img))
        listData.add(ImageObject(R.drawable.img_1))
        listData.add(ImageObject(R.drawable.img_2))
        listData.add(ImageObject(R.drawable.img_3))
    }
}