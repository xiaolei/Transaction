package io.github.xiaolei.enterpriselibrary.utility;

import android.text.TextUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * TODO: add comment
 */
public class HashHelper {
    public static String getSHA256SecurePassword(String plainText) {
        return getSHA256SecurePassword(plainText, null);
    }

    public static String getSHA256SecurePassword(String passwordToHash, String salt) {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            if (!TextUtils.isEmpty(salt)) {
                md.update(salt.getBytes());
            }
            byte[] bytes = md.digest(passwordToHash.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return generatedPassword;
    }
}
