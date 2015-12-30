package io.github.xiaolei.transaction.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import io.github.xiaolei.enterpriselibrary.listener.OnOperationCompletedListener;
import io.github.xiaolei.enterpriselibrary.logging.Logger;
import io.github.xiaolei.enterpriselibrary.utility.DateTimeUtils;
import io.github.xiaolei.enterpriselibrary.utility.DialogHelper;
import io.github.xiaolei.enterpriselibrary.utility.PhotoPicker;
import io.github.xiaolei.enterpriselibrary.utility.UriHelper;
import io.github.xiaolei.transaction.GlobalApplication;
import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.adapter.ActionButtonListAdapter;
import io.github.xiaolei.transaction.entity.Photo;
import io.github.xiaolei.transaction.entity.Transaction;
import io.github.xiaolei.transaction.entity.TransactionPhoto;
import io.github.xiaolei.transaction.event.PickPhotoEvent;
import io.github.xiaolei.transaction.event.RefreshTransactionListEvent;
import io.github.xiaolei.transaction.listener.OnGotPermissionResultListener;
import io.github.xiaolei.transaction.listener.PermissionResult;
import io.github.xiaolei.transaction.repository.RepositoryProvider;
import io.github.xiaolei.transaction.repository.TransactionPhotoRepository;
import io.github.xiaolei.transaction.repository.TransactionRepository;
import io.github.xiaolei.transaction.util.ActivityHelper;
import io.github.xiaolei.transaction.viewmodel.ActionButtonId;
import io.github.xiaolei.transaction.viewmodel.ActionButtonInfo;
import io.github.xiaolei.transaction.widget.CurrencyTextView;
import io.github.xiaolei.transaction.widget.DataContainerView;
import io.github.xiaolei.transaction.widget.PhotoGalleryView;

/**
 * TODO: add comment
 */
public class TransactionEditorActivity extends BaseActivity {
    public static final String ARG_TRANSACTION_ID = "arg_transaction_id";
    private static final String TAG = TransactionEditorActivity.class.getSimpleName();
    private ViewHolder mViewHolder;
    private long mTransactionId;
    private boolean mIsModified = false;
    private Transaction mTransaction;

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

        mViewHolder.textViewPrice.enableEditMode(true);
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

