package io.github.xiaolei.transaction.ui;


import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
public class TagsFragment extends BaseDataFragment implements AdapterView.OnItemClickListener, SearchBoxView.OnSearchListener {
    private static final String TAG = TagsFragment.class.getSimpleName();
    private ViewHolder mViewHolder;
    private GenericEndlessAdapter<Tag> mAdapter;
    private String mSearchKeywords;

    public static TagsFragment newInstance() {
        TagsFragment fragment = new TagsFragment();
        return fragment;
    }

    public TagsFragment() {
        this.setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.tags_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new_tag:
                getFragmentSwitcher().switchToTagEditor(-1);
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tags, container, false);
        mViewHolder = new ViewHolder(view);
        mViewHolder.gridViewTags.setOnItemClickListener(this);
        mViewHolder.searchBoxForTags.setOnSearchListener(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadData(mViewHolder.searchBoxForTags.getKeywords(), false);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    public void loadData(final String keywords, boolean resetKeywords) {
        switchToBusyView();
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
            switchToDataView();
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
    public String getActionBarTitle() {
        return getString(R.string.tags);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
        this.getFragmentSwitcher().switchToTagEditor(id);
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
