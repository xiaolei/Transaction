package io.github.xiaolei.transaction.adapter;

import java.sql.SQLException;
import java.util.List;

import io.github.xiaolei.transaction.entity.TableEntity;

/**
 * TODO: add comment
 */
public interface IPaginationDataLoader<T extends TableEntity> {
    List<T> load(int offset, int limit) throws SQLException;
}
