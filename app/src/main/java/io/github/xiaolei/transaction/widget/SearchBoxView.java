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
public class SearchBoxView extends RelativeLayout {
    protected static final String TAG = SearchBoxView.class.getSimpleName();
    private ViewHolder mViewHolder;
    private String mKeywords;
    private String mHintText;
    private boolean mPerformSearchDuringTyping;
    private OnSearchListener mOnSearchListener;
    private int mMinCharacters = 1;
    private boolean mEnableSearch = true;

    public SearchBoxView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize(context, attrs);
    }

    public SearchBoxView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.initialize(context, attrs);
    }

    public SearchBoxView(Context context) {
        super(context);
        this.initialize(context, null);
    }

    public void setOnSearchListener(OnSearchListener listener) {
        mOnSearchListener = listener;
    }

    protected void onSearch(String keywords) {
        if (mOnSearchListener != null && mEnableSearch) {
            mOnSearchListener.onSearch(keywords);
        }
    }

    public void reset(String keywords) {
        mEnableSearch = false;
        try {
            mViewHolder.editTextKeywords.setText(keywords);
        } finally {
            mEnableSearch = true;
        }
    }

    protected void initialize(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs,
                    R.styleable.SearchBoxView, 0, 0);
            try {
                mKeywords = typedArray
                        .getString(R.styleable.SearchBoxView_keywords);
                mHintText = typedArray
                        .getString(R.styleable.SearchBoxView_hintText);
                mPerformSearchDuringTyping = typedArray.getBoolean(R.styleable.SearchBoxView_performSearchDuringTyping, true);
                mKeywords = !TextUtils.isEmpty(mKeywords) ? mKeywords : "";
                mHintText = !TextUtils.isEmpty(mHintText) ? mHintText : "";
            } finally {
                typedArray.recycle();
            }
        }

        View view = View.inflate(context, R.layout.view_search_box, this);
        mViewHolder = new ViewHolder(view);
        mViewHolder.editTextKeywords.setText(mKeywords);
        mViewHolder.editTextKeywords.setHint(mHintText);
        mViewHolder.editTextKeywords.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String keywords = editable.toString();
                mViewHolder.imageViewDeleteSearchKeywords.setVisibility(keywords.length() > 0 ? View.VISIBLE : View.GONE);
                if (TextUtils.isEmpty(keywords) || keywords.length() >= mMinCharacters) {
                    onSearch(keywords);
                }
            }
        });
        mViewHolder.imageViewDeleteSearchKeywords.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewHolder.editTextKeywords.setText("");
            }
        });
    }

    public String getKeywords() {
        return mViewHolder.editTextKeywords.getText().toString();
    }

    public void setKeywords(String keywords) {
        String currentKeywords = getKeywords();
        if (!TextUtils.equals(keywords, currentKeywords)) {
            mViewHolder.editTextKeywords.setText(keywords);
        }
    }

    private class ViewHolder {
        public EditText editTextKeywords;
        public ImageView imageViewDeleteSearchKeywords;

        public ViewHolder(View view) {
            editTextKeywords = (EditText) view.findViewById(R.id.editTextSearchKeywords);
            imageViewDeleteSearchKeywords = (ImageView) view.findViewById(R.id.imageViewDeleteSearchKeywords);
        }
    }

    public interface OnSearchListener {
        void onSearch(String keywords);
    }
}