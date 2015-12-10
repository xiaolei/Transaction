package io.github.xiaolei.transaction.util;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import io.github.xiaolei.transaction.R;

/**
 * TODO: add comment
 */
public class ImageLoader {

    public static void loadImage(Context context, String imageUrl, final ImageView imageView) {
        if (context == null || TextUtils.isEmpty(imageUrl) || imageView == null) {
            return;
        }

        Picasso.with(context)
                .load(imageUrl)
                .fit()
                .centerCrop()
                .error(R.drawable.bitmap_missing)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    }

                    @Override
                    public void onError() {
                        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    }
                });
    }
}
