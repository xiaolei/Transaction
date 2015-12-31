package io.github.xiaolei.transaction.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.util.ImageLoader;
import io.github.xiaolei.transaction.widget.DataContainerView;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

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
        mViewHolder.imageView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                toggleActionBar();
            }
        });

        return view;
    }

    private void toggleActionBar() {
        if (!isAdded()) {
            return;
        }

        BaseActivity baseActivity = (BaseActivity) getActivity();
        baseActivity.toggleActionBar(R.id.toolbarPhotoList);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadImage(mPhotoUrl);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void loadImage(String imageUri) {
        if (TextUtils.isEmpty(imageUri) || mViewHolder == null) {
            return;
        }

        ImageLoader.loadImage(getActivity(), imageUri, mViewHolder.imageView, ImageLoader.PhotoScaleMode.CENTER_INSIDE);
    }

    private class ViewHolder {
        public PhotoView imageView;
        public DataContainerView dataContainerViewPhotoView;
        public LinearLayout linearLayoutPhotoContainer;

        public ViewHolder(View view) {
            dataContainerViewPhotoView = (DataContainerView) view.findViewById(R.id.dataContainerViewPhotoView);
            imageView = (PhotoView) view.findViewById(R.id.imageView);
            linearLayoutPhotoContainer = (LinearLayout) view.findViewById(R.id.linearLayoutPhotoContainer);
        }
    }
}
