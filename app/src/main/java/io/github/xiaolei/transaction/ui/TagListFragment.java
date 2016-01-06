package io.github.xiaolei.transaction.ui;


import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.adapter.GenericEndlessAdapter;
import io.github.xiaolei.transaction.adapter.IPaginationDataLoader;
import io.github.xiaolei.transaction.adapter.TagListAdapter;
import io.github.xiaolei.transaction.entity.Tag;
import io.github.xiaolei.transaction.repository.RepositoryProvider;
import io.github.xiaolei.transaction.repository.TagRepository;
import io.github.xiaolei.transaction.util.ConfigurationManager;
import io.github.xiaolei.transaction.widget.DataContainerView;
import io.github.xiaolei.transaction.widget.SearchBoxView;

/**
 * TODO: add comments
 */
public class TagListFragment extends BaseFragment implements AdapterView.OnItemClickListener, SearchBoxView.OnSearchListener {
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
        return R.layout.fragment_tags;
    }


    @Override
    public void initialize(View view) {
        mViewHolder = new ViewHolder(view);
        mViewHolder.gridViewTags.setOnItemClickListener(this);
        mViewHolder.searchBoxForTags.setOnSearchListener(this);
    }

    @Override
    public void load() {
        loadData(mViewHolder.searchBoxForTags.getKeywords(), false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new_tag:

                return true;
            default:
                return false;
        }
    }

    public void loadData(final String keywords, boolean resetKeywords) {
        mViewHolder.dataContainerView.switchToBusyView();
        if (resetKeywords) {
            mViewHolder.searchBoxForTags.reset(keywords);
        }

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

    }

    @Override
    public void onSearch(String keywords) {
        mSearchKeywords = keywords;
        loadData(mSearchKeywords, false);
    }

    private class ViewHolder {
        public SearchBoxView searchBoxForTags;
        public DataContainerView dataContainerView;
        public GridView gridViewTags;

        public ViewHolder(View view) {
            searchBoxForTags = (SearchBoxView) view.findViewById(R.id.searchBoxForTags);
            dataContainerView = (DataContainerView) view.findViewById(R.id.dataContainerViewTags);
            gridViewTags = (GridView) view.findViewById(R.id.gridViewTags);
        }
    }
}
