package io.github.xiaolei.transaction.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import io.github.xiaolei.enterpriselibrary.utility.ColorUtil;

/**
 * TODO: add comment
 */
@DatabaseTable(tableName = "product")
public class Product extends BaseEntity {
    public static final String NAME = "name";
    public static final String FREQUENCY = "frequency"; // 使用频率，每购买一次此产品，在原有值基础上+0.0001

    @DatabaseField(canBeNull = false, unique = true)
    private String name;

    @DatabaseField
    private String description;

    @DatabaseField(canBeNull = false, columnName = "banner_color")
    private int bannerColor;

    @DatabaseField
    private String barcode;

    @DatabaseField
    private double frequency;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Product() {
        setBannerColor(ColorUtil.generateRandomColor());
    }

    public Product(long id){
        this.id = id;
    }

    public Product(String name, String description, long accountId) {
        this();
        setName(name);
        setDescription(description);
        setAccountId(accountId);
    }

    public int getBannerColor() {
        return bannerColor;
    }

    public void setBannerColor(int bannerColor) {
        this.bannerColor = bannerColor;
    }

    @Override
    public String toString() {
        return getName();
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public double getFrequency() {
        return frequency;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }
}
