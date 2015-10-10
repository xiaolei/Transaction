package io.github.xiaolei.transaction.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * TODO: add comment
 */
public class SquareImageView extends ImageView {
    public SquareImageView(Context context) {
        super(context);
        initialize();
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    protected void initialize(){

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
