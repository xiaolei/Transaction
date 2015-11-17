package io.github.xiaolei.transaction.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.RelativeLayout;

import io.github.xiaolei.enterpriselibrary.logging.Logger;

/**
 * TODO: add comment
 */
public class CheckableRelativeLayout extends RelativeLayout  implements Checkable {
    private static final int[] mCheckedStateSet = {android.R.attr.state_checked};
    private static final String TAG = CheckableRelativeLayout.class.getSimpleName();
    private boolean mChecked;

    public CheckableRelativeLayout(Context context) {
        super(context);
    }

    public CheckableRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckableRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setChecked(boolean checked) {
        mChecked = checked;
        Logger.d(TAG, "checked: " + mChecked);
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
}
