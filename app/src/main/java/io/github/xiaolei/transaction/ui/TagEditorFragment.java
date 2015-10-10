package io.github.xiaolei.transaction.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.sql.SQLException;

import io.github.xiaolei.transaction.GlobalApplication;
import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.entity.Tag;
import io.github.xiaolei.transaction.repository.RepositoryProvider;
import io.github.xiaolei.transaction.repository.TagRepository;
import io.github.xiaolei.transaction.widget.DataContainerView;

/**
 * TODO: add comment
 */
public class TagEditorFragment extends BaseEditorFragment {
    public static final String ARG_TAG_ID = "tag_id";
    private ViewHolder mViewHolder;
    private long mTagId;
    private Tag mTag;

    public TagEditorFragment() {
        this.setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mTagId = savedInstanceState.getInt(ARG_TAG_ID);
        }

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tag_editor, container, false);
        mViewHolder = new ViewHolder(view);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.product_editor_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save_product:
                validateThenSave();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void readArguments(Bundle args) {
        mTagId = args.getLong(ARG_TAG_ID, -1);
    }

    @Override
    public void load(long id) throws SQLException {
        mTagId = id;
        if (mTagId > 0) {
            mTag = RepositoryProvider.getInstance(getActivity()).resolve(TagRepository.class).getDataAccessObject(Tag.class).queryForId(mTagId);
        } else {
            mTag = new Tag("", GlobalApplication.getCurrentAccount());
        }
    }

    @Override
    public void bind() {
        if (mTag == null) {
            return;
        }

        mViewHolder.editTextTagName.setText(mTag.getName());
    }

    @Override
    protected void preSave() {
        mTag.setName(mViewHolder.editTextTagName.getText().toString());
    }

    @Override
    public void save() throws Exception {
        RepositoryProvider.getInstance(getActivity()).resolve(TagRepository.class).save(mTag);
    }

    @Override
    protected long getEntityId() {
        return mTagId;
    }

    @Override
    protected void onSaveCompleted(boolean success, Exception error) {
        if (success) {
            getFragmentSwitcher().switchToTagList();
        } else {
            Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected Object getViewHolder() {
        return mViewHolder;
    }

    @Override
    public void switchToBusyView() {
        mViewHolder.dataContainerView.switchToBusyView();
    }

    @Override
    public void switchToRetryView() {
        mViewHolder.dataContainerView.switchToRetryView();
    }

    @Override
    public void switchToDataView() {
        mViewHolder.dataContainerView.switchToDataView();
    }

    @Override
    public String getActionBarTitle() {
        return getString(R.string.tag_editor);
    }

    private class ViewHolder {
        public DataContainerView dataContainerView;

        @NotEmpty(trim = true, messageResId = R.string.error_tag_name_empty)
        public EditText editTextTagName;

        public ViewHolder(View view) {
            dataContainerView = (DataContainerView) view.findViewById(R.id.dataContainerViewTagEditor);
            editTextTagName = (EditText) view.findViewById(R.id.editTextTagName);
        }
    }
}
