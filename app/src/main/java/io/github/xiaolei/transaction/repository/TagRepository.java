package io.github.xiaolei.transaction.repository;

import android.content.Context;
import android.text.TextUtils;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

import io.github.xiaolei.transaction.entity.Product;
import io.github.xiaolei.transaction.entity.Tag;

/**
 * TODO: add comment
 */
public class TagRepository extends BaseRepository {
    private Dao<Tag, Long> tagDao;

    public TagRepository(Context context) throws SQLException {
        super(context);

        tagDao = getDataAccessObject(Tag.class);
    }

    public Dao.CreateOrUpdateStatus save(Tag tag) throws SQLException {
        QueryBuilder<Tag, Long> queryBuilder = tagDao.queryBuilder();
        queryBuilder.where().eq(Tag.NAME, tag.getName()).countOf();
        boolean exist = tagDao.countOf(queryBuilder.prepare()) > 0;

        if (exist) {
            Dao.CreateOrUpdateStatus status = new Dao.CreateOrUpdateStatus(false, false, 0);
            return status;
        }

        return tagDao.createOrUpdate(tag);
    }

    public Tag getTagByName(String name) throws SQLException {
        if (TextUtils.isEmpty(name)) {
            return null;
        }

        QueryBuilder<Tag, Long> queryBuilder = tagDao.queryBuilder();
        queryBuilder.where().eq(Tag.NAME, name);
        Tag result = tagDao.queryForFirst(queryBuilder.prepare());

        return result;
    }

    public List<Tag> query(String keywords, long offset, long limit) throws SQLException {
        Dao<Tag, Long> dao = getDataAccessObject(Tag.class);
        QueryBuilder<Tag, Long> queryBuilder = dao.queryBuilder();
        if (!TextUtils.isEmpty(keywords)) {
            queryBuilder.where().eq(Tag.ACTIVE, true).and().like(Tag.NAME, "%" + keywords + "%");
        }

        return dao.query(queryBuilder.orderBy(Tag.LAST_MODIFIED, false).orderBy(Tag.CREATION_TIME, false).offset(offset).limit(limit).prepare());
    }

    public List<Tag> query(long offset, long limit) throws SQLException {
        return query(null, offset, limit);
    }
}
