package io.github.xiaolei.transaction.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import net.i2p.android.ext.floatingactionbutton.FloatingActionButton;
import net.i2p.android.ext.floatingactionbutton.FloatingActionsMenu;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;
import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.adapter.GenericEndlessAdapter;
import io.github.xiaolei.transaction.adapter.IPaginationDataLoader;
import io.github.xiaolei.transaction.adapter.ProductListAdapter;
import io.github.xiaolei.transaction.entity.Product;
import io.github.xiaolei.transaction.event.CreateProductEvent;
import io.github.xiaolei.transaction.event.DateSelectedEvent;
import io.github.xiaolei.transaction.event.GetBarcodeResultEvent;
import io.github.xiaolei.transaction.event.RefreshProductListEvent;
import io.github.xiaolei.transaction.event.ShowDatePickerEvent;
import io.github.xiaolei.transaction.listener.OnDateSelectedListener;
import io.github.xiaolei.transaction.listener.OnProductSelectedListener;
import io.github.xiaolei.transaction.repository.ProductRepository;
import io.github.xiaolei.transaction.repository.RepositoryProvider;
import io.github.xiaolei.transaction.util.ActivityHelper;
import io.github.xiaolei.transaction.util.ConfigurationManager;
import io.github.xiaolei.transaction.widget.DataContainerView;

/**
 * TODO: add comments
 */
public class QuickProductsFragment extends BaseFragment implements View.OnClickListener {
    public static final String ARG_IS_SELECTION_MODE = "is_selection_mode";
    public static final String ARG_SHOW_ADD_BUTTON = "arg_show_add_button";
    private static final String TAG = QuickProductsFragment.class.getSimpleName();
    private ViewHolder mViewHolder;
    private boolean mIsSelectionMode = false;
    private boolean mShowAddButton = false;
    private GenericEndlessAdapter<Product> mAdapter;
    private List<Product> mProductList = new ArrayList<Product>();
    private OnProductSelectedListener mOnProductSelectedListener;
    private android.view.ActionMode mActionMode;

