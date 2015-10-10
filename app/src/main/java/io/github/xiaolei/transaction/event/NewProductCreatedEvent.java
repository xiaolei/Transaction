package io.github.xiaolei.transaction.event;

import io.github.xiaolei.transaction.entity.Product;

/**
 * TODO: add comment
 */
public class NewProductCreatedEvent {
    public Product product;

    public NewProductCreatedEvent(Product product) {
        this.product = product;
    }
}
