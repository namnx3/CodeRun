package com.example.coderun

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.example.coderun.adapter.DetailImageAdapter
import com.example.coderun.databinding.ActivityMainBinding
import com.example.coderun.model.Photo

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var detailImageAdapter = DetailImageAdapter(this,getListPhoto())
        binding.imgDetail.adapter=detailImageAdapter
        detailImageAdapter.setOnclickDetailPhoto(object :DetailImageAdapter.OnclickDetailPhoto{
            override fun onClickDetailPhoto() {

            }

        })

    }

    fun getListPhoto():MutableList<Photo>{
        var listPhoto:MutableList<Photo> = mutableListOf()
        listPhoto.add(Photo(R.drawable.img1))
        listPhoto.add(Photo(R.drawable.img2))
        listPhoto.add(Photo(R.drawable.img3))
        listPhoto.add(Photo(R.drawable.img4))
        return listPhoto
    }
}