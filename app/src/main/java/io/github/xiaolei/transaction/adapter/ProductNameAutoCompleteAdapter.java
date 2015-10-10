package io.github.xiaolei.transaction.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.entity.Product;
import io.github.xiaolei.transaction.repository.ProductRepository;
import io.github.xiaolei.transaction.repository.RepositoryProvider;

/**
 * TODO: add comment
 */
public class ProductNameAutoCompleteAdapter extends BaseAdapter implements Filterable {
    private static final String TAG = ProductNameAutoCompleteAdapter.class.getSimpleName();
    private static final int MAX_SUGGESTION_ITEM_COUNT = 3;
    private Filter mFilter;
    private List<Product> mProducts = new ArrayList<Product>();
    private Context mContext;
    private ViewHolder mViewHolder;

    public ProductNameAutoCompleteAdapter(Context context) {
        mContext = context;
        initialize();
    }

    private void initialize() {
        mFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                mProducts.clear();

                if (constraint != null) {
                    try {
                        List<Product> result = RepositoryProvider.getInstance(mContext).resolve(ProductRepository.class).query(constraint.toString(), 0, MAX_SUGGESTION_ITEM_COUNT);
                        if (result != null) {
                            mProducts.addAll(result);
                        }

                        Log.d(TAG, String.format("Search: %s, result size: %d", constraint.toString(), result.size()));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    filterResults.values = mProducts;
                    filterResults.count = mProducts.size();
                }

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                    Log.d(TAG, "notifyDataSetChanged()");
                } else {
                    notifyDataSetInvalidated();
                    Log.d(TAG, "notifyDataSetInvalidated();");
                }
            }

            @Override
            public String toString() {
                return "AutoCompletedTextView.Filter";
            }
        };
    }

    @Override
    public int getCount() {
        return mProducts.size();
    }

    @Override
    public Product getItem(int position) {
        if (position < 0 || position > mProducts.size()) {
            return null;
        } else {
            return mProducts.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return mProducts.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_1, parent, false);
            mViewHolder = new ViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        bindData(mProducts.get(position));

        return convertView;
    }

    private void bindData(Product product) {
        if (mViewHolder == null) {
            return;
        }
        mViewHolder.textView.setText(product.getName());
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private class ViewHolder {
        public TextView textView;

        public ViewHolder(View view) {
            textView = (TextView) view.findViewById(android.R.id.text1);
        }
    }
}
