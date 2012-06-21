
package com.silentlabs.android.mobilequeue.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

import com.silentlabs.android.mobilequeue.R;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

public class AsyncImageLoader {

    private final ConcurrentHashMap<URL, SoftReference<Bitmap>> imageCache;
    private static Resources res;

    public AsyncImageLoader(Resources _resources) {
        imageCache = new ConcurrentHashMap<URL, SoftReference<Bitmap>>();
        res = _resources;
    }

    public Bitmap loadBitmap(final URL imageUrl, final ImageCallback imageCallback) {
        if (imageCache.containsKey(imageUrl)) {
            SoftReference<Bitmap> softReference = imageCache.get(imageUrl);
            Bitmap bitmap = softReference.get();
            if (bitmap != null) {
                return bitmap;
            }
        }

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                imageCallback.imageLoaded((Bitmap) message.obj, imageUrl);
            }
        };

        new Thread() {
            @Override
            public void run() {
                Bitmap bitmap = loadImageFromUrl(imageUrl);
                imageCache.put(imageUrl, new SoftReference<Bitmap>(bitmap));
                Message message = handler.obtainMessage(0, bitmap);
                handler.sendMessage(message);
            }
        }.start();
        return null;
    }

    public static Bitmap loadImageFromUrl(URL url) {
        InputStream inBitmap = null;

        Bitmap bitmap;
        try {
            inBitmap = url.openConnection().getInputStream();
            bitmap = BitmapFactory.decodeStream(inBitmap);

        } catch (IOException e) {
            bitmap = BitmapFactory.decodeResource(res, R.drawable.netflix_branding);
        }

        return bitmap;
    }

    public interface ImageCallback {
        public void imageLoaded(Bitmap imageBitmap, URL imageUrl);
    }
}
