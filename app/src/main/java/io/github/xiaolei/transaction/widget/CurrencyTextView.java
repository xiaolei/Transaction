package io.github.xiaolei.transaction.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.math.BigDecimal;

import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.util.CurrencyHelper;

/**
 * TODO: add comment
 */
public class CurrencyTextView extends RelativeLayout {
    protected static final String TAG = CurrencyTextView.class.getSimpleName();
    private ViewHolder mViewHolder;
    private int mIncomeTextColor;
    private int mExpenseTextColor;

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

    public void setPrice(BigDecimal price, String currencyCode) {
        if (price == null || TextUtils.isEmpty(currencyCode)) {
            return;
        }

        mViewHolder.textViewCurrency.setText(CurrencyHelper.formatCurrency(currencyCode, price));
        if (price.intValue() > 0) {
            mViewHolder.textViewCurrency.setTextColor(mIncomeTextColor);
        } else {
            mViewHolder.textViewCurrency.setTextColor(mExpenseTextColor);
        }
    }

    protected void initialize(Context context, AttributeSet attrs) {
        View view = View.inflate(context, R.layout.view_currency, this);
        mIncomeTextColor = getContext().getResources().getColor(R.color.incoming_text_color);
        mExpenseTextColor = getContext().getResources().getColor(R.color.expense_text_color);
        mViewHolder = new ViewHolder(view);
    }

    private class ViewHolder {
        public TextView textViewCurrency;

        public ViewHolder(View view) {
            textViewCurrency = (TextView) view.findViewById(R.id.textViewCurrency);
        }
    }
}