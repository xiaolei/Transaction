package io.github.xiaolei.transaction.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.util.HashMap;

import io.github.xiaolei.transaction.R;

/**
 * TODO: add comment
 */
public class DataContainerView extends ViewFlipper {
    public static final String TAG = DataContainerView.class.getSimpleName();
    public static final int VIEW_INDEX_DATA_LAYOUT = 0;
    public static final int VIEW_INDEX_BUSY_LAYOUT = 1;
    public static final int VIEW_INDEX_RETRY_LAYOUT = 2;
    public static final int VIEW_INDEX_EMPTY_LAYOUT = 3;

    protected int mBusyLayoutResourceId;
    protected int mDataLayoutResourceId;
    protected int mRetryLayoutResourceId;
    protected int mEmptyLayoutResourceId;

    protected ViewHolder mViewHolder;
    protected OnClickListener mOnRetryViewClickListener;

    protected HashMap<Integer, Integer> mLayoutViewIndexMapping;

    public DataContainerView(Context context) {
        super(context);
        this.initialize(context, null);
    }

    public DataContainerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize(context, attrs);
    }

    protected void initialize(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs,
                    R.styleable.DataContainerView, 0, 0);
            try {
                mBusyLayoutResourceId = typedArray
                        .getResourceId(R.styleable.DataContainerView_busyLayout, R.layout.layout_loading);
                mRetryLayoutResourceId = typedArray
                        .getResourceId(R.styleable.DataContainerView_retryLayout, R.layout.layout_retry);
                mDataLayoutResourceId = typedArray
                        .getResourceId(R.styleable.DataContainerView_dataLayout, -1);
                mEmptyLayoutResourceId = typedArray
                        .getResourceId(R.styleable.DataContainerView_emptyLayout, R.layout.layout_no_result);
            } finally {
                typedArray.recycle();
            }
        }

        if (mDataLayoutResourceId <= 0) {
            throw new IllegalArgumentException("dataLayout is required. You must set a layout resource id to this attribute.");
        }

        mLayoutViewIndexMapping = new HashMap<>();

        mViewHolder = new ViewHolder();
        mViewHolder.dataView = View.inflate(context, mDataLayoutResourceId, this);
        mViewHolder.loadingView = View.inflate(context, mBusyLayoutResourceId, this);
        mViewHolder.retryView = View.inflate(context, mRetryLayoutResourceId, this);
        mViewHolder.emptyView = View.inflate(context, mEmptyLayoutResourceId, this);
        mViewHolder.retryView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onRetryViewClick(view);
            }
        });

        mViewHolder.findViews(this);
    }

    public View getLoadingView() {
        return mViewHolder.loadingView;
    }

    public View getRetryView() {
        return mViewHolder.retryView;
    }

    public View getDataView() {
        return mViewHolder.dataView;
    }

    public View getEmptyView() {
        return mViewHolder.emptyView;
    }

    public void setEmptyViewDisplayText(String text) {
        if(TextUtils.isEmpty(text)){
            return;
        }
        mViewHolder.textViewNoResult.setText(text);
    }

    /**
     * Switches to busy view.
     */
    public void switchToBusyView() {
        switchView(VIEW_INDEX_BUSY_LAYOUT);
    }

    /**
     * Switches to data view to display data.
     */
    public void switchToDataView() {
        switchView(VIEW_INDEX_DATA_LAYOUT);
    }

    /**
     * Switches to retry view.
     */
    public void switchToRetryView() {
        switchView(VIEW_INDEX_RETRY_LAYOUT);
    }

    public void switchToEmptyView() {
        switchView(VIEW_INDEX_EMPTY_LAYOUT);
    }

    public void switchToEmptyView(String displayText){
        setEmptyViewDisplayText(displayText);
        switchToEmptyView();
    }

    public void setOnRetryViewClickListener(OnClickListener onRetryViewClickListener) {
        mOnRetryViewClickListener = onRetryViewClickListener;
    }

    public int addLayout(int layoutResourceId) {
        int viewIndex = -1;
        if (mLayoutViewIndexMapping.containsKey(layoutResourceId)) {
            viewIndex = mLayoutViewIndexMapping.get(layoutResourceId);
        } else {
            View.inflate(getContext(), layoutResourceId, this);
            viewIndex = getChildCount() - 1;
        }

        return viewIndex;
    }

    public synchronized void switchToView(int layoutResourceId) {
        int viewIndex = -1;
        if (mLayoutViewIndexMapping.containsKey(layoutResourceId)) {
            viewIndex = mLayoutViewIndexMapping.get(layoutResourceId);
        } else {
            addLayout(layoutResourceId);
        }

        switchView(viewIndex);
    }

    protected void onRetryViewClick(View view) {
        if (mOnRetryViewClickListener != null) {
            mOnRetryViewClickListener.onClick(view);
        }
    }

    protected void switchView(int viewIndex) {
        setDisplayedChild(viewIndex);
    }

    private class ViewHolder {
        public View loadingView;
        public View retryView;
        public View dataView;
        public View emptyView;
        public TextView textViewNoResult;

        public ViewHolder() {

        }

        public void findViews(View view) {
            textViewNoResult = (TextView) view.findViewById(R.id.textViewNoResult);
        }
    }
}
