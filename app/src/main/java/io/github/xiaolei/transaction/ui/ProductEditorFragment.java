package io.github.xiaolei.transaction.ui;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import io.github.xiaolei.enterpriselibrary.utility.DialogHelper;
import io.github.xiaolei.transaction.GlobalApplication;
import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.entity.Product;
import io.github.xiaolei.transaction.entity.Tag;
import io.github.xiaolei.transaction.event.NewProductCreatedEvent;
import io.github.xiaolei.transaction.event.RefreshProductListEvent;
import io.github.xiaolei.transaction.repository.ProductRepository;
import io.github.xiaolei.transaction.repository.RepositoryProvider;
import io.github.xiaolei.transaction.widget.DataContainerView;
import io.github.xiaolei.transaction.widget.TagsView;

public class ProductEditorFragment extends BaseEditorFragment {
    public static final String ARG_PRODUCT_ID = "product_id";
    public static final String TAG = ProductEditorFragment.class.getSimpleName();

    private long mProductId;
    private Product mProduct;
    private List<Tag> mTags;
    private ViewHolder mViewHolder;
    private OnProductEditorFragmentInteractionListener mListener;
    private TextWatcher mTextWatcher;

    public static ProductEditorFragment newInstance(long productId) {
        ProductEditorFragment fragment = new ProductEditorFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PRODUCT_ID, productId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    protected void readArguments(Bundle args) {
        mProductId = getArguments().getLong(ARG_PRODUCT_ID);
    }

    public void setProductId(long productId) {
        mProductId = productId;
    }

    @Override
    protected void load(long id) throws Exception {
        mProductId = id;
        if (mProductId > 0) {
            ProductRepository productRepository = RepositoryProvider.getInstance(getActivity()).resolve(ProductRepository.class);
            mProduct = productRepository.getDataAccessObject(Product.class).queryForId(mProductId);
            mTags = productRepository.getProductTags(mProductId);
        } else {
            mProduct = new Product();
            mProduct.setAccountId(GlobalApplication.getCurrentAccountId());
            mTags = new ArrayList<Tag>();
        }
    }

    @Override
    protected void bind() {
        showProductDetail(mProduct, mTags);
    }

    @Override
    protected void preSave() {
        mProduct.setName(mViewHolder.editTextProductName.getText().toString());
        mTags = getTags();
    }

    @Override
    protected void save() throws Exception {
        RepositoryProvider.getInstance(getActivity()).resolve(ProductRepository.class).save(mProduct, mTags);
    }

    @Override
    protected long getEntityId() {
        return mProductId;
    }

    @Override
    protected void onSaveCompleted(boolean success, Exception error) {
        if (success) {
            EventBus.getDefault().post(new RefreshProductListEvent());
            EventBus.getDefault().post(new NewProductCreatedEvent(mProduct));
            getActivity().finish();
        } else {
            Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected Object getViewHolder() {
        return mViewHolder;
    }

    public ProductEditorFragment() {
        this.setHasOptionsMenu(true);
    }

    @Override
    public void initialize(View view) {
        mViewHolder = new ViewHolder(view);
        mViewHolder.editTextProductName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Product product = (Product) adapterView.getItemAtPosition(i);
                if (product != null) {
                    mViewHolder.editTextProductName.dismissDropDown();

                    mProductId = product.getId();
                    mProduct = product;
                    loadAsync(mProductId);
                }
            }
        });

        mTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                showAutoCompleteProducts(editable.toString());
            }
        };
    }

    private void showAutoCompleteProducts(String productNameKeywords) {
        AsyncTask<String, Void, List<Product>> task = new AsyncTask<String, Void, List<Product>>() {
            @Override
            protected List<Product> doInBackground(String... keywords) {
                List<Product> result = null;
                try {
                    result = RepositoryProvider.getInstance(getActivity()).resolve(ProductRepository.class).query(keywords[0], 0, 3);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                return result;
            }

            @Override
            protected void onPostExecute(List<Product> result) {
                ArrayAdapter<Product> adapter = null;
                if (result != null && result.size() > 0) {
                    adapter = new ArrayAdapter<Product>(getActivity(), android.R.layout.simple_list_item_1, result);
                }

                mViewHolder.editTextProductName.setAdapter(adapter);
            }
        };
        task.execute(productNameKeywords);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putLong(ARG_PRODUCT_ID, mProductId);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_product_editor, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save_product:
                validateThenSave();
                return true;
            case R.id.action_delete_product:
                DialogHelper.showConfirmDialog(getActivity(), getString(R.string.msg_confirm_remove_product), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeProduct(mProductId);
                    }
                });

                return true;
            default:
                return false;
        }
    }

    private void removeProduct(long productId) {
        AsyncTask<Long, Void, Boolean> task = new AsyncTask<Long, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Long... params) {
                long id = params[0];
                try {
                    RepositoryProvider.getInstance(getActivity()).resolve(ProductRepository.class)
                            .remove(id);
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }

                return true;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (!isAdded()) {
                    return;
                }

                EventBus.getDefault().post(new RefreshProductListEvent());
                getActivity().finish();
            }
        };

        task.execute(productId);
    }

    private List<Tag> getTags() {
        List<Tag> result = mViewHolder.productTagsView.getCheckedTags();
        String inputTags = mViewHolder.editTextProductTags.getText().toString().trim();
        if (!TextUtils.isEmpty(inputTags)) {
            String[] tagNames = inputTags.split(" ");
            if (tagNames.length > 0) {
                for (String name : tagNames) {
                    Tag tag = new Tag();
                    tag.setAccountId(GlobalApplication.getCurrentAccountId());
                    tag.setName(name);

                    result.add(tag);
                }
            }
        }

        return result;
    }

    private void showProductDetail(Product product, List<Tag> tags) {
        if (mViewHolder == null || product == null) {
            return;
        }

        mViewHolder.editTextProductName.removeTextChangedListener(mTextWatcher);
        mViewHolder.editTextProductName.setAdapter(null);
        mViewHolder.editTextProductName.dismissDropDown();

        if (product.getId() > 0) {
            mViewHolder.editTextProductName.setText(product.getName());
        } else {
            mViewHolder.editTextProductName.setText("");
            mViewHolder.editTextProductTags.setText("");
        }

        mViewHolder.imageViewProductPrimaryPhoto.setBackgroundColor(mProduct.getBannerColor());
        mViewHolder.productTagsView.bind(tags);
        mViewHolder.editTextProductName.addTextChangedListener(mTextWatcher);
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_product_editor;
    }

    @Override
    public void load() {

    }

    @Override
    public int getActionBarTitle() {
        return R.string.product_editor;
    }

    public interface OnProductEditorFragmentInteractionListener {
        public void onProductSaveSuccess(Product product);
    }

    public class ViewHolder {
        public DataContainerView dataContainerView;

        @NotEmpty(trim = true, messageResId = R.string.error_product_name_empty)
        public AutoCompleteTextView editTextProductName;

        public EditText editTextProductTags;
        public TagsView productTagsView;
        public ImageView imageViewProductPrimaryPhoto;

        public ViewHolder(View view) {
            dataContainerView = (DataContainerView) view.findViewById(R.id.dataContainerViewProductEditor);
            editTextProductName = (AutoCompleteTextView) view.findViewById(R.id.editTextProductName);
            editTextProductTags = (EditText) view.findViewById(R.id.editTextProductTags);
            productTagsView = (TagsView) view.findViewById(R.id.productTagsView);
            imageViewProductPrimaryPhoto = (ImageView) view.findViewById(R.id.imageViewProductPrimaryPhoto);
        }
    }
}
