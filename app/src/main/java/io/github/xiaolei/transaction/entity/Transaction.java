package io.github.xiaolei.transaction.entity;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * TODO: add comment
 */
@DatabaseTable(tableName = "transaction")
public class Transaction extends BaseEntity {
    public static final String DESCRIPTION = "description";
    public static final String STAR = "star";
    public static final String PRODUCT_PRICE = "product_price";
    public static final String PRICE = "price";
    public static final String PRODUCT_COUNT = "product_count";
    public static final String CURRENCY_CODE = "currency_code";
    public static final String PRODUCT_ID = "product_id";

    @DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false, columnName = PRODUCT_ID)
    private Product product;

    @DatabaseField(canBeNull = false, columnName = PRODUCT_PRICE)
    private int productPrice;

    @DatabaseField(canBeNull = false, columnName = PRICE)
    private int price;

    @DatabaseField(canBeNull = false, columnName = PRODUCT_COUNT)
    private int productCount;

    @DatabaseField(columnName = DESCRIPTION)
    private String description;

    @DatabaseField(canBeNull = false, columnName = CURRENCY_CODE)
    private String currencyCode;

    @DatabaseField(columnName = STAR, defaultValue = "false", dataType = DataType.BOOLEAN)
    private boolean star;

    @ForeignCollectionField(eager = true, orderColumnName = CREATION_TIME, orderAscending = false)
    private ForeignCollection<TransactionPhoto> photos;

    public boolean checked;

    public Transaction() {
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

    public boolean getStar() {
        return star;
    }

    public void setStar(boolean star) {
        this.star = star;
    }
}
