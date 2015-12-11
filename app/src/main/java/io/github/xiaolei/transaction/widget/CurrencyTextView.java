package io.github.xiaolei.transaction.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.math.BigDecimal;

import io.github.xiaolei.enterpriselibrary.utility.CurrencyHelper;
import io.github.xiaolei.transaction.R;

/**
 * TODO: add comment
 */
public class CurrencyTextView extends RelativeLayout implements View.OnClickListener {
    protected static final String TAG = CurrencyTextView.class.getSimpleName();
    public static final int VIEW_INDEX_READ = 0;
    public static final int VIEW_INDEX_WRITE = 1;

    private ViewHolder mViewHolder;
    private int mIncomeTextColor;
    private int mExpenseTextColor;
    protected BigDecimal mPrice;
    protected String mCurrencyCode;
    protected boolean mIsModified = false;
    protected boolean mEnableEditMode = false;

    public CurrencyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize(context, attrs);
    }

    public CurrencyTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.initialize(context, attrs);
    }

    public CurrencyTextView(Context context) {
        super(context);
        this.initialize(context, null);
    }

    public void setIsModified(boolean modified) {
        mIsModified = modified;
    }

    public void enableEditMode(boolean enable) {
        mEnableEditMode = enable;
        if (enable) {
            mViewHolder.textViewCurrency.setOnClickListener(this);
        } else {
            mViewHolder.textViewCurrency.setOnClickListener(null);
        }
    }

    public boolean isModified() {
        return mIsModified;
    }

    public boolean isInEditMode() {
        return mViewHolder.viewFlipperPriceContainer.getDisplayedChild() == VIEW_INDEX_WRITE;
    }

    public void toggleEditMode(boolean enableEditMode) {
        if (enableEditMode) {
            mViewHolder.viewFlipperPriceContainer.setDisplayedChild(VIEW_INDEX_WRITE);
        } else {
            mViewHolder.viewFlipperPriceContainer.setDisplayedChild(VIEW_INDEX_READ);
        }
    }

    public void setPrice(BigDecimal price, String currencyCode) {
        if (price == null || TextUtils.isEmpty(currencyCode)) {
            return;
        }

        mPrice = price;
        mCurrencyCode = currencyCode;

        mViewHolder.textViewCurrency.setText(CurrencyHelper.formatCurrency(currencyCode, price));
        if (price.intValue() > 0) {
            mViewHolder.textViewCurrency.setTextColor(mIncomeTextColor);
        } else {
            mViewHolder.textViewCurrency.setTextColor(mExpenseTextColor);
        }

        mViewHolder.editTextPrice.setText(mPrice.toString());
        mViewHolder.textViewCurrencyCode.setText(currencyCode);

        mIsModified = false;
    }

    public void setPrice(int price, String currencyCode) {
        if (TextUtils.isEmpty(currencyCode)) {
            return;
        }

        setPrice(CurrencyHelper.castToBigDecimal(price), currencyCode);
    }

    public BigDecimal getPrice() {
        return mPrice;
    }

    public String getCurrencyCode() {
        return mCurrencyCode;
    }

    protected void initialize(Context context, AttributeSet attrs) {
        View view = View.inflate(context, R.layout.view_currency, this);
        mIncomeTextColor = getContext().getResources().getColor(R.color.incoming_text_color);
        mExpenseTextColor = getContext().getResources().getColor(R.color.expense_text_color);
        mViewHolder = new ViewHolder(view);
        mViewHolder.editTextPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mIsModified = true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textViewCurrency:
                if (mEnableEditMode) {
                    toggleEditMode(true);
                }
                break;
            default:
                break;
        }
    }

    private class ViewHolder {
        public ViewFlipper viewFlipperPriceContainer;
        public TextView textViewCurrency;
        public EditText editTextPrice;
        public TextView textViewCurrencyCode;

        public ViewHolder(View view) {
            viewFlipperPriceContainer = (ViewFlipper) view.findViewById(R.id.viewFlipperPriceContainer);
            textViewCurrency = (TextView) view.findViewById(R.id.textViewCurrency);
            editTextPrice = (EditText) view.findViewById(R.id.editTextPrice);
            textViewCurrencyCode = (TextView) view.findViewById(R.id.textViewCurrencyCode);
        }
    }
}