package io.github.xiaolei.enterpriselibrary.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * TODO: add comment
 */
public class FragmentViewPager extends ViewPager {
    protected boolean mPagingEnabled = false;

    public FragmentViewPager(Context context) {
        super(context);
        initialize();
    }

    public FragmentViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    protected void initialize() {
    }

    public void setPagingEnabled(boolean enable) {
        mPagingEnabled = enable;
    }

    @Override
    public void setCurrentItem(int item) {
        if (mPagingEnabled) {
            super.setCurrentItem(item);
        } else {
            super.setCurrentItem(item, false);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.mPagingEnabled) {
            return super.onTouchEvent(event);
        }

        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mPagingEnabled) {
            return super.onInterceptTouchEvent(ev);
        } else {
            return false;
        }
    }
}
