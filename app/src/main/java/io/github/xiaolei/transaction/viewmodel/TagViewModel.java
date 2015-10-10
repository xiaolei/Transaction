package io.github.xiaolei.transaction.viewmodel;

import android.view.View;

import io.github.xiaolei.transaction.entity.Tag;

/**
 * TODO: add comment
 */
public class TagViewModel {
    public Tag tag;
    public View view;
    public boolean checked;

    public TagViewModel() {
    }

    public TagViewModel(Tag tag, View view, boolean checked) {
        this.tag = tag;
        this.view = view;
        this.checked = checked;
    }
}
