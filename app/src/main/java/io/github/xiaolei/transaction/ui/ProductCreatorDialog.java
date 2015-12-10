package io.github.xiaolei.transaction.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import java.sql.SQLException;

import de.greenrobot.event.EventBus;
import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.entity.Product;
import io.github.xiaolei.transaction.event.ProductSelectedEvent;
import io.github.xiaolei.transaction.event.RefreshProductListEvent;
import io.github.xiaolei.transaction.repository.ProductRepository;
import io.github.xiaolei.transaction.repository.RepositoryProvider;

/**
 * TODO: add comment
 */
public class ProductCreatorDialog extends DialogFragment {
    private static final String TAG = ProductCreatorDialog.class.getSimpleName();
    private ViewHolder mViewHolder;
    private Product mCurrentProduct;
    private String mTypedProductName;

    public ProductCreatorDialog() {

    }

    public static ProductCreatorDialog newInstance() {
        ProductCreatorDialog fragment = new ProductCreatorDialog();
        return fragment;
    }

    public static void showDialog(FragmentManager fragmentManager) {
        ProductCreatorDialog fragment = newInstance();
        fragment.show(fragmentManager, TAG);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.dialog_fragment_quick_product_creator, null);
        mViewHolder = new ViewHolder(view);
        mViewHolder.editTextProductName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mCurrentProduct = (Product) adapterView.getItemAtPosition(i);
                if (mCurrentProduct != null) {
                    mViewHolder.editTextProductName.dismissDropDown();
                }
            }
        });
        mViewHolder.editTextProductName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTypedProductName = s.toString().trim();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mViewHolder.editTextProductName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    chooseProduct();
                    dismiss();
                }
                return false;
            }
        });

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.create_product)
                .setView(view)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                chooseProduct();
                            }
                        }
                )
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        }
                )
                .create();

        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    private void chooseProduct() {
        if (TextUtils.isEmpty(mTypedProductName)) {
            return;
        }

        // Notify choose a product
        if (mCurrentProduct != null && mCurrentProduct.getName().equals(mTypedProductName)) {
            EventBus.getDefault().post(new ProductSelectedEvent(mCurrentProduct));
            return;
        }

        // Create new product
        if (mCurrentProduct == null || (mCurrentProduct != null && !mCurrentProduct.getName().equals(mTypedProductName))) {
            final ProgressDialog dialog = ProgressDialog.show(getActivity(), "", getString(R.string.loading_msg_creating_product));
            AsyncTask<Void, Void, Product> task = new AsyncTask<Void, Void, Product>() {

                @Override
                protected Product doInBackground(Void... params) {
                    try {
                        mCurrentProduct = RepositoryProvider.getInstance(getActivity()).resolve(ProductRepository.class).createOrGetProductByName(mTypedProductName);
                        return mCurrentProduct;
                    } catch (SQLException e) {
                        Log.e(TAG, e.toString());
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(Product result) {
                    dialog.dismiss();
                    if (result != null) {
                        EventBus.getDefault().post(new RefreshProductListEvent());
                        EventBus.getDefault().post(new ProductSelectedEvent(result));
                    }
                }
            };
            task.execute();
        }
    }

    private class ViewHolder {
        public AutoCompleteTextView editTextProductName;

        public ViewHolder(View view) {
            editTextProductName = (AutoCompleteTextView) view.findViewById(R.id.editTextProductName);
        }
    }
}
