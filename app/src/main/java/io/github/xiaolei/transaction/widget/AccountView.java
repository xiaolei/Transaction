package io.github.xiaolei.transaction.widget;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;

import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;
import io.github.xiaolei.enterpriselibrary.listener.OnOperationCompletedListener;
import io.github.xiaolei.enterpriselibrary.utility.DialogHelper;
import io.github.xiaolei.enterpriselibrary.utility.PhotoPicker;
import io.github.xiaolei.enterpriselibrary.utility.ViewHelper;
import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.entity.Account;
import io.github.xiaolei.transaction.repository.AccountRepository;
import io.github.xiaolei.transaction.repository.RepositoryProvider;
import io.github.xiaolei.transaction.util.ImageLoader;

/**
 * TODO: add comment
 */
public class AccountView extends RelativeLayout {
    protected static final String TAG = AccountView.class.getSimpleName();
    private ViewHolder mViewHolder;
    private Account mAccount;
    private Context mContext;

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
        mContext = getContext();
        mViewHolder = new ViewHolder(view);
        mViewHolder.textViewUserName.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogHelper.showInputDialog(getContext(),
                        getContext().getString(R.string.input_new_account_name),
                        mAccount != null ? mAccount.getDisplayName() : "",
                        new OnOperationCompletedListener<String>() {
                            @Override
                            public void onOperationCompleted(boolean success, String result, String message) {
                                rename(result.trim());
                            }
                        });
            }
        });

        mViewHolder.profileImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoPicker.getInstance(getContext()).showPhotoPickerDialog(ViewHelper.getActivity(getContext()));
            }
        });
    }

    public void bind(Account account) {
        if (account == null) {
            return;
        }

        mAccount = account;
        mViewHolder.textViewUserName.setText(account.getDisplayName());
        if (account.getPhoto() != null) {
            ImageLoader.loadImage(getContext(), account.getPhoto().getUrl(), mViewHolder.profileImage, ImageLoader.PhotoScaleMode.CENTER_INSIDE);
        }
    }

    private void rename(final String displayName) {
        if (TextUtils.isEmpty(displayName) ||
                TextUtils.equals(displayName, mAccount.getDisplayName())) {
            return;
        }

        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    mAccount = RepositoryProvider.getInstance(mContext).resolve(AccountRepository.class)
                            .changeDisplayName(mAccount.getId(), displayName);
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (!success) {
                    Toast.makeText(mContext, getContext().getString(R.string.error_failed_to_update_account_display_name), Toast.LENGTH_SHORT).show();
                    return;
                }

                bind(mAccount);
            }
        };
        task.execute();
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