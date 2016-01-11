package io.github.xiaolei.transaction.ui;


import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;

import de.greenrobot.event.EventBus;
import io.github.xiaolei.enterpriselibrary.listener.OnOperationCompletedListener;
import io.github.xiaolei.enterpriselibrary.logging.Logger;
import io.github.xiaolei.enterpriselibrary.utility.DialogHelper;
import io.github.xiaolei.transaction.GlobalApplication;
import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.adapter.ButtonInfo;
import io.github.xiaolei.transaction.adapter.CalculatorPagerAdapter;
import io.github.xiaolei.transaction.database.DatabaseHelper;
import io.github.xiaolei.transaction.entity.Product;
import io.github.xiaolei.transaction.entity.Transaction;
import io.github.xiaolei.transaction.event.CreateProductEvent;
import io.github.xiaolei.transaction.event.DateSelectedEvent;
import io.github.xiaolei.transaction.event.NewProductCreatedEvent;
import io.github.xiaolei.transaction.event.ProductSelectedEvent;
import io.github.xiaolei.transaction.event.RefreshTransactionListEvent;
import io.github.xiaolei.transaction.event.SearchProductEvent;
import io.github.xiaolei.transaction.listener.OnCalculatorActionClickListener;
import io.github.xiaolei.transaction.listener.OnCalculatorActionLongClickListener;
import io.github.xiaolei.transaction.listener.OnProductSelectedListener;
import io.github.xiaolei.transaction.repository.RepositoryProvider;
import io.github.xiaolei.transaction.repository.TransactionRepository;
import io.github.xiaolei.transaction.util.ActivityHelper;
import io.github.xiaolei.transaction.viewmodel.CalculatorOutputInfo;
import io.github.xiaolei.transaction.viewmodel.DailyTransactionSummaryInfo;
import io.github.xiaolei.transaction.viewmodel.TransactionType;
import io.github.xiaolei.transaction.widget.CalculatorOutputView;

/**
 * TODO: add comments
 */
public class CalculatorFragment extends BaseFragment implements OnProductSelectedListener, OnCalculatorActionClickListener, OnCalculatorActionLongClickListener {
    public static final String TAG = CalculatorFragment.class.getSimpleName();
    public static final String ARG_TRANSACTION_DATE = "arg_transaction_date";

    public static final int VIEW_INDEX_PRICE = 1;
    public static final int VIEW_INDEX_PRODUCTS = 0;
    public static final String ARG_PRODUCT = "arg_product";
    private ViewHolder mViewHolder;
    private Product mProduct;
    private CalculatorPagerAdapter mAdapter;
    private Date mTransactionDate;
    private View.OnClickListener mOnSnackBarClickListener;

    public CalculatorFragment() {

    }

    public static CalculatorFragment newInstance(Date transactionDate) {
        CalculatorFragment fragment = new CalculatorFragment();

        if (transactionDate != null) {
            Bundle args = new Bundle();
            args.putLong(ARG_TRANSACTION_DATE, transactionDate.getTime());
            fragment.setArguments(args);
        }

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        EventBus.getDefault().register(this);
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_calculator;
    }

