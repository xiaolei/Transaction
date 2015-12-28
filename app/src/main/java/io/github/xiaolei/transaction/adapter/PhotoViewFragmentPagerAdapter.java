package io.github.xiaolei.transaction.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import io.github.xiaolei.transaction.entity.Photo;
import io.github.xiaolei.transaction.ui.PhotoViewFragment;

/**
 * TODO: add comment
 */
public class PhotoViewFragmentPagerAdapter extends FragmentPagerAdapter {
    private List<Photo> mPhotos = new ArrayList<Photo>();

    public PhotoViewFragmentPagerAdapter(FragmentManager fm, List<Photo> photoList) {
        super(fm);
        mPhotos = photoList;
    }

    @Override
    public Fragment getItem(int position) {
        String photoUrl = mPhotos.get(position).getUrl();
        PhotoViewFragment fragment = PhotoViewFragment.newInstance(photoUrl);

        return fragment;
    }

    @Override
    public int getCount() {
        return mPhotos.size();
    }
}
