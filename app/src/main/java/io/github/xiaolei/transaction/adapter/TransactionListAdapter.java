package io.github.xiaolei.transaction.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import io.github.xiaolei.enterpriselibrary.utility.CurrencyHelper;
import io.github.xiaolei.enterpriselibrary.utility.DateTimeUtils;
import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.entity.Transaction;
import io.github.xiaolei.transaction.listener.OnCheckedStateChangedListener;
import io.github.xiaolei.transaction.widget.CurrencyTextView;

/**
 * Transaction list adapter.
 */
public class TransactionListAdapter extends BaseAdapter implements IDataAdapter<Transaction>, View.OnClickListener {
    private Context mContext;
    private List<Transaction> mTransactions;
    private LayoutInflater mInflater;
    private ViewHolder mViewHolder;
    private OnCheckedStateChangedListener<CheckedTextView> mOnItemCheckedStateChangedListener;

    public TransactionListAdapter(Context context, List<Transaction> transactions) {
        mContext = context;
        mTransactions = transactions != null ? transactions : new ArrayList<Transaction>();
        mInflater = LayoutInflater.from(context);
    }

    public void setOnItemCheckedStateChanged(OnCheckedStateChangedListener<CheckedTextView> listener){
        mOnItemCheckedStateChangedListener = listener;
    }

    protected void onItemCheckedStateChanged(CheckedTextView sender, boolean checked){
        if(mOnItemCheckedStateChangedListener != null){
            mOnItemCheckedStateChangedListener.onCheckedStateChanged(sender, checked);
        }
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

        int count = transaction.getProductCount();
        String name = count == 1 ? transaction.getProduct().getName() :
                String.format("%s × %d", transaction.getProduct().getName(), count);

        mViewHolder.textViewProductName.setText(name);
        mViewHolder.textViewCreationTime.setText(DateTimeUtils.formatDateTime(transaction.getCreationTime()));
        mViewHolder.textViewPrice.setPrice(CurrencyHelper.castToBigDecimal(transaction.getPrice()), transaction.getCurrencyCode());
        mViewHolder.checkedTextViewTransactionIcon.setTag(transaction);
        mViewHolder.checkedTextViewTransactionIcon.setOnClickListener(this);
        mViewHolder.checkedTextViewTransactionIcon.setChecked(transaction.checked);
        if (!transaction.checked) {
            mViewHolder.checkedTextViewTransactionIcon.setText(transaction.getProduct().getName().substring(0, 1).toUpperCase());
        } else {
            mViewHolder.checkedTextViewTransactionIcon.setText("");
        }
    }

    public List<Transaction> getCheckedItems() {
        ArrayList<Transaction> result = new ArrayList<Transaction>();
        for (Transaction transaction : mTransactions) {
            if (transaction.checked) {
                result.add(transaction);
            }
        }

        return result;
    }

    @Override
    public void append(List<Transaction> data) {
        mTransactions.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public void swap(List<Transaction> data) {
        mTransactions.clear();
        mTransactions.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        Transaction currentTransaction = (Transaction) v.getTag();
        currentTransaction.checked = !currentTransaction.checked;

        CheckedTextView checkedTextView = (CheckedTextView) v;
        checkedTextView.setChecked(currentTransaction.checked);

        notifyDataSetChanged();

        onItemCheckedStateChanged(checkedTextView, currentTransaction.checked);
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
