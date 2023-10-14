package com.example.coderun

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.widget.ImageView
import java.util.Collections
import java.util.WeakHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ImageLoaderUtils private constructor(context: Context) {

    companion object {
        private var imageLoaderUtils: ImageLoaderUtils? = null

        fun getInstance(context: Context): ImageLoaderUtils {
            return this.imageLoaderUtils ?: synchronized(this) {
                val new = ImageLoaderUtils(context)
                imageLoaderUtils = new
                new
            }
        }
    }

    private var fileCache: FileCache

    private var executorService: ExecutorService

    private val memoryCache = MemoryCache()

    private var height: Int = 0

    private var width: Int = 0


    init {
        fileCache = FileCache(context)
        executorService = Executors.newFixedThreadPool(5)
    }

    fun setWidth(width: Int): ImageLoaderUtils {
        this.width = width
        return this
    }

    fun setHeight(height: Int): ImageLoaderUtils {
        this.height = height
        return this
    }

    fun setSizeView(view: ImageView) {
        view.post {
            this.height = view.height
            this.width = view.width
        }
    }

    private val imageViews = Collections.synchronizedMap(
        WeakHashMap<ImageView, String>()
    )


    var handler = Handler()

    val stub_id = R.drawable.ic_launcher_background

    fun load(url: String, imageView: ImageView?) {
        if (TextUtils.isEmpty(url)) {
            return
        }

        if (imageView == null) {
            return
        }
        imageViews[imageView] = url

        //Check image is stored in MemoryCache Map or not (see MemoryCache.java)
        val bitmap = memoryCache[url]
        if (bitmap != null) {
            // if image is stored in MemoryCache Map then
            // Show image in listview row
            imageView.setImageBitmap(bitmap)
        } else {
            //queue Photo to download from url
            queuePhoto(url, imageView)

            //Before downloading image show default image
            imageView.setImageResource(stub_id)
        }
    }


    private fun queuePhoto(url: String, imageView: ImageView) {
        // Store image and url in PhotoToLoad object
        val p = PhotoToLoad(url, imageView)

        // pass PhotoToLoad object to PhotosLoader runnable class
        // and submit PhotosLoader runnable to executers to run runnable
        // Submits a PhotosLoader runnable task for execution
        executorService!!.submit(PhotosLoader(p))
    }

    class PhotoToLoad(var url: String, var imageView: ImageView)

    inner class PhotosLoader(var photoToLoad: ImageLoaderUtils.PhotoToLoad) :
        Runnable {
        override fun run() {
            try {
                //Check if image already downloaded
                if (imageViewReused(photoToLoad)) return
                // download image from web url
                val bmp: Bitmap? = getBitmap(photoToLoad.url)

                // set image data in Memory Cache
                memoryCache.put(photoToLoad.url, bmp)
                if (imageViewReused(photoToLoad)) return

                // Get bitmap to display
                val bd = BitmapDisplayer(bmp, photoToLoad)

                // Causes the Runnable bd (BitmapDisplayer) to be added to the message queue.
                // The runnable will be run on the thread to which this handler is attached.
                // BitmapDisplayer run method will call
                handler.post(bd)
            } catch (th: Throwable) {
                th.printStackTrace()
            }
        }
    }

    fun getBitmap(url: String): Bitmap? {
        val f = fileCache!!.getFile(url)
        return try {

            //            BitmapFactory.Options options = new BitmapFactory.Options();
            //            options.inJustDecodeBounds = true;
            //            int imageHeight = options.outHeight;
            //            int imageWidth = options.outWidth;
            //            String imageType = options.outMimeType;
            // Decode bitmap with inSampleSize
            //            options.inSampleSize = calculateInSampleSize(options, imageWidth, imageHeight);
            //            options.inJustDecodeBounds = false;
            var bitmap = BitmapFactory.decodeFile(url)
            bitmap = Bitmap.createScaledBitmap(bitmap!!, 1080 / 3, 1080 / 3, false)
            bitmap
        } catch (ex: Throwable) {
            ex.printStackTrace()
            if (ex is OutOfMemoryError) memoryCache.clear()
            null
        }
    }

    fun imageViewReused(photoToLoad: ImageLoaderUtils.PhotoToLoad): Boolean {
        val tag = imageViews[photoToLoad.imageView]
        //Check url is already exist in imageViews MAP
        return if (tag == null || tag != photoToLoad.url) true else false
    }

    //Used to display bitmap in the UI thread
    inner class BitmapDisplayer(
        var bitmap: Bitmap?,
        var photoToLoad: ImageLoaderUtils.PhotoToLoad
    ) :
        Runnable {
        override fun run() {
            if (imageViewReused(photoToLoad)) return

            // Show bitmap on UI
            if (bitmap != null) {
                photoToLoad.imageView.setImageBitmap(bitmap)
                Log.i("namnx", "run: " + photoToLoad.imageView.width)
            } else {
                photoToLoad.imageView.setImageResource(stub_id)
            }
        }
    }

    fun clearCache() {
        //Clear cache directory downloaded images and stored data in maps
        memoryCache.clear()
        fileCache!!.clear()
    }

    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        val totalPixels = (width * height).toFloat()
        val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++
        }
        return inSampleSize
    }

}