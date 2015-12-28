package io.github.xiaolei.transaction.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Collection;
import java.util.List;

import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.entity.Photo;
import io.github.xiaolei.transaction.util.CollectionHelper;
import io.github.xiaolei.transaction.util.ImageLoader;

/**
 * TODO: add comment
 */
public class PhotoListAdapter extends GenericListAdapter<Photo, PhotoListAdapter.ViewHolder> {

    public PhotoListAdapter(Context context, List<Photo> items) {
        super(context, items);
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.item_photo;
    }

    @Override
    public ViewHolder createViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public void bindData(final ViewHolder viewHolder, Photo viewModel) {
        ImageLoader.loadImage(getContext(), viewModel.getUrl(), viewHolder.imageViewPhoto,
                ImageLoader.PhotoScaleMode.CENTER_CROP);

        if (TextUtils.isEmpty(viewModel.getDescription())) {
            viewHolder.textViewPhotoName.setVisibility(View.GONE);
        } else {
            viewHolder.textViewPhotoName.setVisibility(View.VISIBLE);
            viewHolder.textViewPhotoName.setText(viewModel.getDescription());
        }
    }

    public class ViewHolder {
        public ImageView imageViewPhoto;
        public TextView textViewPhotoName;

        public ViewHolder(View view) {
            imageViewPhoto = (ImageView) view.findViewById(R.id.imageViewPhoto);
            textViewPhotoName = (TextView) view.findViewById(R.id.textViewPhotoName);
        }
    }
}
