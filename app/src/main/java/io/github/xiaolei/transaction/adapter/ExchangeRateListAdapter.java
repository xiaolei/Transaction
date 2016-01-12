package io.github.xiaolei.transaction.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import io.github.xiaolei.enterpriselibrary.utility.CurrencyHelper;
import io.github.xiaolei.enterpriselibrary.utility.DateTimeUtils;
import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.entity.ExchangeRate;
import io.github.xiaolei.transaction.listener.OnLoadMoreListener;
import io.github.xiaolei.transaction.viewholder.GenericRecyclerViewHolder;
import io.github.xiaolei.transaction.widget.CurrencyTextView;

/**
 * TODO: add comment
 */
public class ExchangeRateListAdapter extends EndlessGenericRecyclerViewAdapter<ExchangeRate, ExchangeRateListAdapter.ViewHolder> {

    public ExchangeRateListAdapter(RecyclerView recyclerView, List<ExchangeRate> items, OnLoadMoreListener<ExchangeRate> onLoadMoreListener) {
        super(recyclerView, items, onLoadMoreListener);
    }

    @Override
    protected ViewHolder createDataItemViewHolder(ViewGroup parent, int viewType) {
        View view = getLayoutInflater().inflate(R.layout.layout_item_exchange_rate, parent, false);
        return new ViewHolder(view);
    }

    @Override
    protected void bindData(ViewHolder viewHolder, ExchangeRate item) {
        viewHolder.textViewCurrencyName.setText(item.getCurrencyCode());
        viewHolder.textViewExchangeRate.setCurrencyCode(item.getCurrencyCode());
        viewHolder.textViewExchangeRate.setPrice(CurrencyHelper.castToBigDecimal(item.getExchangeRate()), item.getCurrencyCode());
        viewHolder.textViewCreationTime.setText(DateTimeUtils.formatDateTime(item.getCreationTime()));
    }

    public class ViewHolder extends GenericRecyclerViewHolder {
        public TextView textViewCurrencyName;
        public TextView textViewCreationTime;
        public CurrencyTextView textViewExchangeRate;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewCurrencyName = (TextView) itemView.findViewById(R.id.textViewCurrencyName);
            textViewCreationTime = (TextView) itemView.findViewById(R.id.textViewCreationTime);
            textViewExchangeRate = (CurrencyTextView) itemView.findViewById(R.id.textViewExchangeRate);
        }
    }
}
