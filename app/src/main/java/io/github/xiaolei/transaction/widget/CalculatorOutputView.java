package io.github.xiaolei.transaction.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.google.gson.Gson;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;

import de.greenrobot.event.EventBus;
import io.github.xiaolei.enterpriselibrary.utility.DateTimeUtils;
import io.github.xiaolei.enterpriselibrary.utility.DialogHelper;
import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.common.ValidationHelper;
import io.github.xiaolei.transaction.entity.Product;
import io.github.xiaolei.transaction.event.ProductSelectedEvent;
import io.github.xiaolei.transaction.event.RefreshProductListEvent;
import io.github.xiaolei.transaction.event.ShowDatePickerEvent;
import io.github.xiaolei.transaction.listener.OnFragmentDialogDismissListener;
import io.github.xiaolei.transaction.repository.ProductRepository;
import io.github.xiaolei.transaction.repository.RepositoryProvider;
import io.github.xiaolei.transaction.ui.ChooseCurrencyFragment;
import io.github.xiaolei.transaction.ui.ProductRenameDialog;
import io.github.xiaolei.transaction.util.ConfigurationManager;
import io.github.xiaolei.transaction.util.PreferenceHelper;
import io.github.xiaolei.transaction.viewmodel.CalculatorOutputInfo;
import io.github.xiaolei.transaction.viewmodel.TransactionType;

/**
 * TODO: add comment
 */
public class CalculatorOutputView extends RelativeLayout {
    protected static final String TAG = CalculatorOutputView.class.getSimpleName();
    public static final int MAX_PRICE_LENGTH = 9;
    public static final int MAX_DECIMAL_LENGTH = 2;
    public static final int MAX_QUANTITY_LENGTH = 4;
    public static final int DEFAULT_QUANTITY_COUNT = 1;
    public static final String QUANTITY_SYMBOL = "×";

    private ViewHolder mViewHolder;
    private CalculatorOutputInfo mCalculatorOutputInfo = new CalculatorOutputInfo();
    private boolean mIsLastPrice = false;
    private boolean mQuantityOn = false;

    private PopupMenu mPopupMenu;

