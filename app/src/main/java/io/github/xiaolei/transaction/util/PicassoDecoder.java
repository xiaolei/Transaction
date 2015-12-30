package io.github.xiaolei.transaction.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.davemorrissey.labs.subscaleview.decoder.SkiaImageDecoder;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import io.github.xiaolei.transaction.R;

/**
 * https://gist.github.com/davemorrissey/e2781ba5b966c9e95539
 */
public class PicassoDecoder extends SkiaImageDecoder {
    public PicassoDecoder() {
    }

    @Override
    public Bitmap decode(Context context, Uri uri) throws Exception {
        if (uri.toString().startsWith("http")) {
            return Picasso.with(context)
                    .load(uri)
                    .error(R.drawable.bitmap_missing)
                    .config(Bitmap.Config.RGB_565)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .get();
        } else {
            return super.decode(context, uri);
        }
    }
}