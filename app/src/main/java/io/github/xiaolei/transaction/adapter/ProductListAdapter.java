package io.github.xiaolei.transaction.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.List;

import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.entity.Product;

/**
 * TODO: add comment
 */
public class ProductListAdapter extends BaseAdapter implements IDataAdapter<Product> {
    private List<Product> mProducts = new ArrayList<Product>();
    private Context mContext;
    private LayoutInflater mInflater;
    private ViewHolder mViewHolder;
    private AddProductViewHolder mAddProductViewHolder;

    public ProductListAdapter(Context context, List<Product> products) {
        mProducts.addAll(products);
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public void setProducts(List<Product> products) {
        mProducts = products;
    }

    @Override
    public int getCount() {
        return mProducts.size();
    }

    @Override
    public Object getItem(int i) {
        return mProducts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mProducts.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = mInflater.inflate(R.layout.layout_item_product, viewGroup, false);
            mViewHolder = new ViewHolder(view);
            view.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) view.getTag();
        }

        bindData(mProducts.get(i));
        return view;
    }

    private void bindData(Product product) {
        if (mViewHolder == null) {
            return;
        }

        if (product.getId() == -1) {
            mViewHolder.viewFlipperProductItemContainer.setDisplayedChild(1);
        } else {
            mViewHolder.viewFlipperProductItemContainer.setDisplayedChild(0);
            mViewHolder.textViewName.setText(product.getName());
            mViewHolder.imageView.setBackgroundColor(product.getBannerColor());
        }
    }

    @Override
    public void append(List<Product> data) {
        if (data != null) {
            mProducts.addAll(data);
        }
    }

    @Override
    public void swap(List<Product> data) {
        mProducts.clear();
        mProducts.addAll(data);
        notifyDataSetChanged();
    }

    private class ViewHolder {
        public ViewFlipper viewFlipperProductItemContainer;
        public TextView textViewName;
        public ImageView imageView;

        public ViewHolder(View view) {
            viewFlipperProductItemContainer = (ViewFlipper) view.findViewById(R.id.viewFlipperProductItemContainer);
            textViewName = (TextView) view.findViewById(R.id.textViewProductName);
            imageView = (ImageView) view.findViewById(R.id.imageViewProduct);
        }
    }

    private class AddProductViewHolder {
        public AddProductViewHolder(View view) {

        }
    }
}
