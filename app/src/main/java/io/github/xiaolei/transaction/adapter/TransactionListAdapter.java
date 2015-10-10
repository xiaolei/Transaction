package io.github.xiaolei.transaction.adapter;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.github.xiaolei.transaction.GlobalApplication;
import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.entity.Transaction;
import io.github.xiaolei.transaction.util.CurrencyHelper;
import io.github.xiaolei.transaction.util.DateTimeUtils;
import io.github.xiaolei.transaction.widget.CurrencyTextView;

/**
 * TODO: add comment
 */
public class TransactionListAdapter extends BaseAdapter implements IDataAdapter {
    private Context mContext;
    private List<Transaction> mTransactions;
    private LayoutInflater mInflater;
    private ViewHolder mViewHolder;

    public TransactionListAdapter(Context context, List<Transaction> transactions) {
        mContext = context;
        mTransactions = transactions != null ? transactions : new ArrayList<Transaction>();
        mInflater = LayoutInflater.from(context);
    }

    public void swapData(List<Transaction> transactions) {
        mTransactions.clear();
        mTransactions.addAll(transactions);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mTransactions.size();
    }

    @Override
    public Object getItem(int i) {
        return mTransactions.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mTransactions.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = mInflater.inflate(R.layout.layout_item_transaction, viewGroup, false);
            mViewHolder = new ViewHolder(view);
            view.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) view.getTag();
        }

        bindData(mTransactions.get(i));

        return view;
    }

    private void bindData(Transaction transaction) {
        if (mViewHolder == null || transaction == null) {
            return;
        }

        mViewHolder.textViewProductName.setText(transaction.getProduct().getName());
        mViewHolder.textViewCreationTime.setText(DateTimeUtils.formatDateTime(transaction.getCreationTime()));
        mViewHolder.textViewPrice.setPrice(CurrencyHelper.castToBigDecimal(transaction.getPrice()), transaction.getCurrencyCode());
        mViewHolder.checkedTextViewTransactionIcon.setText(transaction.getProduct().getName().substring(0, 1).toUpperCase());
    }

    @Override
    public void append(List data) {
        mTransactions.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public void swapDate(List data) {
        mTransactions.clear();
        mTransactions.addAll(data);
        notifyDataSetChanged();
    }

    private class ViewHolder {
        public TextView textViewProductName;
        public TextView textViewCreationTime;
        public CurrencyTextView textViewPrice;
        public CheckedTextView checkedTextViewTransactionIcon;

        public ViewHolder(View view) {
            textViewProductName = (TextView) view.findViewById(R.id.textViewProductName);
            textViewCreationTime = (TextView) view.findViewById(R.id.textViewCreationTime);
            textViewPrice = (CurrencyTextView) view.findViewById(R.id.textViewPrice);
            checkedTextViewTransactionIcon = (CheckedTextView) view.findViewById(R.id.checkedTextViewTransactionIcon);
        }
    }
}
