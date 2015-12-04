package io.github.xiaolei.transaction.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.List;

import io.github.xiaolei.enterpriselibrary.utility.CurrencyHelper;
import io.github.xiaolei.enterpriselibrary.utility.DateTimeUtils;
import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.entity.Transaction;
import io.github.xiaolei.transaction.listener.OnLoadMoreListener;
import io.github.xiaolei.transaction.viewholder.GenericRecyclerViewHolder;
import io.github.xiaolei.transaction.widget.CurrencyTextView;

/**
 * Transaction list adapter.
 */
public class TransactionListNewAdapter extends NewGenericRecyclerViewAdapter<Transaction, TransactionListNewAdapter.ViewHolder> {

    public TransactionListNewAdapter(RecyclerView recyclerView, List<Transaction> transactions,
                                     OnLoadMoreListener<Transaction> onLoadMoreListener) {
        super(recyclerView, transactions, onLoadMoreListener);
    }

    @Override
    protected ViewHolder createDataItemViewHolder(ViewGroup parent, int viewType) {
        View view = getLayoutInflater().inflate(R.layout.layout_item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        bindData(holder, mItems.get(position));
    }

    @Override
    public long getItemId(int i) {
        return mItems.get(i).getId();
    }

    private void bindData(ViewHolder holder, Transaction transaction) {
        if (holder == null || transaction == null) {
            return;
        }

        int count = transaction.getProductCount();
        String name = count == 1 ? transaction.getProduct().getName() :
                String.format("%s × %d", transaction.getProduct().getName(), count);

        holder.textViewProductName.setText(name);
        holder.textViewCreationTime.setText(DateTimeUtils.formatDateTime(transaction.getCreationTime()));
        holder.textViewPrice.setPrice(CurrencyHelper.castToBigDecimal(transaction.getPrice()), transaction.getCurrencyCode());
        holder.checkedTextViewTransactionIcon.setTag(transaction);
        holder.checkedTextViewTransactionIcon.setChecked(transaction.checked);
        if (!transaction.checked) {
            holder.checkedTextViewTransactionIcon.setText(transaction.getProduct().getName().substring(0, 1).toUpperCase());
        } else {
            holder.checkedTextViewTransactionIcon.setText("");
        }
    }

    public List<Transaction> getCheckedItems() {
        ArrayList<Transaction> result = new ArrayList<Transaction>();
        for (Transaction transaction : mItems) {
            if (transaction.checked) {
                result.add(transaction);
            }
        }

        return result;
    }

    public void uncheckAll() {
        ArrayList<Transaction> result = new ArrayList<Transaction>();
        for (Transaction transaction : mItems) {
            if (transaction.checked) {
                transaction.checked = false;
            }
        }

        notifyDataSetChanged();
    }

    public class ViewHolder extends GenericRecyclerViewHolder {
        public ViewFlipper viewFlipperTransactionItem;
        public TextView textViewProductName;
        public TextView textViewCreationTime;
        public CurrencyTextView textViewPrice;
        public CheckedTextView checkedTextViewTransactionIcon;

        public ViewHolder(View view) {
            super(view);

            viewFlipperTransactionItem = (ViewFlipper) view.findViewById(R.id.viewFlipperTransactionItem);
            textViewProductName = (TextView) view.findViewById(R.id.textViewProductName);
            textViewCreationTime = (TextView) view.findViewById(R.id.textViewCreationTime);
            textViewPrice = (CurrencyTextView) view.findViewById(R.id.textViewPrice);
            checkedTextViewTransactionIcon = (CheckedTextView) view.findViewById(R.id.checkedTextViewTransactionIcon);
        }

        @Override
        public void switchToLoadingView() {
            viewFlipperTransactionItem.setDisplayedChild(1);
        }

        @Override
        public void switchToDataView() {
            viewFlipperTransactionItem.setDisplayedChild(0);
        }
    }
}
