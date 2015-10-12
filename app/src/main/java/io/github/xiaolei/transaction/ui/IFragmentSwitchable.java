package io.github.xiaolei.transaction.ui;

import io.github.xiaolei.transaction.entity.Product;
import me.tabak.fragmentswitcher.FragmentSwitcher;

/**
 * TODO: add comment
 */
public interface IFragmentSwitchable {
    FragmentSwitcher getFragmentSwitcher();

    void switchToProductEditor(long productId);

    void switchToTagEditor(long tagId);

    void switchToTagList();

    void switchToProductList(boolean reload, boolean isSelectionMode);

    void switchToCalculator(Product product);
}
