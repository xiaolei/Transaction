package io.github.xiaolei.enterpriselibrary.ui;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.LayoutRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import io.github.xiaolei.enterpriselibrary.R;

/**
 * 气球弹出提示框
 */
public class Tooltip {
    protected WindowManager mWindowManager;
    protected Context mContext;
    protected PopupWindow mWindow;
    protected ViewHolder mViewHolder;

    protected View mTooltipView;
    protected View mAnchorView;
    protected View mContentView;

    protected LayoutInflater mLayoutInflater;
    protected ShowListener mShowListener;

    protected Tooltip(Context context) {
        mContext = context;
        mWindow = new PopupWindow(context);
        mWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (mShowListener != null) {
                    mShowListener.onDismiss(mAnchorView);
                }
            }
        });

        mWindowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);

        mLayoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mTooltipView = mLayoutInflater.inflate(R.layout.tooltip, null);
        mWindow.setContentView(mTooltipView);

        mViewHolder = new ViewHolder(mTooltipView);
    }

    public Tooltip(Context context, @LayoutRes int contentLayoutResourceId) {
        this(context);

        mViewHolder.tooltipContentContainer.removeAllViews();
        mContentView = mLayoutInflater.inflate(contentLayoutResourceId, mViewHolder.tooltipContentContainer);
    }

    public View getContentView() {
        return mContentView;
    }

    public <T extends View> T getAnchorView(Class<T> viewType) {
        if (mAnchorView == null || !viewType.isInstance(mAnchorView)) {
            return null;
        } else {
            return viewType.cast(mAnchorView);
        }
    }

    public void show(View anchor) {
        preShow();

        mAnchorView = anchor;

        int[] location = new int[2];

        anchor.getLocationOnScreen(location);

        Rect anchorRect = new Rect(location[0], location[1], location[0]
                + anchor.getWidth(), location[1] + anchor.getHeight());

        mTooltipView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        int popupHeight = mTooltipView.getMeasuredHeight();
        int popupWidth = mTooltipView.getMeasuredWidth();

        int anchorWidth = anchor.getMeasuredWidth();
        int anchorHeight = anchor.getMeasuredHeight();

        final int screenWidth = mWindowManager.getDefaultDisplay().getWidth();
        final int screenHeight = mWindowManager.getDefaultDisplay().getHeight();

        int arrowHeight = mTooltipView.getContext().getResources().getDimensionPixelSize(R.dimen.tooltip_arrow_height);

        int xPos = anchorRect.left;
        int yPos = anchorRect.top - popupHeight - arrowHeight;

        if (popupWidth > anchorWidth) {
            xPos = anchorRect.left - Math.abs(popupWidth - anchorWidth) / 2;
        } else {
            xPos = anchorRect.left + Math.abs(popupWidth - anchorWidth) / 2;
        }

        if (xPos < 0) {
            xPos = anchorRect.left;
        }

        mWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, yPos);

        if (mShowListener != null) {
            mShowListener.onShow(mAnchorView);
        }
    }

    public boolean isShowing() {
        return mWindow != null && mWindow.isShowing();
    }

    protected void preShow() {
        if (mTooltipView == null)
            throw new IllegalStateException("view undefined");


        if (mShowListener != null) {
            mShowListener.onPreShow(mAnchorView);
        }

        mWindow.setBackgroundDrawable(new BitmapDrawable());
        mWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        mWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        mWindow.setTouchable(true);
        mWindow.setFocusable(true);
        mWindow.setOutsideTouchable(true);

        mWindow.setContentView(mTooltipView);
    }

    public void setOnDismissListener(PopupWindow.OnDismissListener listener) {
        mWindow.setOnDismissListener(listener);
    }

    public void dismiss() {
        mWindow.dismiss();
    }

    public void setShowListener(ShowListener showListener) {
        this.mShowListener = showListener;
    }

    public interface ShowListener {
        void onPreShow(View anchorView);

        void onDismiss(View anchorView);

        void onShow(View anchorView);
    }

    private class ViewHolder {
        public LinearLayout tooltipContentContainer;
        public ImageView upImageView;
        public ImageView downImageView;

        public ViewHolder(View view) {
            tooltipContentContainer = (LinearLayout) view.findViewById(R.id.tooltipContentContainer);
            upImageView = (ImageView) mTooltipView.findViewById(R.id.arrow_up);
            downImageView = (ImageView) mTooltipView.findViewById(R.id.arrow_down);
        }
    }
}
