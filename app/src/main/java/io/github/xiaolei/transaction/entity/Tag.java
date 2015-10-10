package io.github.xiaolei.transaction.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * TODO: add comment
 */
@DatabaseTable(tableName = "tag")
public class Tag extends BaseEntity {
    public static final String NAME = "name";

    @DatabaseField(canBeNull = false, unique = true)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.trim();
    }

    public Tag() {
    }

    public Tag(String name, Account account) {
        setName(name);
        setAccount(account);
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof Tag)) return false;

        Tag instance = (Tag) other;
        if (instance.getName().equalsIgnoreCase(this.getName())) {
            return true;
        }

        if (instance.getId() > 0 && this.getId() > 0 && instance.getId() == this.getId()) {
            return true;
        }

        return false;
    }
}
