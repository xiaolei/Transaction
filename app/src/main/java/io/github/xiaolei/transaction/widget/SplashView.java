package io.github.xiaolei.transaction.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import io.github.xiaolei.transaction.R;

/**
 * TODO: add comment
 */
public class SplashView extends RelativeLayout {
    protected static final String TAG = SplashView.class.getSimpleName();
    private ViewHolder mViewHolder;

    public SplashView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize(context, attrs);
    }

    public SplashView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.initialize(context, attrs);
    }

    public SplashView(Context context) {
        super(context);
        this.initialize(context, null);
    }

    protected void initialize(Context context, AttributeSet attrs) {
        View view = View.inflate(context, R.layout.view_splash, this);
        mViewHolder = new ViewHolder(view);
    }

    private class ViewHolder {

        public ViewHolder(View view) {

        }
    }
}