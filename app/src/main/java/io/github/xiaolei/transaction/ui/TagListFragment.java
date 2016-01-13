package io.github.xiaolei.transaction.ui;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import io.github.xiaolei.enterpriselibrary.listener.OnOperationCompletedListener;
import io.github.xiaolei.enterpriselibrary.ui.InputDialogFragment;
import io.github.xiaolei.transaction.GlobalApplication;
import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.adapter.GenericEndlessAdapter;
import io.github.xiaolei.transaction.adapter.IPaginationDataLoader;
import io.github.xiaolei.transaction.adapter.TagListAdapter;
import io.github.xiaolei.transaction.entity.Tag;
import io.github.xiaolei.transaction.repository.RepositoryProvider;
import io.github.xiaolei.transaction.repository.TagRepository;
import io.github.xiaolei.transaction.util.ConfigurationManager;
import io.github.xiaolei.transaction.widget.DataContainerView;

/**
 * TODO: add comments
 */
public class TagListFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    public static final String TAG = TagListFragment.class.getSimpleName();
    private ViewHolder mViewHolder;
    private GenericEndlessAdapter<Tag> mAdapter;
    private String mSearchKeywords;

    public static TagListFragment newInstance() {
        TagListFragment fragment = new TagListFragment();
        return fragment;
    }

    public TagListFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_tag_list;
    }


    @Override
    public void initialize(View view) {
        mViewHolder = new ViewHolder(view);
        mViewHolder.gridViewTags.setOnItemClickListener(this);
    }

    @Override
    public void load() {
        loadData(mSearchKeywords);
    }

    private void initializeSearchView(final MenuItem searchMenuItem) {
        if (searchMenuItem == null) {
            return;
        }

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        searchView.setQueryHint(getString(R.string.search_product));

        MenuItemCompat.setOnActionExpandListener(searchMenuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                mSearchKeywords = "";
                load();
                return true;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mSearchKeywords = query;
                load();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mSearchKeywords = newText;
                load();
                return true;
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu.clear();
        inflater.inflate(R.menu.menu_tag_list, menu);

        initializeSearchView(menu.findItem(R.id.action_search_tag));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!isAdded()) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.action_new_tag:
                InputDialogFragment.showDialog(getActivity(), getActivity().getSupportFragmentManager(),
                        getString(R.string.title_new_tag), "", new OnOperationCompletedListener<String>() {
                            @Override
                            public void onOperationCompleted(boolean success, String result, String message) {
                                createTag(result);
                            }
                        });
                return true;
            default:
                return true;
        }
    }

    public void loadData(final String keywords) {
        mViewHolder.dataContainerView.switchToBusyView();

        AsyncTask<Void, Void, List<Tag>> task = new AsyncTask<Void, Void, List<Tag>>() {
            @Override
            protected List<Tag> doInBackground(Void... voids) {
                List<Tag> result = new ArrayList<Tag>();
                try {
                    result = RepositoryProvider.getInstance(getActivity()).resolve(TagRepository.class).query(keywords, 0, ConfigurationManager.DEFAULT_PAGE_SIZE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return result;
            }

            @Override
            protected void onPostExecute(List<Tag> result) {
                showTagList(result);
            }
        };
        task.execute();
    }

    private void showTagList(List<Tag> tags) {
        if (mViewHolder == null) {
            return;
        }

        mAdapter = new GenericEndlessAdapter<Tag>(getActivity(), new TagListAdapter(getActivity(), tags), new IPaginationDataLoader<Tag>() {
            @Override
            public List<Tag> load(int offset, int limit) throws SQLException {
                return RepositoryProvider.getInstance(getActivity()).resolve(TagRepository.class).query(mSearchKeywords, offset, limit);
            }
        });

        mViewHolder.gridViewTags.setAdapter(mAdapter);

        if (tags.size() > 0) {
            mViewHolder.dataContainerView.switchToDataView();
        } else {
            if (!TextUtils.isEmpty(mSearchKeywords)) {
                mViewHolder.dataContainerView.setEmptyViewDisplayText(getString(R.string.no_search_result));
            } else {
                mViewHolder.dataContainerView.setEmptyViewDisplayText(getString(R.string.no_result));
            }

            mViewHolder.dataContainerView.switchToEmptyView();
        }
    }

    @Override
    public int getActionBarTitle() {
        return R.string.tags;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
        final Tag currentTag = (Tag) adapterView.getItemAtPosition(i);
        InputDialogFragment.showDialog(getActivity(), getActivity().getSupportFragmentManager(),
                getString(R.string.title_rename_tag), currentTag.getName(), new OnOperationCompletedListener<String>() {
                    @Override
                    public void onOperationCompleted(boolean success, String result, String message) {
                        renameTag(currentTag.getId(), result);
                    }
                });
    }

    private void renameTag(final long tagId, final String newName) {
        if (TextUtils.isEmpty(newName)) {
            return;
        }

        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    RepositoryProvider.getInstance(getActivity()).resolve(TagRepository.class)
                            .rename(tagId, newName);
                } catch (Exception e) {
                    e.printStackTrace();
                    return e.getMessage();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String errorMessage) {
                if (!TextUtils.isEmpty(errorMessage)) {
                    Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                    return;
                }

                load();
            }
        };
        task.execute();
    }

    private void createTag(final String tagName) {
        if (TextUtils.isEmpty(tagName)) {
            return;
        }

        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    RepositoryProvider.getInstance(getActivity()).resolve(TagRepository.class)
                            .create(tagName, GlobalApplication.getCurrentAccountId());
                } catch (Exception e) {
                    e.printStackTrace();
                    return e.getMessage();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String errorMessage) {
                if (!TextUtils.isEmpty(errorMessage)) {
                    Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                    return;
                }

                load();
            }
        };
        task.execute();
    }

    private class ViewHolder {
        public DataContainerView dataContainerView;
        public GridView gridViewTags;

        public ViewHolder(View view) {
            dataContainerView = (DataContainerView) view.findViewById(R.id.dataContainerViewTags);
            gridViewTags = (GridView) view.findViewById(R.id.gridViewTags);
        }
    }
}
