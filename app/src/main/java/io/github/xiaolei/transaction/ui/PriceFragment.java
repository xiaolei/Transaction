package io.github.xiaolei.transaction.ui;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.adapter.CalculatorAdapter;
import io.github.xiaolei.transaction.adapter.CalculatorItem;
import io.github.xiaolei.transaction.entity.Product;
import io.github.xiaolei.transaction.listener.OnCalculatorActionClickListener;
import io.github.xiaolei.transaction.listener.OnCalculatorActionLongClickListener;
import io.github.xiaolei.transaction.widget.CalculatorOutputView;

/**
 * TODO: add comments
 */
public class PriceFragment extends BaseDataFragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private static final String TAG = PriceFragment.class.getSimpleName();
    public static final String ARG_PRODUCT = "arg_product";
    private static final int TEXT_SIZE_ACTION_BUTTON = 15;
    private static final int TEXT_SIZE_NUMBER_BUTTON = 20;
    private List<CalculatorItem> mItems;
    private CalculatorAdapter mAdapter;
    private ViewHolder mViewHolder;
    private Product mProduct;
    private CalculatorOutputView mOutputView;
    private OnCalculatorActionClickListener mActionClickListener;
    private OnCalculatorActionLongClickListener mActionLongClickListener;

    public PriceFragment() {
        setHasOptionsMenu(false);
    }

    public void setProduct(Product product) {
        mProduct = product;
    }

    public void setOutputView(CalculatorOutputView outputView) {
        mOutputView = outputView;
    }

    private void initialize() {
        mItems = new ArrayList<CalculatorItem>();
        mItems.add(new CalculatorItem("Erase", 3, TEXT_SIZE_ACTION_BUTTON));
        mItems.add(new CalculatorItem("7", 0, TEXT_SIZE_NUMBER_BUTTON));
        mItems.add(new CalculatorItem("8", 0, TEXT_SIZE_NUMBER_BUTTON));
        mItems.add(new CalculatorItem("9", 0, TEXT_SIZE_NUMBER_BUTTON));
        mItems.add(new CalculatorItem(getString(R.string.outgoing), 2, TEXT_SIZE_ACTION_BUTTON));
        mItems.add(new CalculatorItem("4", 0, TEXT_SIZE_NUMBER_BUTTON));
        mItems.add(new CalculatorItem("5", 0, TEXT_SIZE_NUMBER_BUTTON));
        mItems.add(new CalculatorItem("6", 0, TEXT_SIZE_NUMBER_BUTTON));
        mItems.add(new CalculatorItem(getString(R.string.incoming), 1, TEXT_SIZE_ACTION_BUTTON));
        mItems.add(new CalculatorItem("1", 0, TEXT_SIZE_NUMBER_BUTTON));
        mItems.add(new CalculatorItem("2", 0, TEXT_SIZE_NUMBER_BUTTON));
        mItems.add(new CalculatorItem("3", 0, TEXT_SIZE_NUMBER_BUTTON));
        mItems.add(new CalculatorItem(getString(R.string.ok), 4, TEXT_SIZE_ACTION_BUTTON));
        mItems.add(new CalculatorItem("0", 0, TEXT_SIZE_NUMBER_BUTTON));
        mItems.add(new CalculatorItem("00", 0, TEXT_SIZE_NUMBER_BUTTON));
        mItems.add(new CalculatorItem(".", 0, TEXT_SIZE_NUMBER_BUTTON));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            String productJson = args.getString(ARG_PRODUCT, "");
            if (!TextUtils.isEmpty(productJson)) {
                mProduct = new Gson().fromJson(productJson, Product.class);
            }
        }

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_price, container, false);
        mViewHolder = new ViewHolder(view);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initialize();
        load();
    }

    public void load() {
        mAdapter = new CalculatorAdapter(getActivity(), mItems);
        mViewHolder.gridView.setOnItemClickListener(this);
        mViewHolder.gridView.setOnItemLongClickListener(this);
        mViewHolder.gridView.setAdapter(mAdapter);
    }

    @Override
    public String getActionBarTitle() {
        return getString(R.string.calculator_title);
    }

    @Override
    public void switchToBusyView() {

    }

    @Override
    public void switchToRetryView() {

    }

    @Override
    public void switchToDataView() {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        CalculatorItem actionItem = (CalculatorItem) adapterView.getAdapter().getItem(position);
        if (actionItem != null && mOutputView != null) {
            if (mActionClickListener != null) {
                mActionClickListener.onCalculatorActionClick(actionItem);
            }
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (mActionLongClickListener != null) {
            CalculatorItem actionItem = (CalculatorItem) adapterView.getAdapter().getItem(position);
            mActionLongClickListener.onCalculatorActionLongClick(actionItem);
            return true;
        }

        return false;
    }

    public void setActionClickListener(OnCalculatorActionClickListener listener) {
        mActionClickListener = listener;
    }

    public void setActionLongClickListener(OnCalculatorActionLongClickListener listener) {
        mActionLongClickListener = listener;
    }


    private class ViewHolder {
        public GridView gridView;

        public ViewHolder(View view) {
            gridView = (GridView) view.findViewById(R.id.gridViewCalculator);
        }
    }
}
