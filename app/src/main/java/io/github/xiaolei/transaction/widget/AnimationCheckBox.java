package io.github.xiaolei.transaction.widget;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.CheckBox;

/**
 * TODO: add comment
 */
public class AnimationCheckBox extends CheckBox {
    protected AnimatorSet mCheckedStateAnimatorSet;
    protected AnimatorSet mUncheckedStateAnimatorSet;

    public AnimationCheckBox(Context context) {
        super(context);
        initialize(context);
    }

    public AnimationCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public AnimationCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    protected void initialize(Context context) {
        ObjectAnimator scaleUp = ObjectAnimator.ofPropertyValuesHolder(this,
                PropertyValuesHolder.ofFloat("scaleX", 1.1f),
                PropertyValuesHolder.ofFloat("scaleY", 1.1f));

        scaleUp.setDuration(150);
        scaleUp.setInterpolator(new DecelerateInterpolator());

        ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(this,
                PropertyValuesHolder.ofFloat("scaleX", 1.0f),
                PropertyValuesHolder.ofFloat("scaleY", 1.0f));

        scaleDown.setDuration(150);
        scaleDown.setInterpolator(new AccelerateInterpolator());

        mCheckedStateAnimatorSet = new AnimatorSet();
        mCheckedStateAnimatorSet.playSequentially(scaleUp, scaleDown);


        ObjectAnimator fadeOut = ObjectAnimator.ofPropertyValuesHolder(this,
                PropertyValuesHolder.ofFloat("alpha", 0f));
        fadeOut.setDuration(200);
        mUncheckedStateAnimatorSet = new AnimatorSet();
        mUncheckedStateAnimatorSet.playTogether(fadeOut);
    }

    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);

        if (mCheckedStateAnimatorSet != null) {
            if (checked) {
                if(mUncheckedStateAnimatorSet.isRunning()){
                    mUncheckedStateAnimatorSet.cancel();
                }

                if (mCheckedStateAnimatorSet.isRunning()) {
                    return;
                }

                mCheckedStateAnimatorSet.start();
            } else {
                if (mCheckedStateAnimatorSet.isRunning()) {
                    mCheckedStateAnimatorSet.cancel();
                }

                if(mUncheckedStateAnimatorSet.isRunning()){
                    return;
                }

                mUncheckedStateAnimatorSet.start();
            }
        }
    }
}