    public CalculatorOutputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize(context, attrs);
    }

    public CalculatorOutputView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.initialize(context, attrs);
    }

    public CalculatorOutputView(Context context) {
        super(context);
        this.initialize(context, null);
    }

    protected void initialize(Context context, AttributeSet attrs) {
        View view = View.inflate(context, R.layout.view_calculator_output, this);
        mViewHolder = new ViewHolder(view);

        mViewHolder.textViewTransactionDate.setVisibility(View.GONE);
        mViewHolder.textViewCalculatorPrice.setText("");
        mViewHolder.textViewCalculatorProductName.setText("");
        mViewHolder.textViewCurrencyCode.setText(PreferenceHelper.DEFAULT_CURRENCY_CODE);
        mViewHolder.textViewIncomingOrOutgoing.setText("");
        mViewHolder.textViewCurrencyCode.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentActivity activity = (FragmentActivity) getContext();
                final ChooseCurrencyFragment fragment = ChooseCurrencyFragment.newInstance(getCurrencyCode());
                fragment.setOnFragmentDialogDismissListener(new OnFragmentDialogDismissListener<String>() {
                    @Override
                    public void onFragmentDialogDismiss(String result) {
                        setCurrencyCode(result);
                        ConfigurationManager.changeDefaultCurrencyCode(getContext(), result);
                    }
                });
                fragment.show(activity.getSupportFragmentManager(), ChooseCurrencyFragment.TAG);
            }
        });
        mViewHolder.textViewTransactionDate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new ShowDatePickerEvent(mCalculatorOutputInfo.date));
            }
        });

        mPopupMenu = new PopupMenu(getContext(), mViewHolder.textViewCalculatorProductName);
        mPopupMenu.getMenuInflater().inflate(R.menu.menu_product_name, mPopupMenu.getMenu());
        mPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (mCalculatorOutputInfo.product == null) {
                    return true;
                }

                switch (item.getItemId()) {
                    case R.id.menu_rename_product:
                        AppCompatActivity activity = (AppCompatActivity) getContext();
                        ProductRenameDialog.showDialog(getContext(), activity.getSupportFragmentManager(), mCalculatorOutputInfo.product);

                        return true;
                    case R.id.menu_remove_product:
                        DialogHelper.showConfirmDialog(getContext(), getContext().getString(R.string.confirm_remove_product),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        long productId = mCalculatorOutputInfo.product.getId();
                                        removeProductAsync(productId);
                                    }
                                });

                        return true;
                    default:
                        return false;
                }
            }
        });

        mViewHolder.textViewCalculatorProductName.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = ((TextView) mViewHolder.textViewCalculatorProductName.getCurrentView()).getText().toString();
                if (TextUtils.isEmpty(text)) {
                    return;
                }

                if (mPopupMenu != null) {
                    mPopupMenu.show();
                }
            }
        });
    }

    private void removeProductAsync(long productId) {
        final ProductRepository productRepository = RepositoryProvider.getInstance(getContext()).resolve(ProductRepository.class);
        AsyncTask<Long, Void, String> task = new AsyncTask<Long, Void, String>() {

            @Override
            protected String doInBackground(Long... params) {
                String errorMessage = null;
                long productId = params[0];

                try {
                    productRepository.remove(productId);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                return errorMessage;
            }

            @Override
            public void onPostExecute(String errorMessage) {
                if (!TextUtils.isEmpty(errorMessage)) {
                    DialogHelper.showAlertDialog(getContext(), errorMessage);
                } else {
                    EventBus.getDefault().post(new ProductSelectedEvent(null));
                    EventBus.getDefault().post(new RefreshProductListEvent());
                }
            }
        };
        task.execute(productId);
    }

    public void bind(CalculatorOutputInfo outputInfo) {
        mCalculatorOutputInfo = outputInfo;
        showOutputInfo();
    }

    private void showOutputInfo() {
        mViewHolder.textViewCalculatorPrice.setText(mCalculatorOutputInfo.price.toString());
        setProductName(mCalculatorOutputInfo.productName);
        setCurrencyCode(mCalculatorOutputInfo.currencyCode);
        setTransactionType(mCalculatorOutputInfo.transactionType);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(TAG, super.onSaveInstanceState());
        bundle.putString("output_info_json", new Gson().toJson(getOutputInfo()));

        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {

        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            String stateJson = bundle.getString("output_info_json");
            mCalculatorOutputInfo = new Gson().fromJson(stateJson, CalculatorOutputInfo.class);
            showOutputInfo();

            state = bundle.getParcelable(TAG);
        }
        super.onRestoreInstanceState(state);
    }

    public void clear() {
        mCalculatorOutputInfo.price = new BigDecimal("0");
        mViewHolder.textViewCalculatorPrice.setText("0");
    }

    private boolean exceedLengthLimitation(String output) {
        int increaseLength = output.length();
        String value = getOutputText();
        String priceText = value;
        String quantityText = "";
        boolean containsQuantitySymbol = value.contains(QUANTITY_SYMBOL);
        boolean isQuantitySymbol = output.equals(QUANTITY_SYMBOL);

        if (value.contains(QUANTITY_SYMBOL)) {
            String[] array = value.split(QUANTITY_SYMBOL);
            priceText = array[0];
            if (array.length > 1) {
                quantityText = array[1];
            }
        }

        if (!TextUtils.isEmpty(quantityText) && quantityText.length() + increaseLength > MAX_QUANTITY_LENGTH) {
            return true;
        }

        if (!priceText.contains(".")) {
            if (!output.equals(".")) {
                return !containsQuantitySymbol && !isQuantitySymbol && (priceText.length() + increaseLength > MAX_PRICE_LENGTH);
            } else {
                return !containsQuantitySymbol && !isQuantitySymbol && (priceText.length() + increaseLength > MAX_PRICE_LENGTH + 1);
            }
        } else {
            String[] values = priceText.split("\\.");
            if (values.length == 1) {
                return false;
            } else if (values.length == 2) {
                return !containsQuantitySymbol && !isQuantitySymbol && (values[1].length() + increaseLength > MAX_DECIMAL_LENGTH);
            }
        }

        return false;
    }

    public void output(String inputText) {
        if (mIsLastPrice) {
            mIsLastPrice = false;

            // If currently displaying text is the last price and the input text is not quantity symbol, then clear output
            if (!inputText.equals(QUANTITY_SYMBOL)) {
                clear();
            }
        }

        if (exceedLengthLimitation(inputText)) {
            return;
        }

        String value = getOutputText();

        // Allow input only 1 dot symbol
        if (value.contains(".") && inputText.equals(".")) {
            return;
        }

        // Allow input only 1 quantity symbol
        if (value.contains(QUANTITY_SYMBOL) && inputText.equals(QUANTITY_SYMBOL)) {
            removeQuantity();
            return;
        }

        // When quantity symbol is visible, do not allow input dot symbol
        if (value.contains(QUANTITY_SYMBOL) && inputText.equals(".")) {
            return;
        }

        // Do not allow input quantity symbol, if current value is zero
        if (value.equals("0") && inputText.equals(QUANTITY_SYMBOL)) {
            return;
        }

        if (value.equals("0") && inputText.startsWith("00")) {
            mViewHolder.textViewCalculatorPrice.setText("0");
            return;
        }

        if (value.equals("0") && inputText.equals(".")) {
            mViewHolder.textViewCalculatorPrice.setText("0" + inputText);
            return;
        }

        if (value.startsWith("00") && inputText.equals(".")) {
            mViewHolder.textViewCalculatorPrice.setText("0" + inputText);
            return;
        }

        if (!value.startsWith("0") || value.contains(".")) {
            mViewHolder.textViewCalculatorPrice.setText(value + inputText);
        } else {
            mViewHolder.textViewCalculatorPrice.setText(inputText);
        }

        //ObjectAnimator mover = ObjectAnimator.ofFloat(mViewHolder.textViewCalculatorPrice, "translationX", mViewHolder.textViewCalculatorPrice.getTextSize()/2, 0);
        //mover.start();
    }

    public String validate() {
        String outputText = getOutputText();

        if (TextUtils.isEmpty(outputText) || outputText.equals("0")) {
            return getContext().getString(R.string.validation_error_input_price);
        }

        if (outputText.contains(QUANTITY_SYMBOL)) {
            String[] array = outputText.split(QUANTITY_SYMBOL);
            String priceText = array[0];
            String quantityText = "";

            if (array.length == 2) {
                quantityText = array[1];
            }

            // Validate price value
            if (TextUtils.isEmpty(priceText)) {
                return getContext().getString(R.string.validation_error_input_price);
            } else if (!ValidationHelper.isValidBigDecimal(priceText)) {
                return getContext().getString(R.string.validation_error_invalid_price);
            }

            // Validate quantity value
            if (!TextUtils.isEmpty(quantityText)) {
                if (!ValidationHelper.isValidInteger(quantityText)) {
                    return getContext().getString(R.string.validation_error_invalid_quantity);
                }
            }
        } else {
            if (!ValidationHelper.isValidBigDecimal(outputText)) {
                return getContext().getString(R.string.validation_error_invalid_price);
            }
        }

        return "";
    }

    public void erase() {
        String value = mViewHolder.textViewCalculatorPrice.getText().toString();

        if (TextUtils.isEmpty(value)) {
            clear();
        } else {
            if (value.length() - 1 > 0) {
                mViewHolder.textViewCalculatorPrice.setText(value.substring(0, value.length() - 1));
            } else {
                clear();
            }
        }
    }

    public void setQuantitySymbol(boolean visibility) {
        mQuantityOn = visibility;
        if (visibility) {
            output(QUANTITY_SYMBOL);
        } else {
            removeQuantity();
        }
    }

    public void removeQuantity() {
        mQuantityOn = false;
        String outputText = getOutputText();
        if (!TextUtils.isEmpty(outputText) && outputText.contains(QUANTITY_SYMBOL)) {
            String[] array = outputText.split(QUANTITY_SYMBOL);
            if (array.length > 1) {
                clear();
                output(array[0]);
            } else {
                clear();
                output(outputText.replace(QUANTITY_SYMBOL, ""));
            }
        }
    }

    public void setPrice(BigDecimal price) {
        mIsLastPrice = false;
        mCalculatorOutputInfo.price = price;
        mViewHolder.textViewCalculatorPrice.setText(price.toString());
    }

    public int getQuantityValue() {
        String outputText = getOutputText();
        if (TextUtils.isEmpty(outputText)) {
            return DEFAULT_QUANTITY_COUNT;
        }

        if (outputText.contains(QUANTITY_SYMBOL)) {
            String[] array = outputText.split(QUANTITY_SYMBOL);
            if (array.length > 1) {
                return Integer.parseInt(array[1]);
            }
        }

        return DEFAULT_QUANTITY_COUNT;
    }

    public void setLastPrice(BigDecimal price) {
        mIsLastPrice = true;
        mCalculatorOutputInfo.price = price;
        mViewHolder.textViewCalculatorPrice.setText(price.toString());
    }

    public void setProductName(String name) {
        mCalculatorOutputInfo.productName = name;
        mViewHolder.textViewCalculatorProductName.setText(name);
    }

    public void setProduct(Product product) {
        mCalculatorOutputInfo.product = product;
        setProductName(product != null ? product.getName() : "");
    }

    public void setCurrencyCode(String currencyCode) {
        mCalculatorOutputInfo.currencyCode = currencyCode;
        mViewHolder.textViewCurrencyCode.setText(currencyCode);
    }

    public String getCurrencyCode() {
        return mViewHolder.textViewCurrencyCode.getText().toString();
    }

    public void setTransactionType(TransactionType type) {
        mCalculatorOutputInfo.transactionType = type;
        mViewHolder.textViewIncomingOrOutgoing.setText(getTransactionTypeName(type));
        int productNameTextColor = type == TransactionType.Incoming ? R.color.incoming_text_color : R.color.expense_text_color;
        mViewHolder.textViewIncomingOrOutgoing.setTextColor(getResources().getColor(productNameTextColor));
    }

    public void setTransactionDate(Date date) {
        mCalculatorOutputInfo.date = date;
        mViewHolder.textViewTransactionDate.setText(date != null ? DateTimeUtils.formatDateTime(date) : "");
        mViewHolder.textViewTransactionDate.setVisibility(date != null ? View.VISIBLE : View.GONE);
    }

    public Date getTransactionDate() {
        return mCalculatorOutputInfo.date;
    }

    public TransactionType getTransactionType() {
        return mCalculatorOutputInfo.transactionType;
    }

    private BigDecimal getPrice() {
        String outputText = getOutputText();
        if (!outputText.contains(QUANTITY_SYMBOL)) {
            return new BigDecimal(outputText);
        } else {
            String[] array = outputText.split(QUANTITY_SYMBOL);
            return new BigDecimal(array[0]);
        }
    }

    public CalculatorOutputInfo getOutputInfo() {
        mCalculatorOutputInfo.price = getPrice();
        mCalculatorOutputInfo.quantity = getQuantityValue();

        return mCalculatorOutputInfo;
    }

    private String getTransactionTypeName(TransactionType type) {
        String text = "";
        if (type == TransactionType.Incoming) {
            text = getContext().getString(R.string.flag_incoming);
        } else if (type == TransactionType.Outgoing) {
            text = getContext().getString(R.string.flag_outgoing);
        }

        return text;
    }

    public String getOutputText() {
        return mViewHolder.textViewCalculatorPrice.getText().toString();
    }

    public void setTip(String tip) {
        mViewHolder.textViewTodayAmount.setText(tip);
    }

    private class ViewHolder {
        public TextView textViewCalculatorPrice;
        public TextSwitcher textViewCalculatorProductName;
        public TextView textViewCurrencyCode;
        public TextView textViewIncomingOrOutgoing;
        public TextSwitcher textViewTodayAmount;
        public TextView textViewTransactionDate;

        public ViewHolder(View view) {
            textViewCalculatorPrice = (TextView) view.findViewById(R.id.textViewCalculatorPrice);
            textViewCalculatorProductName = (TextSwitcher) view.findViewById(R.id.textViewCalculatorProductName);
            textViewCurrencyCode = (TextView) view.findViewById(R.id.textViewCurrencyCode);
            textViewIncomingOrOutgoing = (TextView) view.findViewById(R.id.textViewTransactionType);
            textViewTodayAmount = (TextSwitcher) view.findViewById(R.id.textViewTodayAmount);
            textViewTransactionDate = (TextView) view.findViewById(R.id.textViewTransactionDate);
        }
    }
}