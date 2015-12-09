package io.github.xiaolei.transaction.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * 提供一组集全，数组，List 相关的辅助方法
 */
public class CollectionHelper {

    public static <T> List<T> toList(T[] array) {
        if (array == null) {
            return new ArrayList<T>();
        }

        ArrayList<T> result = new ArrayList<T>();
        for (T item : array) {
            result.add(item);
        }

        return result;
    }

    public static <T> List<T> toList(Collection<T> collection) {
        if (collection == null) {
            return new ArrayList<T>();
        }

        ArrayList<T> result = new ArrayList<T>();
        for (T item : collection) {
            result.add(item);
        }

        return result;
    }

    public static <T> T[] append(T[] array, T element) {
        final int N = array.length;
        array = Arrays.copyOf(array, N + 1);
        array[N] = element;

        return array;
    }

    public static <T> String join(List<T> array, String separator) {
        if (array == null || separator == null) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        for (T item : array) {
            if (result.length() > 0) {
                result.append(separator).append(String.valueOf(item));
            } else {
                result.append(String.valueOf(item));
            }
        }

        return result.toString();
    }
}