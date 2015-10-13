package io.github.xiaolei.transaction.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import io.github.xiaolei.enterpriselibrary.utility.CurrencyHelper;
import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.viewmodel.AmountInfo;

/**
 * TODO: add comment
 */
public class AmountView extends RelativeLayout {
    protected static final String TAG = AmountView.class.getSimpleName();
    private ViewHolder mViewHolder;
    private AmountInfo mAmountInfo;

    public AmountView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize(context, attrs);
    }

    public AmountView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.initialize(context, attrs);
    }

    public AmountView(Context context) {
        super(context);
        this.initialize(context, null);
    }

    protected void initialize(Context context, AttributeSet attrs) {
        View view = View.inflate(context, R.layout.view_asset_summary, this);
        mViewHolder = new ViewHolder(view);
    }

    public void bindData(AmountInfo amountInfo) {
        if (amountInfo == null) {
            return;
        }

        mViewHolder.textViewAmount.setText(CurrencyHelper.formatCurrency(amountInfo.currencyCode, amountInfo.amount));
        mViewHolder.textViewTotalExpense.setText(CurrencyHelper.formatCurrency(amountInfo.currencyCode, amountInfo.totalExpense));
        mViewHolder.textViewTotalIncome.setText(CurrencyHelper.formatCurrency(amountInfo.currencyCode, amountInfo.totalIncome));
    }

    public void setBusyIndicatorVisibility(boolean visible) {
        if (visible) {
            mViewHolder.dataContainerViewAssetSummary.switchToBusyView();
        } else {
            mViewHolder.dataContainerViewAssetSummary.switchToDataView();
        }
    }

    private class ViewHolder {
        private DataContainerView dataContainerViewAssetSummary;
        public TextView textViewAmount;
        public TextView textViewTotalIncome;
        public TextView textViewTotalExpense;

        public ViewHolder(View view) {
            dataContainerViewAssetSummary = (DataContainerView) view.findViewById(R.id.dataContainerViewAmountSummary);
            textViewAmount = (TextView) view.findViewById(R.id.textViewAmount);
            textViewTotalIncome = (TextView) view.findViewById(R.id.textViewTotalIncome);
            textViewTotalExpense = (TextView) view.findViewById(R.id.textViewTotalExpense);
        }
    }
}