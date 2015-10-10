package io.github.xiaolei.transaction.entity;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

import java.util.Date;


/**
 * Base class. Represents a SQLite table.
 */
public abstract class TableEntity {
    public static final String CREATION_TIME = "creation_time";
    public static final String LAST_MODIFIED = "last_modified";
    public static final String ACTIVE = "active";
    public static final String ID = "id";

    @DatabaseField(generatedId = true)
    protected long id;

    @DatabaseField(defaultValue = "true", dataType = DataType.BOOLEAN)
    protected boolean active;

    @DatabaseField(version = true, dataType = DataType.DATE_STRING, format = "yyyy-MM-dd HH:mm:ss", columnName = LAST_MODIFIED)
    private Date lastModified;

    @DatabaseField(dataType = DataType.DATE_STRING, format = "yyyy-MM-dd HH:mm:ss", columnName = CREATION_TIME)
    private Date creationTime = new Date();

    public TableEntity() {
        this.active = true;
        this.setCreationTime(new Date());
    }

    public long getId() {
        return id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }
}
