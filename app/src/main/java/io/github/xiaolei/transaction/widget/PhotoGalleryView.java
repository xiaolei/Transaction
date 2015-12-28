package io.github.xiaolei.transaction.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.adapter.PhotoListAdapter;
import io.github.xiaolei.transaction.entity.Photo;
import io.github.xiaolei.transaction.listener.PicassoScrollListener;

/**
 * TODO: add comment
 */
public class PhotoGalleryView extends RelativeLayout {
    protected static final String TAG = PhotoGalleryView.class.getSimpleName();
    private ViewHolder mViewHolder;
    private Collection<Photo> mPhotoCollection;
    private PhotoListAdapter mAdapter;
    private OnPhotoClickListener mOnPhotoClickListener;

    public PhotoGalleryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize(context, attrs);
    }

    public PhotoGalleryView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.initialize(context, attrs);
    }

    public PhotoGalleryView(Context context) {
        super(context);
        this.initialize(context, null);
    }

    protected void initialize(Context context, AttributeSet attrs) {
        View view = View.inflate(context, R.layout.view_photo_gallery, this);
        mViewHolder = new ViewHolder(view);
        mViewHolder.gridViewPhotos.setOnScrollListener(new PicassoScrollListener(getContext()));
        mViewHolder.gridViewPhotos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Photo currentPhoto = (Photo) parent.getItemAtPosition(position);
                onPhotoClick(currentPhoto, position);
            }
        });
    }

    public void setOnPhotoClickListener(OnPhotoClickListener onPhotoClickListener) {
        mOnPhotoClickListener = onPhotoClickListener;
    }

    protected void onPhotoClick(Photo photo, int position) {
        if (mOnPhotoClickListener != null) {
            mOnPhotoClickListener.onPhotoClick(photo, position);
        }
    }

    public List<String> getPhotoUrls() {
        ArrayList<String> result = new ArrayList<String>();
        for (Photo photo : mPhotoCollection) {
            result.add(photo.getUrl());
        }

        return result;
    }

    public void bindData(List<Photo> photoCollection) {
        mPhotoCollection = photoCollection;

        if (mAdapter == null) {
            mAdapter = new PhotoListAdapter(getContext(), photoCollection);
            mViewHolder.gridViewPhotos.setAdapter(mAdapter);
        } else {
            mAdapter.swap(photoCollection);
        }

        if (photoCollection.size() > 0) {
            mViewHolder.relativeLayoutPhotoGalleryHeader.setVisibility(View.VISIBLE);
            mViewHolder.textViewTransactionPhotoCount.setText(String.format(getContext().getString(R.string.total_photo_count), photoCollection.size()));
        } else {
            mViewHolder.relativeLayoutPhotoGalleryHeader.setVisibility(View.GONE);
        }
    }

    public interface OnPhotoClickListener {
        void onPhotoClick(Photo photo, int position);
    }

    private class ViewHolder {
        public DataContainerView dataContainerViewTransactionEditor;
        public GridView gridViewPhotos;
        public TextView textViewTransactionPhotoCount;
        public RelativeLayout relativeLayoutPhotoGalleryHeader;

        public ViewHolder(View view) {
            dataContainerViewTransactionEditor = (DataContainerView) view.findViewById(R.id.dataContainerViewTransactionEditor);
            gridViewPhotos = (GridView) view.findViewById(R.id.gridViewPhotos);
            textViewTransactionPhotoCount = (TextView) view.findViewById(R.id.textViewPhotoCount);
            relativeLayoutPhotoGalleryHeader = (RelativeLayout) view.findViewById(R.id.relativeLayoutPhotoGalleryHeader);
        }
    }
}