package io.github.xiaolei.transaction.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.List;

import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.util.PreferenceHelper;
import io.github.xiaolei.transaction.viewmodel.CurrencyCheckListItem;

/**
 * TODO: add comment
 */
public class CurrencyCheckListAdapter extends ArrayAdapter<CurrencyCheckListItem> {
    private ViewHolder mViewHolder;
    private LayoutInflater mInflater;
    private List<CurrencyCheckListItem> mData;
    public String mCheckedCurrencyCode = PreferenceHelper.DEFAULT_CURRENCY_CODE;

    public CurrencyCheckListAdapter(Context context, List<CurrencyCheckListItem> data, String checkedCurrencyCode) {
        super(context, R.layout.layout_item_choose_currency, data);
        mInflater = LayoutInflater.from(context);
        mData = data;

        if (!TextUtils.isEmpty(checkedCurrencyCode)) {
            mCheckedCurrencyCode = checkedCurrencyCode;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CurrencyCheckListItem item = mData.get(position);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.layout_item_choose_currency, parent, false);
            mViewHolder = new ViewHolder(convertView);
            convertView.setTag(mViewHolder);

            mViewHolder.radioButtonChooseCurrency.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCheckedCurrencyCode = ((CurrencyCheckListItem) view.getTag()).currencyCode;

                    notifyDataSetChanged();
                }
            });
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        bind(item);

        return convertView;
    }

    public String getCheckedCurrencyCode() {
        return mCheckedCurrencyCode;
    }

    private void bind(final CurrencyCheckListItem item) {
        mViewHolder.radioButtonChooseCurrency.setTag(item);
        mViewHolder.textViewCurrencyCode.setText(item.displayName);
        mViewHolder.radioButtonChooseCurrency.setChecked(TextUtils.equals(item.currencyCode, mCheckedCurrencyCode));
    }

    private class ViewHolder {
        public TextView textViewCurrencyCode;
        public RadioButton radioButtonChooseCurrency;

        public ViewHolder(View view) {
            textViewCurrencyCode = (TextView) view.findViewById(R.id.textViewCurrencyCode);
            radioButtonChooseCurrency = (RadioButton) view.findViewById(R.id.radioButtonChooseCurrency);
        }
    }
}
