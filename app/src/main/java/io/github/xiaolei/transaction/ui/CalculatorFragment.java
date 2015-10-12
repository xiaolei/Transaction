package io.github.xiaolei.transaction.ui;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;

import de.greenrobot.event.EventBus;
import io.github.xiaolei.transaction.GlobalApplication;
import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.adapter.CalculatorItem;
import io.github.xiaolei.transaction.adapter.CalculatorPagerAdapter;
import io.github.xiaolei.transaction.entity.Product;
import io.github.xiaolei.transaction.entity.Transaction;
import io.github.xiaolei.transaction.event.DateSelectedEvent;
import io.github.xiaolei.transaction.event.NewProductCreatedEvent;
import io.github.xiaolei.transaction.event.PickPhotoEvent;
import io.github.xiaolei.transaction.event.ProductSelectedEvent;
import io.github.xiaolei.transaction.event.RefreshTransactionListEvent;
import io.github.xiaolei.transaction.listener.OnCalculatorActionClickListener;
import io.github.xiaolei.transaction.listener.OnCalculatorActionLongClickListener;
import io.github.xiaolei.transaction.listener.OnProductSelectedListener;
import io.github.xiaolei.transaction.repository.RepositoryProvider;
import io.github.xiaolei.transaction.repository.TransactionRepository;
import io.github.xiaolei.transaction.viewmodel.CalculatorOutputInfo;
import io.github.xiaolei.transaction.viewmodel.DailyTransactionSummaryInfo;
import io.github.xiaolei.transaction.viewmodel.TransactionType;
import io.github.xiaolei.transaction.widget.CalculatorOutputView;

/**
 * TODO: add comments
 */
public class CalculatorFragment extends BaseDataFragment implements OnProductSelectedListener, OnCalculatorActionClickListener, OnCalculatorActionLongClickListener {
    private static final String TAG = CalculatorFragment.class.getSimpleName();
    public static final int VIEW_INDEX_PRICE = 1;
    public static final int VIEW_INDEX_PRODUCTS = 0;
    public static final String ARG_PRODUCT = "arg_product";
    private ViewHolder mViewHolder;
    private Product mProduct;
    private CalculatorPagerAdapter mAdapter;

    public CalculatorFragment() {

    }

    public static CalculatorFragment newInstance() {
        CalculatorFragment fragment = new CalculatorFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        EventBus.getDefault().register(this);
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
        View view = inflater.inflate(R.layout.fragment_calculator, container, false);
        mViewHolder = new ViewHolder(view);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initialize();
    }

    private void initialize() {
        mAdapter = new CalculatorPagerAdapter(getChildFragmentManager(), mViewHolder.calculatorOutputView, this, this, this);
        mViewHolder.viewPagerCalculator.setAdapter(mAdapter);

        showDailyTransactionSummary();
        mViewHolder.calculatorOutputView.setTip("");

        CalculatorOutputInfo calculatorOutputInfo = new CalculatorOutputInfo();
        calculatorOutputInfo.currencyCode = GlobalApplication.getCurrentAccount().getDefaultCurrencyCode();
        mViewHolder.calculatorOutputView.bind(calculatorOutputInfo);
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
    public void onProductSelected(Product product) {
        mProduct = product;
        mViewHolder.calculatorOutputView.setProductName(product.getName());
        if (mViewHolder.calculatorOutputView.getTransactionType() == TransactionType.Unknown) {
            mViewHolder.calculatorOutputView.setTransactionType(TransactionType.Outgoing);
        }
        switchToPriceView();

        showLastTransactionPriceAsync(product.getName());
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
                    return RepositoryProvider.getInstance(getActivity()).resolve(TransactionRepository.class).getLastTransactionPrice(productNames[0]);
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
                    return RepositoryProvider.getInstance(getActivity()).resolve(TransactionRepository.class).getTransactionSummaryByDate(new Date()
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
    public void onCalculatorActionClick(CalculatorItem item) {
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
    public void onCalculatorActionLongClick(CalculatorItem item) {
        switch (item.actionId) {
            case 3:
                mViewHolder.calculatorOutputView.clear();
                break;
            default:
                break;
        }
    }

    public void onEvent(ProductSelectedEvent event) {
        mProduct = event.product;
        onProductSelected(mProduct);
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

    public void onEvent(PickPhotoEvent event) {
        mViewHolder.calculatorOutputView.showPhoto(event.photoFileUri);
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
                    transaction.setAccount(GlobalApplication.getCurrentAccount());
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
                String transactionType = outputInfo.transactionType == TransactionType.Incoming ? getString(R.string.incoming) : getString(R.string.outgoing);
                String message = String.format("%s %s: %s %s", transactionType, mProduct.getName(), outputInfo.currencyCode,
                        outputInfo.price.multiply(new BigDecimal(outputInfo.quantity)).toString());
                if (result != null) {
                    message = result.toString();
                } else {
                    showDailyTransactionSummary();
                }

                showSnackbarMessage(mViewHolder.view, message);
                reset();

                EventBus.getDefault().post(new RefreshTransactionListEvent());
            }
        };

        task.execute();
    }

    private void reset() {
        mProduct = null;
        mViewHolder.calculatorOutputView.setProductName("");
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
