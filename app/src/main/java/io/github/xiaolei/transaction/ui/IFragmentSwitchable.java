package io.github.xiaolei.transaction.ui;

import io.github.xiaolei.enterpriselibrary.widget.FragmentViewPager;
import io.github.xiaolei.transaction.entity.Product;

/**
 * TODO: add comment
 */
public interface IFragmentSwitchable {
    FragmentViewPager getFragmentSwitcher();

    void switchToProductEditor(long productId);

    void switchToTagEditor(long tagId);

    void switchToTagList();

    void switchToProductList(boolean reload, boolean isSelectionMode);

    void switchToCalculator(Product product);
}
