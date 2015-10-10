package io.github.xiaolei.transaction.widget;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.math.BigDecimal;
import java.util.Date;

import de.greenrobot.event.EventBus;
import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.event.ShowDatePickerEvent;
import io.github.xiaolei.transaction.listener.OnFragmentDialogDismissListener;
import io.github.xiaolei.transaction.ui.ChooseCurrencyFragment;
import io.github.xiaolei.transaction.ui.DatePickerFragment;
import io.github.xiaolei.transaction.util.ConfigurationManager;
import io.github.xiaolei.transaction.util.DateTimeUtils;
import io.github.xiaolei.transaction.util.PreferenceHelper;
import io.github.xiaolei.transaction.viewmodel.CalculatorOutputInfo;
import io.github.xiaolei.transaction.viewmodel.TransactionType;

/**
 * TODO: add comment
 */
public class CalculatorOutputView extends RelativeLayout {
    protected static final String TAG = CalculatorOutputView.class.getSimpleName();
    private ViewHolder mViewHolder;
    public static final int MAX_INTEGER_LENGTH = 9;
    public static final int MAX_DECIMAL_LENGTH = 2;
    private CalculatorOutputInfo mCalculatorOutputInfo = new CalculatorOutputInfo();
    private boolean mIsLastPrice = false;

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
                EventBus.getDefault().post(new ShowDatePickerEvent());
            }
        });
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
        String value = getPriceText();

        if (!value.contains(".")) {
            if (!output.equals(".")) {
                return value.length() + increaseLength > MAX_INTEGER_LENGTH;
            } else {
                return value.length() + increaseLength > MAX_INTEGER_LENGTH + 1;
            }
        } else {
            String[] values = value.split("\\.");
            if (values.length == 1) {
                return false;
            } else if (values.length == 2) {
                return values[1].length() + increaseLength > MAX_DECIMAL_LENGTH;
            }
        }

        return false;
    }

    public void output(String text) {
        if (mIsLastPrice) {
            mIsLastPrice = false;
            clear();
        }

        if (exceedLengthLimitation(text)) {
            return;
        }

        String value = getPriceText();
        if (value.contains(".") && text.equals(".")) {
            return;
        }

        if (value.equals("0") && text.startsWith("00")) {
            mViewHolder.textViewCalculatorPrice.setText("0");
            return;
        }

        if (value.equals("0") && text.equals(".")) {
            mViewHolder.textViewCalculatorPrice.setText("0" + text);
            return;
        }

        if (value.startsWith("00") && text.equals(".")) {
            mViewHolder.textViewCalculatorPrice.setText("0" + text);
            return;
        }

        if (!value.startsWith("0") || value.contains(".")) {
            mViewHolder.textViewCalculatorPrice.setText(value + text);
        } else {
            mViewHolder.textViewCalculatorPrice.setText(text);
        }
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

    public void setPrice(BigDecimal price) {
        mIsLastPrice = false;
        mCalculatorOutputInfo.price = price;
        mViewHolder.textViewCalculatorPrice.setText(price.toString());
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
        mViewHolder.textViewTransactionDate.setText(date != null ? String.format(getResources().getString(R.string.transaction_date), DateTimeUtils.formatDateTime(date)) : "");
        mViewHolder.textViewTransactionDate.setVisibility(date != null ? View.VISIBLE : View.GONE);
    }

    public Date getTransactionDate() {
        return mCalculatorOutputInfo.date;
    }

    public TransactionType getTransactionType() {
        return mCalculatorOutputInfo.transactionType;
    }

    private BigDecimal getPrice() {
        String price = mViewHolder.textViewCalculatorPrice.getText().toString();
        return new BigDecimal(!TextUtils.isEmpty(price) ? price : "0");
    }

    public CalculatorOutputInfo getOutputInfo() {
        mCalculatorOutputInfo.price = getPrice();

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

    public String getPriceText() {
        return mViewHolder.textViewCalculatorPrice.getText().toString();
    }

    public void showPhoto(String photoFileName) {
        if (TextUtils.isEmpty(photoFileName)) {
            return;
        }
        mViewHolder.transactionPhotoGallery.setVisibility(View.VISIBLE);
        Picasso.with(getContext()).load(new File(photoFileName)).fit().into(mViewHolder.imageViewPickedPhoto);
    }

    public void hidePhoto() {
        mViewHolder.transactionPhotoGallery.setVisibility(View.GONE);
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

        public RelativeLayout transactionPhotoGallery;
        public SquareImageView imageViewPickedPhoto;

        public ViewHolder(View view) {
            textViewCalculatorPrice = (TextView) view.findViewById(R.id.textViewCalculatorPrice);
            textViewCalculatorProductName = (TextSwitcher) view.findViewById(R.id.textViewCalculatorProductName);
            textViewCurrencyCode = (TextView) view.findViewById(R.id.textViewCurrencyCode);
            textViewIncomingOrOutgoing = (TextView) view.findViewById(R.id.textViewTransactionType);
            textViewTodayAmount = (TextSwitcher) view.findViewById(R.id.textViewTodayAmount);
            textViewTransactionDate = (TextView) view.findViewById(R.id.textViewTransactionDate);

            transactionPhotoGallery = (RelativeLayout) view.findViewById(R.id.transactionPhotoGallery);
            imageViewPickedPhoto = (SquareImageView) view.findViewById(R.id.imageViewPickedPhoto);
        }
    }
}