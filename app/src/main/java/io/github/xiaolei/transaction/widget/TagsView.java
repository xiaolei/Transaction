package io.github.xiaolei.transaction.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wefika.flowlayout.FlowLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.github.xiaolei.transaction.GlobalApplication;
import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.adapter.ProductTagsAdapter;
import io.github.xiaolei.transaction.entity.Tag;
import io.github.xiaolei.transaction.viewmodel.TagViewModel;

/**
 * TODO: add comment
 */
public class TagsView extends RelativeLayout {
    protected static final String TAG = TagsView.class.getSimpleName();
    private ViewHolder mViewHolder;
    private List<Tag> mTags;
    private List<TagViewModel> mTagViewModels;

    public TagsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize(context, attrs);
    }

    public TagsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.initialize(context, attrs);
    }

    public TagsView(Context context) {
        super(context);
        this.initialize(context, null);
    }

    protected void initialize(Context context, AttributeSet attrs) {
        View view = View.inflate(context, R.layout.view_tags, this);
        mViewHolder = new ViewHolder(view);
    }

    public void bind(List<Tag> tags) {
        mTagViewModels = new ArrayList<TagViewModel>();
        mTags = tags != null ? tags : new ArrayList<Tag>();
        mViewHolder.tagsViewContainer.removeAllViews();

        for (Tag tag : mTags) {
            View view = createView(tag);
        }
    }

    private View createView(Tag tag) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_item_product_tag, mViewHolder.tagsViewContainer, false);
        TagViewModel tagViewModel = new TagViewModel(tag, view, true);
        mViewHolder.tagsViewContainer.addView(view);
        mTagViewModels.add(tagViewModel);
        initTagView(tagViewModel, view);

        return view;
    }

    private void initTagView(TagViewModel tagViewModel, View view) {
        CheckedTextView tv = (CheckedTextView) view.findViewById(R.id.textViewProductTag);
        tv.setTag(tagViewModel);
        tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view instanceof CheckedTextView) {
                    CheckedTextView checkedTextView = (CheckedTextView) view;
                    checkedTextView.setChecked(!checkedTextView.isChecked());

                    TagViewModel viewModel = (TagViewModel) checkedTextView.getTag();
                    if (viewModel != null) {
                        viewModel.checked = checkedTextView.isChecked();
                    }
                }
            }
        });
        tv.setText(tagViewModel.tag.getName());
    }

    public void remove(String tagName) {
        if (TextUtils.isEmpty(tagName)) {
            return;
        }

        Tag removeTag = null;
        for (Tag tag : mTags) {
            if (tag.getName().equalsIgnoreCase(tagName)) {
                removeTag = tag;
                break;
            }
        }

        if (removeTag != null) {
            mTags.remove(removeTag);
        }

        TagViewModel removeTagViewModel = null;
        for (TagViewModel tagViewModel : mTagViewModels) {
            if (tagViewModel.tag.getName().equalsIgnoreCase(tagName)) {
                removeTagViewModel = tagViewModel;
                break;
            }
        }

        if (removeTagViewModel != null) {
            mTagViewModels.remove(removeTagViewModel);
            mViewHolder.tagsViewContainer.removeView(removeTagViewModel.view);
        }
    }

    public void add(String tagName) {
        add(tagName, true);
    }

    protected TagViewModel add(String tag, boolean checkExists) {
        if (TextUtils.isEmpty(tag)) {
            return null;
        }

        boolean exists = checkExists ? tagExists(tag) : false;

        if (!exists) {
            Tag newTag = new Tag();
            newTag.setName(tag);
            newTag.setAccountId(GlobalApplication.getCurrentAccountId());

            mTags.add(newTag);
            View view = createView(newTag);
        }

        return null;
    }

    private boolean tagExists(String name) {
        for (Tag tag : mTags) {
            if (tag.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }

        return false;
    }

    public List<Tag> getCheckedTags() {
        List<Tag> result = new ArrayList<Tag>();
        for (TagViewModel viewModel : mTagViewModels) {
            if (viewModel.checked) {
                result.add(viewModel.tag);
            }
        }

        return result;
    }

    private class ViewHolder {
        public FlowLayout tagsViewContainer;

        public ViewHolder(View view) {
            tagsViewContainer = (FlowLayout) view.findViewById(R.id.tagsViewContainer);
        }
    }
}