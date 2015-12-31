package io.github.xiaolei.transaction.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import io.github.xiaolei.enterpriselibrary.utility.DownloadFileUtil;
import io.github.xiaolei.enterpriselibrary.utility.DownloadManager;
import io.github.xiaolei.enterpriselibrary.utility.DownloadManager.DownloaderCallback;
import io.github.xiaolei.enterpriselibrary.utility.PhotoPicker;
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
    private String mDownloadFileName;

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
        mViewHolder.imageView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                BaseActivity baseActivity = (BaseActivity) getActivity();
                baseActivity.toggleActionBar(R.id.toolbarPhotoList);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadPhoto();
    }

    private void loadPhoto() {
        if (mPhotoUrl.toLowerCase().startsWith("http")) {
            try {
                mDownloadFileName = PhotoPicker.getInstance(getActivity()).getPhotoStorageFolderPath()
                        + File.separator + UUID.randomUUID().toString() + "." + DownloadFileUtil.getFileExtension(mPhotoUrl, "jpg");
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            mViewHolder.dataContainerViewPhotoView.switchToBusyView();
            DownloadManager.getInstance(getActivity()).download(mPhotoUrl, mDownloadFileName, new DownloaderCallback() {
                @Override
                public void onSuccess(String fileUrl) {
                    mViewHolder.dataContainerViewPhotoView.switchToDataView();
                    mPhotoUrl = DownloadFileUtil.getLocalFileUri(mDownloadFileName);
                    loadImage(mPhotoUrl);
                }

                @Override
                public void onFailure(String fileUrl, String errorMessage) {
                    mViewHolder.dataContainerViewPhotoView.switchToDataView();
                    Toast.makeText(getActivity(), getString(R.string.error_download_file_failed, fileUrl), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancel(String fileUrl) {
                    mViewHolder.dataContainerViewPhotoView.switchToDataView();
                }

                @Override
                public void onProgressUpdate(String fileUrl, long totalFileLength, long currentFileLength) {

                }
            });
        } else {
            loadImage(mPhotoUrl);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause(){
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

        public ViewHolder(View view) {
            dataContainerViewPhotoView = (DataContainerView) view.findViewById(R.id.dataContainerViewPhotoView);
            imageView = (PhotoView) view.findViewById(R.id.imageView);
        }
    }
}
