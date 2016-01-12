package io.github.xiaolei.transaction.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.github.xiaolei.enterpriselibrary.utility.CurrencyHelper;
import io.github.xiaolei.enterpriselibrary.utility.DateTimeUtils;
import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.entity.Transaction;
import io.github.xiaolei.transaction.listener.OnLoadMoreListener;
import io.github.xiaolei.transaction.util.ImageLoader;
import io.github.xiaolei.transaction.viewholder.GenericRecyclerViewHolder;
import io.github.xiaolei.transaction.widget.CurrencyTextView;

/**
 * Transaction list adapter.
 */
public class TransactionListRecyclerViewAdapter extends EndlessGenericRecyclerViewAdapter<Transaction, TransactionListRecyclerViewAdapter.ViewHolder> {
    protected GenericRecyclerViewAdapter.OnRecyclerViewItemLongClickListener mOnLongClickListener;
    protected GenericRecyclerViewAdapter.OnRecyclerViewItemClickListener mOnItemClickListener;

    public TransactionListRecyclerViewAdapter(RecyclerView recyclerView, List<Transaction> transactions,
                                              OnLoadMoreListener<Transaction> onLoadMoreListener) {
        super(recyclerView, transactions, onLoadMoreListener);
    }

    public void setOnItemLongClickListener(GenericRecyclerViewAdapter.OnRecyclerViewItemLongClickListener onLongClickListener) {
        mOnLongClickListener = onLongClickListener;
    }

    public void setOnItemClickListener(GenericRecyclerViewAdapter.OnRecyclerViewItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    protected ViewHolder createDataItemViewHolder(ViewGroup parent, int viewType) {
        View view = getLayoutInflater().inflate(R.layout.layout_new_item_transaction, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public long getItemId(int i) {
        return mItems.get(i).getId();
    }

    @Override
    public void bindData(final ViewHolder holder, Transaction transaction) {
        if (holder == null || transaction == null) {
            return;
        }

        int count = transaction.getProductCount();
        String name = count == 1 ? transaction.getProduct().getName() :
                String.format("%s × %d", transaction.getProduct().getName(), count);

        if (transaction.getPhotos() != null && transaction.getPhotos().size() > 0) {
            holder.imageViewTransactionPhoto.setVisibility(View.VISIBLE);
            ImageLoader.loadImage(getContext(),
                    transaction.getPhotos().iterator().next().getPhoto().getUrl(),
                    holder.imageViewTransactionPhoto, ImageLoader.PhotoScaleMode.CENTER_CROP);
        } else {
            holder.imageViewTransactionPhoto.setVisibility(View.GONE);
        }

        holder.textViewProductName.setText(name);
        holder.textViewCreationTime.setText(DateTimeUtils.formatDateTime(transaction.getCreationTime()));
        holder.textViewPrice.setPrice(CurrencyHelper.castToBigDecimal(transaction.getPrice()), transaction.getCurrencyCode());

        if (TextUtils.isEmpty(transaction.getDescription())) {
            holder.textViewDescription.setVisibility(View.GONE);
        } else {
            holder.textViewDescription.setVisibility(View.VISIBLE);
            holder.textViewDescription.setText(transaction.getDescription());
        }

        holder.checkBoxTransaction.setVisibility(transaction.checked ? View.VISIBLE : View.GONE);
        if (transaction.checked) {
            holder.checkBoxTransaction.setChecked(transaction.checked);
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

    public int getCheckedItemCount() {
        int count = 0;
        for (Transaction transaction : mItems) {
            if (transaction.checked) {
                count++;
            }
        }

        return count;
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

    public Transaction toggleSelection(int position) {
        Transaction transaction = mItems.get(position);
        if (transaction != null) {
            transaction.checked = !transaction.checked;
            notifyItemChanged(position);
        }

        return transaction;
    }

    public class ViewHolder extends GenericRecyclerViewHolder {
        public TextView textViewProductName;
        public TextView textViewCreationTime;
        public CurrencyTextView textViewPrice;
        public ImageView imageViewTransactionPhoto;
        public TextView textViewDescription;
        public CardView cardViewTransaction;
        public CheckBox checkBoxTransaction;

        public ViewHolder(View view) {
            super(view);

            textViewProductName = (TextView) view.findViewById(R.id.textViewProductName);
            textViewCreationTime = (TextView) view.findViewById(R.id.textViewCreationTime);
            textViewPrice = (CurrencyTextView) view.findViewById(R.id.textViewPrice);
            imageViewTransactionPhoto = (ImageView) view.findViewById(R.id.imageViewTransactionPhoto);
            textViewDescription = (TextView) view.findViewById(R.id.textViewDescription);
            cardViewTransaction = (CardView) view.findViewById(R.id.cardViewTransaction);
            checkBoxTransaction = (CheckBox) view.findViewById(R.id.checkBoxTransaction);

            cardViewTransaction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onRecyclerViewItemClick(getLayoutPosition());
                    }
                }
            });

            cardViewTransaction.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mOnLongClickListener != null) {
                        mOnLongClickListener.onRecyclerViewItemLongClick(getLayoutPosition());
                    }

                    return true;
                }
            });
        }
    }
}