    public static QuickProductsFragment newInstance(boolean isSelectionMode, boolean showAddButton) {
        QuickProductsFragment fragment = new QuickProductsFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_SELECTION_MODE, isSelectionMode);
        args.putBoolean(ARG_SHOW_ADD_BUTTON, showAddButton);
        fragment.setArguments(args);
        return fragment;
    }

    public QuickProductsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        Bundle args = getArguments();
        if (args != null) {
            mIsSelectionMode = args.getBoolean(ARG_IS_SELECTION_MODE, false);
            mShowAddButton = args.getBoolean(ARG_SHOW_ADD_BUTTON, false);
        }
    }

    private void newProduct() {
        ProductCreatorDialog.showDialog(getActivity().getSupportFragmentManager());
    }

    private void scanBarcode() {
        Intent intent = new Intent(getActivity(), BarcodeScannerActivity.class);
        startActivity(intent);
    }

    public void setIsSelectionMode(boolean isSelectionMode) {
        this.mIsSelectionMode = isSelectionMode;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");

    }

    public void setOnProductSelectedListener(OnProductSelectedListener listener) {
        mOnProductSelectedListener = listener;
    }

    protected void onProductSelected(Product product) {
        if (mOnProductSelectedListener != null) {
            mOnProductSelectedListener.onProductSelected(product);
        }
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_quick_products;
    }

    @Override
    public void initialize(View view) {
        mViewHolder = new ViewHolder(view);
        mViewHolder.gridViewProducts.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
        mViewHolder.gridViewProducts.setMultiChoiceModeListener(new MultiChoiceModeListener());
        mViewHolder.gridViewProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                Product product = (Product) adapterView.getAdapter().getItem(i);
                if (product != null) {
                    if (product.getId() != -1) {
                        if (mIsSelectionMode) {
                            onProductSelected(product);
                        } else {
                            ActivityHelper.startProductEditorActivity(getActivity(), product.getId());
                        }
                    } else {
                        newProduct();
                    }
                }
            }
        });
        mViewHolder.fabScanBarcode.setOnClickListener(this);
        mViewHolder.fabChooseDate.setOnClickListener(this);
        mViewHolder.fabNewProduct.setOnClickListener(this);
    }

    @Override
    public void load() {
        loadProductList();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);

        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    public void onEvent(RefreshProductListEvent event) {
        loadProductList();
    }

    public void onEvent(ShowDatePickerEvent event) {
        chooseTransactionDate(event.initialDate);
    }

    public void onEvent(GetBarcodeResultEvent event) {
        Toast.makeText(getActivity(), event.barcodeResult.getText(), Toast.LENGTH_SHORT).show();
    }

    public void onEvent(CreateProductEvent event) {
        newProduct();
    }

    public void loadProductList() {
        mViewHolder.dataContainerView.switchToBusyView();
        AsyncTask<Void, Void, List<Product>> task = new AsyncTask<Void, Void, List<Product>>() {
            @Override
            protected List<Product> doInBackground(Void... voids) {
                List<Product> result = new ArrayList<Product>();
                try {
                    result = RepositoryProvider.getInstance(getActivity()).resolve(ProductRepository.class).query(0, ConfigurationManager.DEFAULT_PAGE_SIZE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return result;
            }

            @Override
            protected void onPostExecute(List<Product> result) {
                mProductList = result;
                showProductList(mProductList);
            }
        };
        task.execute();
    }

    private void showProductList(List<Product> products) {
        if (mViewHolder == null || !isAdded()) {
            return;
        }

        if (mShowAddButton) {
            products.add(0, new Product(-1));
        }

        if (products.size() > 0) {
            ProductListAdapter innerAdapter = new ProductListAdapter(getActivity(), products);
            mAdapter = new GenericEndlessAdapter<Product>(getActivity(), innerAdapter, new IPaginationDataLoader<Product>() {
                @Override
                public List<Product> load(int offset, int limit) throws SQLException {
                    return RepositoryProvider.getInstance(getActivity()).resolve(ProductRepository.class).query(offset, limit);
                }
            });

            mViewHolder.gridViewProducts.setAdapter(mAdapter);
            mViewHolder.dataContainerView.switchToDataView();
        } else {
            switchToEmptyView();
        }
    }

    private void switchToEmptyView() {
        mViewHolder.dataContainerView.setEmptyViewDisplayText(getString(R.string.no_product));
        mViewHolder.dataContainerView.switchToEmptyView();
    }

    private void deleteProduct(long[] productIds) {
        if (productIds == null || productIds.length == 0) {
            return;
        }

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setMessage(String.format(getString(R.string.confirm_message_delete_products), productIds.length))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).create();
        dialog.show();
    }

    @Override
    public int getActionBarTitle() {
        return R.string.products;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mActionMode != null) {
            mActionMode.finish();
        }
    }

    private void chooseTransactionDate(Date initialDate) {
        DatePickerFragment.showDialog(getActivity().getSupportFragmentManager(), initialDate,
                new OnDateSelectedListener() {
                    @Override
                    public void onDateSelected(Date selectedDate) {
                        EventBus.getDefault().post(new DateSelectedEvent(selectedDate));
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fabNewProduct:
                newProduct();
                mViewHolder.famProduct.collapse();
                break;
            case R.id.fabChooseDate:
                chooseTransactionDate(null);
                mViewHolder.famProduct.collapse();
                break;
            case R.id.fabScanBarcode:
                scanBarcode();
                mViewHolder.famProduct.collapse();
                break;
            default:
                break;
        }
    }

    private class MultiChoiceModeListener implements GridView.MultiChoiceModeListener {
        @Override
        public void onItemCheckedStateChanged(android.view.ActionMode actionMode, int position, long id, boolean checked) {
            int selectCount = mViewHolder.gridViewProducts.getCheckedItemCount();
            switch (selectCount) {
                case 1:
                    actionMode.setSubtitle("One product selected");
                    break;
                default:
                    actionMode.setSubtitle("" + selectCount + " products selected");
                    break;
            }
        }

        @Override
        public boolean onCreateActionMode(android.view.ActionMode actionMode, Menu menu) {
            if (mIsSelectionMode) {
                return false;
            }

            mActionMode = actionMode;
            actionMode.getMenuInflater().inflate(R.menu.action_mode_product_list, menu);
            actionMode.setTitle("Select Products");
            actionMode.setSubtitle("One product selected");

            return true;
        }

        @Override
        public boolean onPrepareActionMode(android.view.ActionMode actionMode, Menu menu) {
            return true;
        }

        @Override
        public boolean onActionItemClicked(android.view.ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_delete_product:
                    deleteProduct(mViewHolder.gridViewProducts.getCheckedItemIds());
                    break;
                default:
                    break;
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(android.view.ActionMode actionMode) {
            mActionMode = null;
        }
    }

    private class ViewHolder {
        public DataContainerView dataContainerView;
        public GridView gridViewProducts;
        public FloatingActionsMenu famProduct;
        public FloatingActionButton fabScanBarcode;
        public FloatingActionButton fabChooseDate;
        public FloatingActionButton fabNewProduct;

        public ViewHolder(View view) {
            dataContainerView = (DataContainerView) view.findViewById(R.id.dataContainerViewProducts);
            gridViewProducts = (GridView) view.findViewById(R.id.gridViewProducts);
            famProduct = (FloatingActionsMenu) view.findViewById(R.id.famProduct);
            fabScanBarcode = (FloatingActionButton) view.findViewById(R.id.fabScanBarcode);
            fabChooseDate = (FloatingActionButton) view.findViewById(R.id.fabChooseDate);
            fabNewProduct = (FloatingActionButton) view.findViewById(R.id.fabNewProduct);
        }
    }

}