    @Override
    public void initialize(View view) {
        Bundle args = getArguments();
        if (args != null) {
            String productJson = args.getString(ARG_PRODUCT, "");
            if (!TextUtils.isEmpty(productJson)) {
                mProduct = new Gson().fromJson(productJson, Product.class);
            }

            long date = args.getLong(ARG_TRANSACTION_DATE, -1);
            if (date > 0) {
                mTransactionDate = new Date(date);
            } else {
                mTransactionDate = null;
            }
        }

        mViewHolder = new ViewHolder(view);
        mOnSnackBarClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityHelper.goToTransactionList(getActivity());
            }
        };
    }

    private void initializeSearchView(final MenuItem searchMenuItem) {
        if (searchMenuItem == null) {
            return;
        }

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        searchView.setQueryHint(getString(R.string.search_product));

        MenuItemCompat.setOnActionExpandListener(searchMenuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                searchProduct("");
                return true;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchProduct(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchProduct(newText);
                return true;
            }
        });
    }

    private void searchProduct(String keywords) {
        EventBus.getDefault().post(new SearchProductEvent(keywords));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu.clear();
        inflater.inflate(R.menu.menu_global_dev, menu);
        initializeSearchView(menu.findItem(R.id.action_search_product));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new_product:
                EventBus.getDefault().post(new CreateProductEvent());
                return true;
            case R.id.action_delete_database:
                DialogHelper.showConfirmDialog(getActivity(), "Are you sure you want to delete the database? Once delete operation completed, app will be finished.",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseHelper.deleteDatabase(getActivity());
                                Toast.makeText(getActivity(), "Database deleted.", Toast.LENGTH_SHORT).show();
                                getActivity().finish();
                            }
                        });

                return true;
            case R.id.action_clone_database:
                try {
                    DatabaseHelper.getInstance(getActivity()).backup();
                    Toast.makeText(getActivity(), "Database cloned.", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, e.toString());
                }
                return true;
            case R.id.action_execute_sql:
                DialogHelper.showInputDialog(getActivity(), "Execute SQL", "", new OnOperationCompletedListener<String>() {
                    @Override
                    public void onOperationCompleted(boolean success, String result, String message) {
                        if(TextUtils.isEmpty(result)){
                            return;
                        }

                        DatabaseHelper.getInstance(getActivity()).executeSql(result);
                        Toast.makeText(getActivity(), "SQL executed.", Toast.LENGTH_SHORT).show();
                    }
                });

                return true;
            default:
                return false;
        }
    }

    @Override
    public void load() {
        Logger.d(TAG, "loading " + TAG);

        mAdapter = new CalculatorPagerAdapter(getChildFragmentManager(), mViewHolder.calculatorOutputView, this, this, this);
        mViewHolder.viewPagerCalculator.setAdapter(mAdapter);

        showDailyTransactionSummary();
        mViewHolder.calculatorOutputView.setTip("");

        CalculatorOutputInfo calculatorOutputInfo = new CalculatorOutputInfo();
        calculatorOutputInfo.currencyCode = GlobalApplication.getCurrentAccount().getDefaultCurrencyCode();
        mViewHolder.calculatorOutputView.bind(calculatorOutputInfo);
        mViewHolder.calculatorOutputView.setTransactionDate(mTransactionDate);
    }

    @Override
    public int getActionBarTitle() {
        return R.string.calculator_title;
    }

    @Override
    public void onProductSelected(Product product) {
        if (mProduct != null && product != null && TextUtils.equals(product.getName(), mProduct.getName())) {
            return;
        }

        mProduct = product;

        if (product != null) {
            mViewHolder.calculatorOutputView.setProduct(product);

            if (mViewHolder.calculatorOutputView.getTransactionType() == TransactionType.Unknown) {
                mViewHolder.calculatorOutputView.setTransactionType(TransactionType.Outgoing);
            }

            switchToPriceView();
            showLastTransactionPriceAsync(product.getName());
        } else {
            reset();
        }
    }

    public int getCurrentViewIndex() {
        return mViewHolder.viewPagerCalculator.getCurrentItem();
    }

    public void switchToPriceView() {
        mViewHolder.viewPagerCalculator.setCurrentItem(VIEW_INDEX_PRICE, true);
    }

    public void switchToProductListView() {
        mViewHolder.viewPagerCalculator.setCurrentItem(VIEW_INDEX_PRODUCTS, true);
    }

    private void showLastTransactionPriceAsync(String productName) {
        AsyncTask<String, Void, BigDecimal> task = new AsyncTask<String, Void, BigDecimal>() {
            @Override
            protected BigDecimal doInBackground(String... productNames) {
                try {
                    long accountId = GlobalApplication.getCurrentAccount().getId();
                    return RepositoryProvider.getInstance(getActivity()).resolve(TransactionRepository.class).getLastTransactionPrice(accountId, productNames[0]);
                } catch (SQLException e) {
                    e.printStackTrace();
                    return BigDecimal.ZERO;
                }
            }

            @Override
            protected void onPostExecute(BigDecimal result) {
                if (!BigDecimal.ZERO.equals(result)) {
                    if (result.intValue() < 0) {
                        mViewHolder.calculatorOutputView.setTransactionType(TransactionType.Outgoing);
                    } else {
                        mViewHolder.calculatorOutputView.setTransactionType(TransactionType.Incoming);
                    }
                    mViewHolder.calculatorOutputView.setLastPrice(result.abs());
                }
            }
        };
        task.execute(productName);
    }

    private void showDailyTransactionSummary() {
        AsyncTask<Void, Void, DailyTransactionSummaryInfo> task = new AsyncTask<Void, Void, DailyTransactionSummaryInfo>() {
            @Override
            protected DailyTransactionSummaryInfo doInBackground(Void... args) {
                try {
                    return RepositoryProvider.getInstance(getActivity()).resolve(TransactionRepository.class).getTransactionSummaryByDate(
                            GlobalApplication.getCurrentAccount().getId(),
                            new Date()
                            , GlobalApplication.getCurrentAccount().getDefaultCurrencyCode());
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(DailyTransactionSummaryInfo result) {
                if (result != null) {
                    String tip = String.format(getString(R.string.today_transactions_amount) + ": %s %s (+%s/%s)", result.currencyCode, result.totalPrice.toString(), result.totalIncoming.toString(), result.totalOutgoing.toString());
                    mViewHolder.calculatorOutputView.setTip(tip);
                } else {
                    mViewHolder.calculatorOutputView.setTip("");
                }
            }
        };
        task.execute();
    }

    @Override
    public void onCalculatorActionClick(ButtonInfo item) {
        switch (item.actionId) {
            case 0:
                mViewHolder.calculatorOutputView.output(item.text);
                break;
            case 1:
                mViewHolder.calculatorOutputView.setTransactionType(TransactionType.Incoming);
                if (mProduct == null) {
                    switchToPriceView();
                }

                break;
            case 2:
                mViewHolder.calculatorOutputView.setTransactionType(TransactionType.Outgoing);
                if (mProduct == null) {
                    switchToPriceView();
                }

                break;
            case 3:
                mViewHolder.calculatorOutputView.erase();
                break;
            case 4:
                save();
                break;
            case 5:
                mViewHolder.calculatorOutputView.setQuantitySymbol(true);
                break;
            default:
                break;
        }
    }

    @Override
    public void onCalculatorActionLongClick(ButtonInfo item) {
        switch (item.actionId) {
            case 3:
                mViewHolder.calculatorOutputView.clear();
                break;
            default:
                break;
        }
    }

    public void onEvent(ProductSelectedEvent event) {
        onProductSelected(event.product);
    }

    public void onEvent(NewProductCreatedEvent event) {
        mProduct = event.product;
        if (mViewHolder.calculatorOutputView.getTransactionType() == TransactionType.Unknown) {
            mViewHolder.calculatorOutputView.setTransactionType(TransactionType.Outgoing);
        }

        onProductSelected(mProduct);
    }

    public void onEvent(DateSelectedEvent event) {
        mViewHolder.calculatorOutputView.setTransactionDate(event.selectedDate);
    }

    private void save() {
        String error = mViewHolder.calculatorOutputView.validate();
        if (!TextUtils.isEmpty(error)) {
            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
            return;
        }

        if (mProduct == null) {
            Toast.makeText(getActivity(), getString(R.string.validation_error_choose_product), Toast.LENGTH_SHORT).show();
            switchToProductListView();
            return;
        }

        final CalculatorOutputInfo outputInfo = mViewHolder.calculatorOutputView.getOutputInfo();

        if (outputInfo.transactionType == TransactionType.Unknown) {
            Toast.makeText(getActivity(), getString(R.string.validation_error_choose_transaction_type), Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, String.format("Quantity: %d", outputInfo.quantity));

        AsyncTask<Void, Void, Exception> task = new AsyncTask<Void, Void, Exception>() {

            @Override
            protected Exception doInBackground(Void... voids) {
                Exception error = null;
                try {
                    int price = outputInfo.price.movePointRight(2).intValue();
                    if (outputInfo.transactionType == TransactionType.Outgoing) {
                        price = price * -1;
                    }
                    TransactionRepository repository = RepositoryProvider.getInstance(getActivity()).resolve(TransactionRepository.class);
                    Transaction transaction = new Transaction();
                    transaction.setAccountId(GlobalApplication.getCurrentAccountId());
                    transaction.setCurrencyCode(outputInfo.currencyCode);
                    transaction.setProduct(mProduct);
                    transaction.setPrice(price * outputInfo.quantity);
                    transaction.setProductCount(outputInfo.quantity);
                    transaction.setProductPrice(price);
                    if (outputInfo.date != null) {
                        transaction.setCreationTime(outputInfo.date);
                    }
                    repository.save(transaction);
                } catch (SQLException e) {
                    error = e;
                    e.printStackTrace();
                }

                return error;
            }

            @Override
            protected void onPostExecute(Exception result) {
                mViewHolder.calculatorOutputView.setTransactionDate(null); // Reset transaction date

                String transactionType = outputInfo.transactionType == TransactionType.Incoming ? getString(R.string.incoming) : getString(R.string.outgoing);
                String message = String.format("%s %s: %s %s", transactionType, mProduct.getName(), outputInfo.currencyCode,
                        outputInfo.price.multiply(new BigDecimal(outputInfo.quantity)).toString());
                if (result != null) {
                    message = result.toString();
                } else {
                    showDailyTransactionSummary();
                }

                showSnackbarMessage(mViewHolder.view, message, mOnSnackBarClickListener);
                reset();

                EventBus.getDefault().post(new RefreshTransactionListEvent());
            }
        };

        task.execute();
    }

    public void reset() {
        mProduct = null;
        mViewHolder.calculatorOutputView.setProductName("");
        mViewHolder.calculatorOutputView.setProduct(null);
        mViewHolder.calculatorOutputView.setPrice(new BigDecimal("0"));
        mViewHolder.calculatorOutputView.setTransactionType(TransactionType.Unknown);
        mViewHolder.calculatorOutputView.setTransactionDate(null);
        switchToProductListView();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);

        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    private class ViewHolder {
        public CalculatorOutputView calculatorOutputView;
        public ViewPager viewPagerCalculator;
        public View view;

        public ViewHolder(View view) {
            this.view = view;
            calculatorOutputView = (CalculatorOutputView) view.findViewById(R.id.calculatorOutputView);
            viewPagerCalculator = (ViewPager) view.findViewById(R.id.viewPagerCalculator);
        }
    }
}
