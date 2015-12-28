package io.github.xiaolei.enterpriselibrary.utility;

import android.text.TextUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LcsString extends LongestCommonSubsequence<String> {
    public static final String[] SupportedPunctuation = new String[]{",", ".", ";", "!", "?"};

    private String[] x;
    private String[] y;

    public LcsString(String[] from, String[] to) {
        List<String> fromArray = Arrays.asList(from);
        List<String> toArray = Arrays.asList(to);
        Collections.reverse(fromArray);
        Collections.reverse(toArray);

        this.x = fromArray.toArray(new String[0]);
        this.y = toArray.toArray(new String[0]);
    }

    protected int lengthOfY() {
        return y.length;
    }

    protected int lengthOfX() {
        return x.length;
    }

    protected String valueOfX(int index) {
        return x[index];
    }

    protected String valueOfY(int index) {
        return y[index];
    }

    private boolean isMatch(int index, List<DiffEntry<String>> diffs) {
        if (index < 0 || diffs == null || diffs.size() == 0) {
            return false;
        }

        for (DiffEntry<String> entry : diffs) {
            if (entry.getType() == DiffType.MATCH && entry.getIndex() == index) {
                return true;
            }
        }

        return false;
    }

    public String getHtmlDiff() {
        DiffType type = null;
        List<DiffEntry<String>> diffs = diff();
        Collections.reverse(diffs);
        StringBuffer buf = new StringBuffer();
        String word;
        String originalWord;

        for (int i = this.x.length - 1; i >= 0; i--) {
            word = this.x[i];
            originalWord = word;

            if (isMatch(i, diffs)) {
                word = "<font color=\"black\">" + word + "</font>";
            }

            if (buf.length() > 0) {
                if (!isPunctuation(originalWord)) {
                    buf.append(" " + word);
                } else {
                    buf.append(word);
                }
            } else {
                buf.append(word);
            }
        }

        return buf.toString();
    }

    private String escapeHtml(Character ch) {
        switch (ch) {
            case '<':
                return "&lt;";
            case '>':
                return "&gt;";
            case '"':
                return "\\&quot;";
            default:
                return ch.toString();
        }
    }

    /**
     * 判断是否为标点符号
     *
     * @param text
     * @return
     */
    public static boolean isPunctuation(String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }

        for (String punctuation : SupportedPunctuation) {
            if (text.equals(punctuation)) {
                return true;
            }
        }

        return false;
    }
}