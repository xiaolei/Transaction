package io.github.xiaolei.transaction.ui;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;
import io.github.xiaolei.enterpriselibrary.logging.Logger;
import io.github.xiaolei.enterpriselibrary.utility.DateTimeUtils;
import io.github.xiaolei.enterpriselibrary.utility.DialogHelper;
import io.github.xiaolei.transaction.GlobalApplication;
import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.adapter.GenericRecyclerViewAdapter;
import io.github.xiaolei.transaction.adapter.TransactionListRecyclerViewAdapter;
import io.github.xiaolei.transaction.entity.Transaction;
import io.github.xiaolei.transaction.event.FinishActionMode;
import io.github.xiaolei.transaction.event.RefreshTransactionListEvent;
import io.github.xiaolei.transaction.listener.OnLoadMoreListener;
import io.github.xiaolei.transaction.repository.RepositoryProvider;
import io.github.xiaolei.transaction.repository.TransactionRepository;
import io.github.xiaolei.transaction.util.ActivityHelper;
import io.github.xiaolei.transaction.util.ConfigurationManager;
import io.github.xiaolei.transaction.viewmodel.LoadMoreReturnInfo;
import io.github.xiaolei.transaction.widget.DataContainerView;

/**
 * Transaction list fragment
 */
public class TransactionListFragment extends BaseFragment implements OnLoadMoreListener<Transaction>, GenericRecyclerViewAdapter.OnRecyclerViewItemClickListener, GenericRecyclerViewAdapter.OnRecyclerViewItemLongClickListener {
    public static final String TAG = TransactionListFragment.class.getSimpleName();
    public static final String ARG_TRANSACTION_START_DATE = "arg_transaction_start_date";
    public static final String ARG_TRANSACTION_END_DATE = "arg_transaction_end_date";

    private TransactionListRecyclerViewAdapter mAdapter;
    private ViewHolder mViewHolder;
    private Date mStartDate;
    private Date mEndDate;

    private MultiChoiceModeListener mActionModeCallback = new MultiChoiceModeListener();
    private android.view.ActionMode mActionMode;

    public TransactionListFragment() {

    }

    public static TransactionListFragment newInstance(Date startDate, Date endDate) {
        TransactionListFragment fragment = new TransactionListFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_TRANSACTION_START_DATE, DateTimeUtils.getStartTimeOfDate(startDate).getTime());
        args.putLong(ARG_TRANSACTION_END_DATE, DateTimeUtils.getEndTimeOfDate(endDate).getTime());
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void initialize(View view) {
        mViewHolder = new ViewHolder(view);

        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mViewHolder.recyclerViewTransactions.setLayoutManager(layoutManager);
        mViewHolder.recyclerViewTransactions.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        Bundle args = getArguments();
        if (args != null) {
            mStartDate = new Date(args.getLong(ARG_TRANSACTION_START_DATE, DateTimeUtils.getStartTimeOfDate(new Date()).getTime()));
            mEndDate = new Date(args.getLong(ARG_TRANSACTION_END_DATE, DateTimeUtils.getEndTimeOfDate(new Date()).getTime()));
        }
    }

    public void load(Date startDate, Date endDate) {
        mStartDate = DateTimeUtils.getStartTimeOfDate(startDate);
        mEndDate = DateTimeUtils.getEndTimeOfDate(endDate);

        load();
    }

    public Date getStartDate() {
        return mStartDate;
    }

    public Date getEndDate() {
        return mEndDate;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showDateRange(mStartDate, mEndDate);
    }

