package io.github.xiaolei.transaction.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.util.DateTimeUtils;
import io.github.xiaolei.transaction.viewmodel.TransactionFilterType;
import io.github.xiaolei.transaction.viewmodel.TransactionNavigatorItem;

/**
 * TODO: add comment
 */
public class TransactionNavigatorAdapter extends BaseAdapter {
    private String DATE_RANGE_TEXT_FORMAT = "%s ~ %s";
    private LayoutInflater mInflater;
    private List<TransactionNavigatorItem> mItems;
    private Date mTransactionDate;
    private int mSelectedTransactionFilterType = TransactionFilterType.TODAY;
    private String mCustomTitle;
    private ViewHolder mViewHolder;

    public TransactionNavigatorAdapter(Context context, List<TransactionNavigatorItem> items,
                                       Date transactionDate) {
        mInflater = LayoutInflater.from(context);
        mItems = items;
        mTransactionDate = transactionDate;
    }

    public void setSelectedItem(int transactionFilterType) {
        mSelectedTransactionFilterType = transactionFilterType;
        notifyDataSetChanged();
    }

    public void setCustomTitle(String title) {
        if (TextUtils.isEmpty(title)) {
            return;
        }

        mCustomTitle = title;
        mSelectedTransactionFilterType = -1;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int i) {
        return mItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mItems.get(i).transactionFilterType;
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = mInflater.inflate(R.layout.toolbar_spinner_item_dropdown, viewGroup, false);
            mViewHolder = new ViewHolder(view);
            view.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) view.getTag();
        }

        bindData(mItems.get(position));

        return view;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null || !view.getTag().toString().equals("NON_DROPDOWN")) {
            view = mInflater.inflate(R.layout.
                    toolbar_spinner_item_actionbar, viewGroup, false);
            view.setTag("NON_DROPDOWN");
        }

        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        if (mSelectedTransactionFilterType >= 0) {
            textView.setText(mItems.get(i).textResourceId);
        } else {
            textView.setText(mCustomTitle);
        }

        return view;
    }

    private void bindData(TransactionNavigatorItem transactionNavigatorItem) {
        if (mViewHolder == null) {
            return;
        }

        mViewHolder.textViewText.setText(transactionNavigatorItem.textResourceId);
        mViewHolder.textViewDateRange.setText(getDateRange(mTransactionDate, transactionNavigatorItem.transactionFilterType));

        if (transactionNavigatorItem.transactionFilterType != mSelectedTransactionFilterType) {
            mViewHolder.textViewText.setTypeface(null, Typeface.NORMAL);
        } else {
            mViewHolder.textViewText.setTypeface(null, Typeface.BOLD);
        }
    }

    private String getDateRange(Date date, int transactionFilterType) {
        String result = "";
        switch (transactionFilterType) {
            case TransactionFilterType.TODAY:
                result = DateTimeUtils.formatShortDate(date);
                break;
            case TransactionFilterType.THIS_WEEK:
                result = String.format(DATE_RANGE_TEXT_FORMAT,
                        DateTimeUtils.formatShortDate(DateTimeUtils.getStartDayOfWeek(date)),
                        DateTimeUtils.formatShortDate(DateTimeUtils.getEndDayOfWeek(date)));
                break;
            case TransactionFilterType.THIS_MONTH:
                result = String.format(DATE_RANGE_TEXT_FORMAT,
                        DateTimeUtils.formatShortDate(DateTimeUtils.getStartDayOfMonth(date)),
                        DateTimeUtils.formatShortDate(DateTimeUtils.getEndDayOfMonth(date)));
                break;
            case TransactionFilterType.THIS_YEAR:
                result = String.format(DATE_RANGE_TEXT_FORMAT,
                        DateTimeUtils.formatShortDate(DateTimeUtils.getStartDayOfYear(date)),
                        DateTimeUtils.formatShortDate(DateTimeUtils.getEndDayOfYear(date)));
                break;
            default:
                break;
        }

        return result;
    }

    private class ViewHolder {
        public TextView textViewText;
        public TextView textViewDateRange;

        public ViewHolder(View view) {
            textViewText = (TextView) view.findViewById(R.id.textViewText);
            textViewDateRange = (TextView) view.findViewById(R.id.textViewDateRange);
        }
    }
}
