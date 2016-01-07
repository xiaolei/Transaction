package io.github.xiaolei.transaction.repository;

import android.content.Context;
import android.text.TextUtils;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;

import io.github.xiaolei.transaction.entity.Photo;

/**
 * TODO: add comment
 */
public class PhotoRepository extends BaseRepository {
    private Dao<Photo, Long> photoDao;

    public PhotoRepository(Context context) throws SQLException {
        super(context);

        photoDao = getDataAccessObject(Photo.class);
    }

    public Photo createPhoto(String url, String description, long accountId) throws SQLException {
        if (TextUtils.isEmpty(url)) {
            return null;
        }

        Photo photo = new Photo();
        photo.setUrl(url);
        photo.setDescription(description);
        photo.setAccountId(accountId);

        photoDao.create(photo);
        return photo;
    }

    public void removePhoto(long photoId) throws SQLException {
        UpdateBuilder<Photo, Long> updateBuilder = photoDao.updateBuilder();
        updateBuilder.updateColumnValue(Photo.ACTIVE, false).where().eq(Photo.ID, photoId);

        photoDao.update(updateBuilder.prepare());
    }

    public Photo queryByUrl(String photoUrl) throws SQLException {
        QueryBuilder<Photo, Long> queryBuilder = photoDao.queryBuilder();
        queryBuilder.where().eq(Photo.URL, new SelectArg(photoUrl));

        return photoDao.queryForFirst(queryBuilder.prepare());
    }
}
