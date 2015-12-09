package io.github.xiaolei.transaction.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;
import android.widget.RelativeLayout;

import java.util.Collection;

import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.adapter.PhotoListAdapter;
import io.github.xiaolei.transaction.entity.Photo;

/**
 * TODO: add comment
 */
public class PhotoGalleryView extends RelativeLayout {
    protected static final String TAG = PhotoGalleryView.class.getSimpleName();
    private ViewHolder mViewHolder;
    private Collection<Photo> mPhotoCollection;
    private PhotoListAdapter mAdapter;

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
    }

    public void bindData(Collection<Photo> photoCollection) {
        mPhotoCollection = photoCollection;

        if(mAdapter == null){
            mAdapter = new PhotoListAdapter(getContext(), photoCollection);
            mViewHolder.gridViewPhotos.setAdapter(mAdapter);
        }else{
            mAdapter.swap(photoCollection);
        }
    }

    private class ViewHolder {
        public DataContainerView dataContainerViewTransactionEditor;
        public GridView gridViewPhotos;

        public ViewHolder(View view) {
            dataContainerViewTransactionEditor = (DataContainerView) view.findViewById(R.id.dataContainerViewTransactionEditor);
            gridViewPhotos = (GridView) view.findViewById(R.id.gridViewPhotos);
        }
    }
}