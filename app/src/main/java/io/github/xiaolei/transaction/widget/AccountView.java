package io.github.xiaolei.transaction.widget;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.math.BigDecimal;

import de.hdodenhof.circleimageview.CircleImageView;
import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.entity.Account;
import io.github.xiaolei.transaction.listener.OnFragmentDialogDismissListener;
import io.github.xiaolei.transaction.ui.ChooseCurrencyFragment;
import io.github.xiaolei.transaction.util.PreferenceHelper;
import io.github.xiaolei.transaction.viewmodel.CalculatorOutputInfo;
import io.github.xiaolei.transaction.viewmodel.TransactionType;

/**
 * TODO: add comment
 */
public class AccountView extends RelativeLayout {
    protected static final String TAG = AccountView.class.getSimpleName();
    private ViewHolder mViewHolder;

    public AccountView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize(context, attrs);
    }

    public AccountView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.initialize(context, attrs);
    }

    public AccountView(Context context) {
        super(context);
        this.initialize(context, null);
    }

    protected void initialize(Context context, AttributeSet attrs) {
        View view = View.inflate(context, R.layout.view_account, this);
        mViewHolder = new ViewHolder(view);

    }

    public void bind(Account account) {
        if (account == null) {
            return;
        }

        mViewHolder.textViewUserName.setText(account.getDisplayName());
    }

    private class ViewHolder {
        public CircleImageView profileImage;
        public TextView textViewUserName;

        public ViewHolder(View view) {
            profileImage = (CircleImageView) view.findViewById(R.id.profileImage);
            textViewUserName = (TextView) view.findViewById(R.id.textViewUserName);
        }
    }
}