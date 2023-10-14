package com.example.coderun.lib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.example.coderun.R;

import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageLoader {

    // Initialize MemoryCache
    MemoryCache memoryCache = new MemoryCache();

    FileCache fileCache;

    private int widthImageView = 0;
    private int heightImageView = 0;

    public void setWithHeight(int widthImageView, int heightImageView) {
        this.widthImageView = widthImageView;
        this.heightImageView = heightImageView;
    }

    //Create Map (collection) to store image and image url in key value pair
    private Map<ImageView, String> imageViews = Collections.synchronizedMap(
            new WeakHashMap<ImageView, String>());
    ExecutorService executorService;

    //handler to display images in UI thread
    Handler handler = new Handler();

    public ImageLoader(Context context) {

        fileCache = new FileCache(context);

        // Creates a thread pool that reuses a fixed number of
        // threads operating off a shared unbounded queue.
        executorService = Executors.newFixedThreadPool(5);

    }

    // default image show in list (Before online image download)
    final int stub_id = R.drawable.ic_launcher_background;

    public void DisplayImage(String url, ImageView imageView) {
        if (imageView == null) {
            return;
        }

        imageViews.put(imageView, url);

        //Check image is stored in MemoryCache Map or not (see MemoryCache.java)
        Bitmap bitmap = memoryCache.get(url);

        if (bitmap != null) {
            // if image is stored in MemoryCache Map then
            // Show image in listview row
            imageView.setImageBitmap(bitmap);
        } else {
            //queue Photo to download from url
            queuePhoto(url, imageView);

            //Before downloading image show default image
            imageView.setImageResource(stub_id);
        }
    }

    private void queuePhoto(String url, ImageView imageView) {
        // Store image and url in PhotoToLoad object
        PhotoToLoad p = new PhotoToLoad(url, imageView);

        // pass PhotoToLoad object to PhotosLoader runnable class
        // and submit PhotosLoader runnable to executers to run runnable
        // Submits a PhotosLoader runnable task for execution

        executorService.submit(new PhotosLoader(p));
    }

    //Task for the queue
    private class PhotoToLoad {
        public String url;
        public ImageView imageView;

        public PhotoToLoad(String u, ImageView i) {
            url = u;
            imageView = i;
        }
    }

    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;

        PhotosLoader(PhotoToLoad photoToLoad) {
            this.photoToLoad = photoToLoad;
        }

        @Override
        public void run() {
            try {
                //Check if image already downloaded
                if (imageViewReused(photoToLoad))
                    return;
                // download image from web url
                Bitmap bmp = getBitmap(photoToLoad.url);

                // set image data in Memory Cache
                memoryCache.put(photoToLoad.url, bmp);

                if (imageViewReused(photoToLoad))
                    return;

                // Get bitmap to display
                BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);

                // Causes the Runnable bd (BitmapDisplayer) to be added to the message queue.
                // The runnable will be run on the thread to which this handler is attached.
                // BitmapDisplayer run method will call
                handler.post(bd);

            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    private Bitmap getBitmap(String url) {
        File f = fileCache.getFile(url);
        try {

//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inJustDecodeBounds = true;
//            int imageHeight = options.outHeight;
//            int imageWidth = options.outWidth;
//            String imageType = options.outMimeType;
            // Decode bitmap with inSampleSize
//            options.inSampleSize = calculateInSampleSize(options, imageWidth, imageHeight);
//            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeFile(url);
            bitmap = Bitmap.createScaledBitmap(bitmap, 1080/3, 1080/3, false);
            return bitmap;

        } catch (Throwable ex) {
            ex.printStackTrace();
            if (ex instanceof OutOfMemoryError)
                memoryCache.clear();
            return null;
        }
    }

    boolean imageViewReused(PhotoToLoad photoToLoad) {

        String tag = imageViews.get(photoToLoad.imageView);
        //Check url is already exist in imageViews MAP
        if (tag == null || !tag.equals(photoToLoad.url))
            return true;
        return false;
    }

    //Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;

        public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
            bitmap = b;
            photoToLoad = p;
        }

        public void run() {
            if (imageViewReused(photoToLoad))
                return;

            // Show bitmap on UI
            if (bitmap != null) {
                photoToLoad.imageView.setImageBitmap(bitmap);
                Log.i("namnx", "run: "+photoToLoad.imageView.getWidth());
            } else {
                photoToLoad.imageView.setImageResource(stub_id);
            }
        }
    }

    public void clearCache() {
        //Clear cache directory downloaded images and stored data in maps
        memoryCache.clear();
        fileCache.clear();
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

}