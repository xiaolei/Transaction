package io.github.xiaolei.transaction.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.util.ImageLoader;
import io.github.xiaolei.transaction.util.PicassoDecoder;
import io.github.xiaolei.transaction.util.PicassoRegionDecoder;
import io.github.xiaolei.transaction.widget.DataContainerView;

/**
 * TODO: add comment
 */
public class PhotoViewFragment extends Fragment {
    private ViewHolder mViewHolder;
    public static final String ARG_PHOTO_URL = "arg_photo_url";
    private String mPhotoUrl;

    public static PhotoViewFragment newInstance(String photoUrl) {
        PhotoViewFragment result = new PhotoViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PHOTO_URL, photoUrl);
        result.setArguments(args);

        return result;
    }

    public PhotoViewFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            mPhotoUrl = args.getString(ARG_PHOTO_URL);
        }

        View view = inflater.inflate(R.layout.fragment_photo_view, container, false);
        mViewHolder = new ViewHolder(view);
        mViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseActivity baseActivity = (BaseActivity) getActivity();
                baseActivity.toggleActionBar(R.id.toolbarPhotoList);
            }
        });

        mViewHolder.imageView.setOnImageEventListener(new SubsamplingScaleImageView.OnImageEventListener() {
            @Override
            public void onReady() {
                mViewHolder.dataContainerViewPhotoView.switchToDataView();
            }

            @Override
            public void onImageLoaded() {
                mViewHolder.dataContainerViewPhotoView.switchToDataView();
            }

            @Override
            public void onPreviewLoadError(Exception e) {
                showErrorImage();
            }

            @Override
            public void onImageLoadError(Exception e) {
                showErrorImage();
            }

            @Override
            public void onTileLoadError(Exception e) {
                showErrorImage();
            }
        });

        mViewHolder.imageView.setBitmapDecoderClass(PicassoDecoder.class);
        mViewHolder.imageView.setRegionDecoderClass(PicassoRegionDecoder.class);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadImage(mPhotoUrl);
    }

    private void showErrorImage() {
        mViewHolder.dataContainerViewPhotoView.switchToDataView();
        mViewHolder.imageView.setImage(ImageSource.resource(R.drawable.bitmap_missing));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    public void loadImage(String imageUri) {
        if (TextUtils.isEmpty(imageUri) || mViewHolder == null) {
            return;
        }

        ImageLoader.loadImage(getActivity(), imageUri, mViewHolder.imageView);
    }

    private class ViewHolder {
        public SubsamplingScaleImageView imageView;
        public DataContainerView dataContainerViewPhotoView;

        public ViewHolder(View view) {
            dataContainerViewPhotoView = (DataContainerView) view.findViewById(R.id.dataContainerViewPhotoView);
            imageView = (SubsamplingScaleImageView) view.findViewById(R.id.imageView);
        }
    }
}