    private void showDateRange(Date startDate, Date endDate) {
        String dateRange = "";
        if (DateTimeUtils.isDateEqualsIgnoreTime(startDate, endDate)) {
            if (!DateUtils.isToday(startDate.getTime())) {
                dateRange = DateUtils.formatDateTime(getContext(), startDate.getTime(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_WEEKDAY);
            } else {
                dateRange = getString(R.string.today);
            }
        } else {
            dateRange = String.format("%s ~ %s",
                    DateUtils.formatDateTime(getContext(), startDate.getTime(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_WEEKDAY),
                    DateUtils.formatDateTime(getContext(), endDate.getTime(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_WEEKDAY));
        }

        mViewHolder.textViewTransactionDateRange.setText(dateRange);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_transactions;
    }

    @Override
    public int getActionBarTitle() {
        return R.string.transactions;
    }

    private List<Transaction> query(Date startDate, Date endDate, long offset,
                                    long limit) {
        List<Transaction> result = null;

        try {
            result = RepositoryProvider.getInstance(getActivity()).resolve(TransactionRepository.class)
                    .query(GlobalApplication.getCurrentAccount().getId(),
                            startDate, endDate, offset, limit);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public void load() {
        if (mActionMode != null) {
            mActionMode.finish();
        }

        showDateRange(mStartDate, mEndDate);
        mViewHolder.dataContainerViewTransactions.switchToBusyView();
        AsyncTask<Void, Void, List<Transaction>> task = new AsyncTask<Void, Void, List<Transaction>>() {

            @Override
            protected List<Transaction> doInBackground(Void... voids) {
                List<Transaction> result = query(mStartDate, mEndDate, 0,
                        ConfigurationManager.DEFAULT_PAGE_SIZE);
                return result;
            }

            @Override
            protected void onPostExecute(List<Transaction> result) {
                if (!isAdded()) {
                    return;
                }

                if (result != null && result.size() > 0) {
                    mViewHolder.dataContainerViewTransactions.switchToDataView();

                    if (mAdapter == null) {
                        mAdapter = new TransactionListRecyclerViewAdapter(mViewHolder.recyclerViewTransactions, result, TransactionListFragment.this);
                        mAdapter.setOnItemClickListener(TransactionListFragment.this);
                        mAdapter.setOnItemLongClickListener(TransactionListFragment.this);
                        mViewHolder.recyclerViewTransactions.setAdapter(mAdapter);
                    } else {
                        mAdapter.swap(result);
                    }
                } else {
                    mViewHolder.dataContainerViewTransactions.switchToEmptyView(getString(R.string.no_transaction));
                }
            }
        };
        task.execute();
    }

    public void onEvent(RefreshTransactionListEvent event) {
        load();
    }

    public void onEvent(FinishActionMode event) {
        finishActionModeIfNeeded();
    }

    @Override
    public void onPause() {
        super.onPause();
        finishActionModeIfNeeded();
    }

    public void finishActionModeIfNeeded() {
        if (mActionMode != null) {
            mActionMode.finish();
        }
    }

    private void removeTransactions(List<Transaction> transactions) {
        if (transactions == null || transactions.size() == 0) {
            return;
        }

        List<Long> transactionIds = new ArrayList<>();
        for (Transaction transaction : transactions) {
            transactionIds.add(transaction.getId());
        }

        AsyncTask<List<Long>, Void, String> task = new AsyncTask<List<Long>, Void, String>() {

            @Override
            protected String doInBackground(List<Long>... params) {
                List<Long> ids = params[0];
                String errorMessage = null;
                try {
                    RepositoryProvider.getInstance(getActivity()).resolve(TransactionRepository.class)
                            .removeTransactions(ids);
                } catch (SQLException e) {
                    e.printStackTrace();
                    errorMessage = e.getMessage();
                }

                return errorMessage;
            }

            @Override
            public void onPostExecute(String errorMessage) {
                load();
            }
        };
        task.execute(transactionIds);
    }

    @Override
    public LoadMoreReturnInfo<Transaction> loadMore(int pageIndex, int offset, int pageSize) {
        List<Transaction> transactions = query(mStartDate, mEndDate, offset, pageSize);
        boolean hasMore = transactions.size() > 0;
        Logger.d(TAG, String.format("hasMore: %s", String.valueOf(hasMore)));

        return new LoadMoreReturnInfo<>(transactions, hasMore);
    }

    @Override
    public void onRecyclerViewItemClick(int position) {
        if (mActionMode != null) {
            Transaction transaction = mAdapter.toggleSelection(position);
            mActionModeCallback.onItemCheckedStateChanged(mActionMode, position, transaction.getId(), transaction.checked);
            return;
        }

        ActivityHelper.startTransactionEditorActivity(getContext(), mAdapter.getItemId(position));
    }

    @Override
    public void onRecyclerViewItemLongClick(int position) {
        if (mActionMode != null) {
            return;
        }

        mAdapter.toggleSelection(position);
        mActionMode = getActivity().startActionMode(mActionModeCallback);
    }

    private class MultiChoiceModeListener implements ListView.MultiChoiceModeListener {
        @Override
        public void onItemCheckedStateChanged(android.view.ActionMode actionMode, int position, long id, boolean checked) {
            int selectCount = mAdapter.getCheckedItemCount();
            Transaction transaction = mAdapter.getItem(position);
            transaction.checked = checked;
            mAdapter.notifyItemChanged(position);

            if (selectCount >= 1) {
                actionMode.setSubtitle(getString(R.string.selected_count_text_format, selectCount));
            } else {
                mActionMode.finish();
            }
        }

        @Override
        public boolean onCreateActionMode(android.view.ActionMode actionMode, Menu menu) {
            mActionMode = actionMode;
            actionMode.getMenuInflater().inflate(R.menu.action_mode_transaction_list, menu);
            actionMode.setTitle(R.string.action_mode_title_select_transactions);
            actionMode.setSubtitle(getString(R.string.selected_count_text_format, 1));

            return true;
        }

        @Override
        public boolean onPrepareActionMode(android.view.ActionMode actionMode, Menu menu) {
            return true;
        }

        @Override
        public boolean onActionItemClicked(android.view.ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_delete_transaction:
                    DialogHelper.showConfirmDialog(getActivity(), getString(R.string.msg_confirm_remove_transactions), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            removeTransactions(mAdapter.getCheckedItems());
                        }
                    });

                    break;
                default:
                    break;
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(android.view.ActionMode actionMode) {
            mActionMode = null;
            mAdapter.uncheckAll();
        }
    }

    private class ViewHolder {
        public RecyclerView recyclerViewTransactions;
        public DataContainerView dataContainerViewTransactions;
        public TextView textViewTransactionDateRange;

        public ViewHolder(View view) {
            recyclerViewTransactions = (RecyclerView) view.findViewById(R.id.recyclerViewTransactions);
            dataContainerViewTransactions = (DataContainerView) view.findViewById(R.id.dataContainerViewTransactions);
            textViewTransactionDateRange = (TextView) view.findViewById(R.id.textViewTransactionDateRange);
        }
    }
}
