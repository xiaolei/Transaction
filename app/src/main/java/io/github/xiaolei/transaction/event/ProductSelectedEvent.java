package io.github.xiaolei.transaction.event;

import io.github.xiaolei.transaction.entity.Product;

/**
 * TODO: add comment
 */
public class ProductSelectedEvent {
    public Product product;

    public ProductSelectedEvent(Product product) {
        this.product = product;
    }
}
