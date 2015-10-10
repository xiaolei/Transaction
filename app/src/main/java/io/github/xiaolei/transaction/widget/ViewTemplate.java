package io.github.xiaolei.transaction.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import io.github.xiaolei.transaction.R;

/**
 * TODO: add comment
 */
public class ViewTemplate extends RelativeLayout {
    protected static final String TAG = ViewTemplate.class.getSimpleName();
    private ViewHolder mViewHolder;
    private String mSampleAttributeValue;


    public ViewTemplate(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize(context, attrs);
    }

    public ViewTemplate(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.initialize(context, attrs);
    }

    public ViewTemplate(Context context) {
        super(context);
        this.initialize(context, null);
    }

    protected void initialize(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs,
                    R.styleable.SearchBoxView, 0, 0);
            try {
                mSampleAttributeValue = typedArray
                        .getString(R.styleable.SearchBoxView_keywords);
            } finally {
                typedArray.recycle();
            }
        }

        View view = View.inflate(context, R.layout.view_search_box, this);
        mViewHolder = new ViewHolder(view);
    }

    private class ViewHolder {
        public ViewHolder(View view) {

        }
    }
}