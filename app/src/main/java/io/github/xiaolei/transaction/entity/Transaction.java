package io.github.xiaolei.transaction.entity;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * TODO: add comment
 */
@DatabaseTable(tableName = "transaction")
public class Transaction extends BaseEntity {
    public static final String DESCRIPTION = "description";

    @DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false, columnName = "product_id")
    private Product product;

    @DatabaseField(canBeNull = false, columnName = "product_price")
    private int productPrice;

    @DatabaseField(canBeNull = false, columnName = "price")
    private int price;

    @DatabaseField(canBeNull = false, columnName = "product_count")
    private int productCount;

    @DatabaseField(columnName = DESCRIPTION)
    private String description;

    @DatabaseField(canBeNull = false, columnName = "currency_code")
    private String currencyCode;

    @ForeignCollectionField(eager = true)
    private ForeignCollection<TransactionPhoto> photos;

    public boolean checked;

    public Transaction(){
        this.setActive(true);
    }

    public int getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(int productPrice) {
        this.productPrice = productPrice;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getProductCount() {
        return productCount;
    }

    public void setProductCount(int productCount) {
        this.productCount = productCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public ForeignCollection<TransactionPhoto> getPhotos() {
        return photos;
    }

    public void setPhotos(ForeignCollection<TransactionPhoto> photos) {
        this.photos = photos;
    }
}
