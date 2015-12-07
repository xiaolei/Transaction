package io.github.xiaolei.transaction.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;

import de.greenrobot.event.EventBus;
import io.github.xiaolei.enterpriselibrary.utility.DateTimeUtils;
import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.entity.Transaction;
import io.github.xiaolei.transaction.event.RefreshTransactionListEvent;
import io.github.xiaolei.transaction.repository.RepositoryProvider;
import io.github.xiaolei.transaction.repository.TransactionRepository;
import io.github.xiaolei.transaction.widget.DataContainerView;

/**
 * TODO: add comment
 */
public class TransactionEditorActivity extends BaseActivity {
    public static final String ARG_TRANSACTION_ID = "arg_transaction_id";
    private ViewHolder mViewHolder;
    private long mTransactionId;
    private boolean mIsModified = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_editor);
        initialize();

        handleIntent(getIntent());
    }

    protected void initialize() {
        mViewHolder = new ViewHolder(this);
        setupToolbar(R.drawable.ic_arrow_back_white_24dp, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mViewHolder.editTextTransactionDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mIsModified = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    protected void loadData() {
        mViewHolder.dataContainerViewTransactionEditor.switchToBusyView();
        AsyncTask<Void, Void, Transaction> task = new AsyncTask<Void, Void, Transaction>() {

            @Override
            protected Transaction doInBackground(Void... params) {
                try {
                    return RepositoryProvider.getInstance(TransactionEditorActivity.this)
                            .resolve(TransactionRepository.class)
                            .getTransactionById(mTransactionId);
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Transaction transaction) {
                if (transaction == null) {
                    mViewHolder.dataContainerViewTransactionEditor.switchToRetryView();
                    return;
                }

                bindData(transaction);
            }
        };
        task.execute();
    }

    protected void bindData(Transaction transaction) {
        mViewHolder.textViewProductName.setText(transaction.getProduct().getName());
        mViewHolder.editTextTransactionDescription.setText(transaction.getDescription());
        mViewHolder.textViewCreationTime.setText(DateTimeUtils.formatDateTime(transaction.getCreationTime()));

        mViewHolder.dataContainerViewTransactionEditor.switchToDataView();
        mIsModified = false;
    }

    private void save() {
        if (!mIsModified) {
            return;
        }

        final String description = mViewHolder.editTextTransactionDescription.getText().toString();
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    RepositoryProvider.getInstance(TransactionEditorActivity.this)
                            .resolve(TransactionRepository.class)
                            .updateTransactionDescription(mTransactionId, description);
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }

                return true;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (!result) {
                    Toast.makeText(TransactionEditorActivity.this, getString(R.string.msg_update_transaction_failed), Toast.LENGTH_SHORT).show();
                } else {
                    EventBus.getDefault().post(new RefreshTransactionListEvent());
                }
            }
        };
        task.execute();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        save();
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        Bundle args = intent.getExtras();
        if (args != null) {
            mTransactionId = args.getLong(ARG_TRANSACTION_ID, -1);
        }

        loadData();
    }

    private class ViewHolder {
        public DataContainerView dataContainerViewTransactionEditor;
        public TextView textViewCreationTime;
        public TextView textViewProductName;
        public EditText editTextTransactionDescription;

        public ViewHolder(Activity activity) {
            dataContainerViewTransactionEditor = (DataContainerView) activity.findViewById(R.id.dataContainerViewTransactionEditor);
            editTextTransactionDescription = (EditText) activity.findViewById(R.id.editTextTransactionDescription);
            textViewCreationTime = (TextView) activity.findViewById(R.id.textViewCreationTime);
            textViewProductName = (TextView) activity.findViewById(R.id.textViewProductName);
        }
    }
}
