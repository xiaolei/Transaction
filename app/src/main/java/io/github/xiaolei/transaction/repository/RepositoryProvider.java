package io.github.xiaolei.transaction.repository;

import android.content.Context;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

/**
 * TODO: add comment
 */
public class RepositoryProvider {
    private static RepositoryProvider INSTANCE;
    private Context mContext;
    private HashMap<Class, Object> mInstances;

    static {
        INSTANCE = new RepositoryProvider();
    }

    private RepositoryProvider() {
        mInstances = new HashMap<Class, Object>();
    }

    public static RepositoryProvider getInstance(Context context) {
        INSTANCE.mContext = context;
        return INSTANCE;
    }

    public synchronized <T extends BaseRepository> T resolve(Class<T> type) {
        Object instance = null;
        if (!mInstances.containsKey(type)) {
            try {
                instance = type.getDeclaredConstructor(Context.class).newInstance(mContext);
                mInstances.put(type, instance);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } else {
            instance = mInstances.get(type);
        }

        return type.cast(instance);
    }
}
