package io.github.xiaolei.transaction.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * TODO: add comment
 */
@DatabaseTable(tableName = "product_tag")
public class ProductTag extends BaseEntity {
    public static final String PRODUCT_ID = "product_id";
    public static final String TAG_ID = "tag_id";


    @DatabaseField(foreign = true, columnName = PRODUCT_ID, foreignAutoRefresh = true)
    private Product product;

    @DatabaseField(foreign = true, columnName = TAG_ID, foreignAutoRefresh = true)
    private Tag tag;

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }
}
