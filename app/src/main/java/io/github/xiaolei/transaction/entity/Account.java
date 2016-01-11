package io.github.xiaolei.transaction.entity;

import android.text.TextUtils;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import io.github.xiaolei.enterpriselibrary.utility.HashHelper;
import io.github.xiaolei.transaction.util.PreferenceHelper;

/**
 * TODO: add comment
 */
@DatabaseTable(tableName = "account")
public class Account extends TableEntity {
    public static final String DISPLAY_NAME = "display_name";
    public static final String PHOTO_ID = "photo_id";

    @DatabaseField(canBeNull = false, columnName = "display_name")
    private String displayName;

    @DatabaseField(canBeNull = false, unique = true)
    private String email;

    @DatabaseField(canBeNull = false, unique = true, columnName = "phone_number")
    private String phoneNumber;

    @DatabaseField(canBeNull = true)
    private String password;

    @DatabaseField(canBeNull = true, columnName = "unlock_password")
    private String unlockPassword;

    @DatabaseField(canBeNull = false, columnName = "default_currency_code", defaultValue = "USD")
    private String defaultCurrencyCode;

    @DatabaseField(foreign = true, columnName = PHOTO_ID, foreignColumnName = Photo.ID)
    private Photo photo;

    public Account() {
        setDefaultCurrencyCode(PreferenceHelper.DEFAULT_CURRENCY_CODE);
    }

    public Account(String displayName, String email, String password) {
        setDisplayName(displayName);
        setEmail(email);
        setPassword(password);
    }

    @Override
    public int hashCode() {
        if (!TextUtils.isEmpty(getEmail())) {
            return getEmail().hashCode();
        }

        if (!TextUtils.isEmpty(getPhoneNumber())) {
            return getPhoneNumber().hashCode();
        }

        return -1;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof Tag)) return false;

        Account instance = (Account) other;

        if (!TextUtils.isEmpty(instance.getEmail()) && !TextUtils.isEmpty(this.getEmail())
                && instance.getEmail().equalsIgnoreCase(this.getEmail())) {
            return true;
        }

        if (!TextUtils.isEmpty(instance.getPhoneNumber()) && !TextUtils.isEmpty(this.getPhoneNumber())
                && instance.getPhoneNumber().equalsIgnoreCase(this.getPhoneNumber())) {
            return true;
        }

        if (instance.getId() > 0 && this.getId() > 0 && instance.getId() == this.getId()) {
            return true;
        }

        return false;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email.trim();
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if(TextUtils.isEmpty(password)){
            this.password = "";
        }else {
            this.password = HashHelper.getSHA256SecurePassword(password);
        }
    }

    public String getDefaultCurrencyCode() {
        return defaultCurrencyCode;
    }

    public void setDefaultCurrencyCode(String defaultCurrencyCode) {
        this.defaultCurrencyCode = defaultCurrencyCode;
    }

    public String getUnlockPassword() {
        return unlockPassword;
    }

    public void setUnlockPassword(String unlockPassword) {
        this.unlockPassword = HashHelper.getSHA256SecurePassword(unlockPassword);
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }
}
