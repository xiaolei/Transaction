package io.github.xiaolei.transaction.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * TODO: add comment
 */
@DatabaseTable(tableName = "photo")
public class Photo extends BaseEntity implements Serializable {
    public static final String URL = "url";

    @DatabaseField(canBeNull = false)
    private String url;

    @DatabaseField()
    private String description;

    public Photo() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
