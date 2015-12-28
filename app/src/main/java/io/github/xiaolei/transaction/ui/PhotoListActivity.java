package io.github.xiaolei.transaction.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;

import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.adapter.PhotoListAdapter;
import io.github.xiaolei.transaction.adapter.PhotoViewFragmentPagerAdapter;
import io.github.xiaolei.transaction.entity.Photo;

/**
 * TODO: add comment
 */
public class PhotoListActivity extends BaseToolbarActivity {
    public static final String ARG_PHOTO_URLS = "arg_photo_urls";
    public static final String ARG_CURRENT_POSITION = "arg_current_position";

    private ViewHolder mViewHolder;
    private PhotoViewFragmentPagerAdapter mAdapter;
    private ArrayList<Photo> mPhotoList;
    private int mCurrentPosition = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_photo_list;
    }

    @Override
    protected void initialize() {
        mViewHolder = new ViewHolder(this);

        handleIntent(getIntent());
    }

    @Override
    protected String getActionBarTitle() {
        return getString(R.string.photo_list);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        handleIntent(intent);
    }

    public void handleIntent(Intent intent) {
        mPhotoList = (ArrayList<Photo>) intent.getSerializableExtra(ARG_PHOTO_URLS);
        mCurrentPosition = intent.getIntExtra(ARG_CURRENT_POSITION, 0);

        if(mPhotoList == null){
            return;
        }

        mAdapter = new PhotoViewFragmentPagerAdapter(getSupportFragmentManager(), mPhotoList);
        mViewHolder.viewPagerPhotoList.setAdapter(mAdapter);

        if(mCurrentPosition >= 0 && mCurrentPosition < mPhotoList.size()) {
            mViewHolder.viewPagerPhotoList.setCurrentItem(mCurrentPosition, false);
        }
    }

    private class ViewHolder {
        public ViewPager viewPagerPhotoList;

        public ViewHolder(Activity activity) {
            viewPagerPhotoList = (ViewPager) activity.findViewById(R.id.viewPagerPhotoList);
        }
    }
}
