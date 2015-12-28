package io.github.xiaolei.transaction.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.w3c.dom.Text;

import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.util.ImageLoader;
import io.github.xiaolei.transaction.widget.DataContainerView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * TODO: add comment
 */
public class PhotoViewFragment extends Fragment {
    private ViewHolder mViewHolder;
    public static final String ARG_PHOTO_URL = "arg_photo_url";
    private String mPhotoUrl;
    private PhotoViewAttacher mPhotoViewAttacher;

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
        mPhotoViewAttacher = new PhotoViewAttacher(mViewHolder.imageView);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadImage(mPhotoUrl);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mPhotoViewAttacher.cleanup();
    }

    public void loadImage(String imageUri) {
        if (TextUtils.isEmpty(imageUri) || mViewHolder == null) {
            return;
        }

        ImageLoader.loadImage(getActivity(), imageUri, mViewHolder.imageView,
                ImageLoader.PhotoScaleMode.CENTER_INSIDE);
        mPhotoViewAttacher.update();
    }

    private class ViewHolder {
        public ImageView imageView;
        public DataContainerView dataContainerViewPhotoView;

        public ViewHolder(View view) {
            dataContainerViewPhotoView = (DataContainerView) view.findViewById(R.id.dataContainerViewPhotoView);
            imageView = (ImageView) view.findViewById(R.id.imageView);
        }
    }
}
