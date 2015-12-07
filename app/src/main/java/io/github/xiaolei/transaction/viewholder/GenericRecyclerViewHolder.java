package io.github.xiaolei.transaction.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import io.github.xiaolei.transaction.adapter.GenericRecyclerViewAdapter;

/**
 * TODO: add comment
 */
public abstract class GenericRecyclerViewHolder extends RecyclerView.ViewHolder {
    protected GenericRecyclerViewAdapter.OnRecyclerViewItemClickListener itemClickListener;

    public GenericRecyclerViewHolder(View itemView) {
        super(itemView);
    }

    public GenericRecyclerViewHolder(View itemView, final GenericRecyclerViewAdapter.OnRecyclerViewItemClickListener listener) {
        super(itemView);
        itemClickListener = listener;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemClickListener != null) {
                    itemClickListener.onRecyclerViewItemClick(getAdapterPosition());
                }
            }
        });
    }

    public void setOnItemClickListener(GenericRecyclerViewAdapter.OnRecyclerViewItemClickListener listener) {
        itemClickListener = listener;
    }
}
