package io.github.xiaolei.transaction.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.google.gson.Gson;

import java.sql.SQLException;

import de.greenrobot.event.EventBus;
import io.github.xiaolei.enterpriselibrary.utility.AlertDialogButton;
import io.github.xiaolei.enterpriselibrary.utility.DialogHelper;
import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.common.ValidationException;
import io.github.xiaolei.transaction.entity.Product;
import io.github.xiaolei.transaction.event.ProductSelectedEvent;
import io.github.xiaolei.transaction.event.RefreshProductListEvent;
import io.github.xiaolei.transaction.repository.ProductRepository;
import io.github.xiaolei.transaction.repository.RepositoryProvider;

/**
 * Renames product dialog
 */
public class ProductRenameDialog extends DialogFragment {
    public static final String TAG = ProductRenameDialog.class.getSimpleName();
    public static final String ARG_PRODUCT = "arg_product";

    private Context mContext;
    private ViewHolder mViewHolder;

    private Product mProduct;

    public static void showDialog(Context context, FragmentManager fragmentManager, Product product) {
        ProductRenameDialog fragment = new ProductRenameDialog();
        fragment.mContext = context;

        if (product != null) {
            Bundle args = new Bundle();
            args.putString(ARG_PRODUCT, new Gson().toJson(product));
            fragment.setArguments(args);
        }

        fragment.show(fragmentManager, TAG);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            String value = args.getString(ARG_PRODUCT);
            if (!TextUtils.isEmpty(value)) {
                mProduct = new Gson().fromJson(value, Product.class);
            }
        }

        View view = View.inflate(getActivity(), R.layout.dialog_fragment_product_rename, null);
        mViewHolder = new ViewHolder(view);
        mViewHolder.editTextProductName.setText(mProduct.getName());

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.rename)
                .setView(view)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                renameAsync(mViewHolder.editTextProductName.getText().toString());
                            }
                        }
                )
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }
                )
                .create();

        return dialog;
    }

    private void renameAsync(String newName) {
        if (TextUtils.isEmpty(newName)) {
            return;
        }

        AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... params) {
                String productName = params[0];
                String errorMessage = null;
                try {
                    mProduct = RepositoryProvider.getInstance(getContext())
                            .resolve(ProductRepository.class).rename(mProduct, productName);
                } catch (SQLException e) {
                    e.printStackTrace();
                    errorMessage = getContext().getString(R.string.fail_rename_product);
                } catch (ValidationException e) {
                    e.printStackTrace();
                    errorMessage = e.getMessage();
                }

                return errorMessage;
            }

            @Override
            protected void onPostExecute(String errorMessage) {
                if (TextUtils.isEmpty(errorMessage)) {
                    EventBus.getDefault().post(new ProductSelectedEvent(mProduct));
                    EventBus.getDefault().post(new RefreshProductListEvent());
                } else {
                    DialogHelper.showAlertDialog(mContext, errorMessage, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AppCompatActivity activity = (AppCompatActivity) mContext;
                            ProductRenameDialog.showDialog(mContext, activity.getSupportFragmentManager(), mProduct);
                        }
                    });
                }
            }
        };

        task.execute(newName);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mViewHolder.editTextProductName.selectAll();
    }

    private class ViewHolder {
        public EditText editTextProductName;

        public ViewHolder(View view) {
            editTextProductName = (EditText) view.findViewById(R.id.editTextProductName);
        }
    }
}
