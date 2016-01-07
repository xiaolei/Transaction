package io.github.xiaolei.transaction.repository;

import android.content.Context;
import android.text.TextUtils;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.List;

import io.github.xiaolei.transaction.R;
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
        queryBuilder.where().eq(Tag.NAME, new SelectArg(tag.getName())).countOf();
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
        queryBuilder.where().eq(Tag.NAME, new SelectArg(name));
        Tag result = tagDao.queryForFirst(queryBuilder.prepare());

        return result;
    }

    public boolean isNameDuplicate(long tagId, String newName) throws SQLException {
        if (tagId <= 0 || TextUtils.isEmpty(newName)) {
            return false;
        }

        QueryBuilder<Tag, Long> queryBuilder = tagDao.queryBuilder();
        queryBuilder.where()
                .eq(Tag.NAME, new SelectArg(newName))
                .and().ne(Tag.ID, tagId);
        Tag result = tagDao.queryForFirst(queryBuilder.prepare());

        return result != null;
    }

    public List<Tag> query(String keywords, long offset, long limit) throws SQLException {
        Dao<Tag, Long> dao = getDataAccessObject(Tag.class);
        QueryBuilder<Tag, Long> queryBuilder = dao.queryBuilder();
        if (!TextUtils.isEmpty(keywords)) {
            queryBuilder.where().eq(Tag.ACTIVE, true).and().like(Tag.NAME, new SelectArg("%" + keywords + "%"));
        }

        return dao.query(queryBuilder.orderBy(Tag.LAST_MODIFIED, false).orderBy(Tag.CREATION_TIME, false).offset(offset).limit(limit).prepare());
    }

    public List<Tag> query(long offset, long limit) throws SQLException {
        return query(null, offset, limit);
    }

    public int rename(long tagId, String newTagName) throws Exception {
        if (TextUtils.isEmpty(newTagName)) {
            return 0;
        }

        newTagName = newTagName.trim();

        if (isNameDuplicate(tagId, newTagName)) {
            throw new Exception(getContext().getString(R.string.error_duplicate_tag_name));
        }

        UpdateBuilder<Tag, Long> updateBuilder = tagDao.updateBuilder();
        updateBuilder.updateColumnValue(Tag.NAME, new SelectArg(newTagName))
                .where()
                .eq(Tag.ID, tagId);

        return tagDao.update(updateBuilder.prepare());
    }

    public Tag create(String name, long accountId) throws SQLException {
        if (TextUtils.isEmpty(name)) {
            return null;
        }

        Tag tag = new Tag();
        tag.setName(name.trim());
        tag.setAccountId(accountId);

        save(tag);
        return tag;
    }
}
