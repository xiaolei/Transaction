package io.github.xiaolei.transaction.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import io.github.xiaolei.transaction.adapter.GenericRecyclerViewAdapter;

/**
 * TODO: add comment
 */
public abstract class GenericRecyclerViewHolder extends RecyclerView.ViewHolder {
    protected GenericRecyclerViewAdapter.OnRecyclerViewItemLongClickListener mOnLongClickListener;
    protected GenericRecyclerViewAdapter.OnRecyclerViewItemClickListener mOnItemClickListener;

    public GenericRecyclerViewHolder(View itemView) {
        super(itemView);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onRecyclerViewItemClick(getAdapterPosition());
                }
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnLongClickListener != null) {
                    mOnLongClickListener.onRecyclerViewItemLongClick(getAdapterPosition());
                }
                return true;
            }
        });
    }

    public void setOnItemLongClickListener(GenericRecyclerViewAdapter.OnRecyclerViewItemLongClickListener onLongClickListener) {
        mOnLongClickListener = onLongClickListener;
    }

    public void setOnItemClickListener(GenericRecyclerViewAdapter.OnRecyclerViewItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}