        mViewHolder.gridViewTransactionEditorActions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ActionButtonInfo actionButtonInfo = (ActionButtonInfo) parent.getItemAtPosition(position);
                switch (actionButtonInfo.id) {
                    case ActionButtonId.PICK_PHOTO_FROM_GALLERY:
                        // PhotoPicker.getInstance(TransactionEditorActivity.this).pickPhotoFromGallery(TransactionEditorActivity.this);
                        PhotoPicker.getInstance(TransactionEditorActivity.this).showPhotoChooserDialog(TransactionEditorActivity.this,
                                TransactionEditorActivity.this.getSupportFragmentManager(),
                                new OnOperationCompletedListener<String>() {
                                    @Override
                                    public void onOperationCompleted(boolean success, String result, String message) {
                                        if (!UriHelper.isValidUrl(result)) {
                                            DialogHelper.showAlertDialog(TransactionEditorActivity.this, getString(R.string.error_invalid_photo_url));
                                            return;
                                        }

                                        onEvent(new PickPhotoEvent(result));
                                    }
                                });
                        break;
                    case ActionButtonId.TAKE_PHOTO:
                        checkCameraPermission(new OnGotPermissionResultListener() {
                            @Override
                            public void onGotPermissionResult(PermissionResult permissionResult) {
                                if (permissionResult.granted) {
                                    PhotoPicker.getInstance(TransactionEditorActivity.this).takePhoto(TransactionEditorActivity.this);
                                } else {
                                    Logger.e(TAG, String.format("Permission: %s is denied.", permissionResult.permission));
                                }
                            }
                        });

                        break;
                    case ActionButtonId.TOGGLE_STAR:
                        toggleStar(!mTransaction.getStar());
                        break;
                    default:
                        break;
                }
            }
        });

        mViewHolder.photoGalleryViewTransactions.setOnPhotoClickListener(new PhotoGalleryView.OnPhotoClickListener() {
            @Override
            public void onPhotoClick(Photo photo, int position) {
                ActivityHelper.startPhotoListActivity(TransactionEditorActivity.this, mTransaction.getTransactionPhotos(), position);
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

                mTransaction = transaction;
                bindData(transaction);
            }
        };
        task.execute();
    }

    protected void bindData(Transaction transaction) {
        setTitle(transaction.getProduct().getName());

        mViewHolder.textViewProductName.setText(transaction.getProduct().getName());
        mViewHolder.textViewPrice.setPrice(transaction.getPrice(), transaction.getCurrencyCode());
        mViewHolder.editTextTransactionDescription.setText(transaction.getDescription());
        mViewHolder.textViewCreationTime.setText(DateTimeUtils.formatDateTime(transaction.getCreationTime()));

        buildActionButtons(transaction);
        bindTransactionPhotos(transaction);

        mViewHolder.dataContainerViewTransactionEditor.switchToDataView();
        mIsModified = false;
    }

    private void bindTransactionPhotos(Transaction transaction) {
        if (transaction == null) {
            return;
        }

        mViewHolder.photoGalleryViewTransactions.bindData(mTransaction.getTransactionPhotos());
    }

    private void buildActionButtons(Transaction transaction) {
        int starDrawable = transaction.getStar() ? R.drawable.bitmap_star_on : R.drawable.ic_favorite_border_white_18dp;

        List<ActionButtonInfo> actionButtons = new ArrayList<>();
        actionButtons.add(new ActionButtonInfo(ActionButtonId.TOGGLE_STAR, null, starDrawable));
        actionButtons.add(new ActionButtonInfo(ActionButtonId.PICK_PHOTO_FROM_GALLERY, null, R.drawable.ic_collections_white_18dp));
        actionButtons.add(new ActionButtonInfo(ActionButtonId.TAKE_PHOTO, null, R.drawable.ic_camera_alt_white_18dp));
        ActionButtonListAdapter actionButtonListAdapter = new ActionButtonListAdapter(this, actionButtons);
        mViewHolder.gridViewTransactionEditorActions.setAdapter(actionButtonListAdapter);
    }

    private void toggleStar(final boolean starOn) {
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    RepositoryProvider.getInstance(TransactionEditorActivity.this).resolve(TransactionRepository.class)
                            .toggleStar(mTransactionId, starOn);
                    return true;
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    mTransaction.setStar(starOn);
                    buildActionButtons(mTransaction);
                } else {
                    DialogHelper.showAlertDialog(TransactionEditorActivity.this, getString(R.string.error_toggle_transaction_star));
                }
            }
        };
        task.execute();
    }

    private void save() {
        mIsModified = mIsModified || mViewHolder.textViewPrice.isModified();

        if (!mIsModified) {
            return;
        }

        final boolean isPriceModified = mViewHolder.textViewPrice.isModified();
        final BigDecimal price = mViewHolder.textViewPrice.getPrice();
        final String currencyCode = mViewHolder.textViewPrice.getCurrencyCode();
        final String description = mViewHolder.editTextTransactionDescription.getText().toString();

        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    TransactionRepository transactionRepository = RepositoryProvider.getInstance(TransactionEditorActivity.this)
                            .resolve(TransactionRepository.class);
                    transactionRepository.updateTransactionDescription(mTransactionId, description);

                    if (isPriceModified) {
                        transactionRepository.updatePrice(mTransactionId, price, currencyCode);
                        Logger.d(TAG, String.format("Update transaction => price: %s, currencyCode: %s", price.toString(), currencyCode));
                    }
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
                    mIsModified = false;
                    EventBus.getDefault().post(new RefreshTransactionListEvent());
                }
            }
        };
        task.execute();
    }


    public void onEvent(final PickPhotoEvent event) {
        if (TextUtils.isEmpty(event.photoFileUri)) {
            return;
        }

        AsyncTask<Void, Void, Transaction> task = new AsyncTask<Void, Void, Transaction>() {

            @Override
            protected Transaction doInBackground(Void... params) {
                try {
                    return RepositoryProvider.getInstance(TransactionEditorActivity.this)
                            .resolve(TransactionPhotoRepository.class)
                            .addPhoto(mTransactionId, event.photoFileUri, "",
                                    GlobalApplication.getCurrentAccount().getId());
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Transaction result) {
                if (result == null) {
                    Toast.makeText(TransactionEditorActivity.this, getString(R.string.error_add_photo), Toast.LENGTH_SHORT).show();
                } else {
                    mTransaction = result;
                    bindTransactionPhotos(result);
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
    public void onPause() {
        super.onPause();
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
        public NestedScrollView nestedScrollViewTransactionEditor;
        public DataContainerView dataContainerViewTransactionEditor;
        public TextView textViewCreationTime;
        public TextView textViewProductName;
        public EditText editTextTransactionDescription;
        public GridView gridViewTransactionEditorActions;
        public PhotoGalleryView photoGalleryViewTransactions;
        public CurrencyTextView textViewPrice;

        public ViewHolder(Activity activity) {
            nestedScrollViewTransactionEditor = (NestedScrollView) activity.findViewById(R.id.nestedScrollViewTransactionEditor);
            dataContainerViewTransactionEditor = (DataContainerView) activity.findViewById(R.id.dataContainerViewTransactionEditor);
            editTextTransactionDescription = (EditText) activity.findViewById(R.id.editTextTransactionDescription);
            textViewCreationTime = (TextView) activity.findViewById(R.id.textViewCreationTime);
            textViewProductName = (TextView) activity.findViewById(R.id.textViewProductName);
            gridViewTransactionEditorActions = (GridView) activity.findViewById(R.id.gridViewTransactionEditorActions);
            photoGalleryViewTransactions = (PhotoGalleryView) activity.findViewById(R.id.photoGalleryViewTransactions);
            textViewPrice = (CurrencyTextView) activity.findViewById(R.id.textViewPrice);
        }
    }
}
