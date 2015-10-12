package io.github.xiaolei.transaction.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import io.github.xiaolei.transaction.adapter.GenericListAdapter;

/**
 * TODO: add comment
 */
public abstract class BaseViewHolder extends RecyclerView.ViewHolder {
    public BaseViewHolder(View itemView, final GenericListAdapter.OnRecyclerViewItemClickListener listener) {
        super(itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onRecyclerViewItemClick(getAdapterPosition());
                }
            }
        });
    }


}
