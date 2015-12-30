package io.github.xiaolei.transaction.util;

/**
 * TODO: add comment
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;

import com.davemorrissey.labs.subscaleview.decoder.ImageRegionDecoder;
import com.davemorrissey.labs.subscaleview.decoder.SkiaImageRegionDecoder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;

import java.io.InputStream;

/**
 * https://gist.github.com/davemorrissey/e2781ba5b966c9e95539
 */
public class PicassoRegionDecoder extends SkiaImageRegionDecoder {
    private OkHttpClient client;
    private BitmapRegionDecoder decoder;
    private final Object decoderLock = new Object();

    public PicassoRegionDecoder() {
        this.client = new OkHttpClient();
    }

    public PicassoRegionDecoder(OkHttpClient client) {
        this.client = client;
    }

    @Override
    public Point init(Context context, Uri uri) throws Exception {
        if (uri.toString().startsWith("http")) {
            OkHttpDownloader downloader = new OkHttpDownloader(client);
            InputStream inputStream = downloader.load(uri, 0).getInputStream();
            this.decoder = BitmapRegionDecoder.newInstance(inputStream, false);

            return new Point(this.decoder.getWidth(), this.decoder.getHeight());
        } else {
            return super.init(context, uri);
        }
    }

    @Override
    public Bitmap decodeRegion(Rect rect, int sampleSize) {
        if (this.decoder != null) {
            synchronized (this.decoderLock) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = sampleSize;
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                Bitmap bitmap = this.decoder.decodeRegion(rect, options);
                if (bitmap == null) {
                    throw new RuntimeException("Region decoder returned null bitmap - image format may not be supported");
                } else {
                    return bitmap;
                }
            }
        } else {
            return super.decodeRegion(rect, sampleSize);
        }
    }

    @Override
    public boolean isReady() {
        if (this.decoder != null) {
            return this.decoder != null && !this.decoder.isRecycled();
        } else {
            return super.isReady();
        }
    }

    @Override
    public void recycle() {
        if (this.decoder != null) {
            this.decoder.recycle();
        } else {
            super.recycle();
        }
    }
}