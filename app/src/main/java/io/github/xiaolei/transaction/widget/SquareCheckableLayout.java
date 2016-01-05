package io.github.xiaolei.transaction.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.FrameLayout;

import io.github.xiaolei.transaction.R;

/**
 * TODO: add comment
 */
public class SquareCheckableLayout extends FrameLayout implements Checkable {
    private static final int[] mCheckedStateSet = {android.R.attr.state_checked};
    private boolean mChecked;
    private ViewHolder mViewHolder;
    private View mCheckableView;

    public SquareCheckableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareCheckableLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SquareCheckableLayout(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    public void setChecked(boolean checked) {
        mChecked = checked;

        if (mCheckableView == null) {
            mCheckableView = View.inflate(getContext(), R.layout.view_checkable_layout, this);
            mViewHolder = new ViewHolder(mCheckableView);
        }

        //mViewHolder.checkBoxSelectIndicator.setVisibility(checked ? View.VISIBLE : View.GONE);
        //mViewHolder.checkBoxSelectIndicator.setChecked(checked);
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
        setChecked(!mChecked);
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, mCheckedStateSet);
        }
        return drawableState;
    }

    private class ViewHolder {
        public AnimationCheckBox checkBoxSelectIndicator;

        public ViewHolder(View view) {
            checkBoxSelectIndicator = (AnimationCheckBox) view.findViewById(R.id.checkBoxSelectIndicator);
        }
    }
}