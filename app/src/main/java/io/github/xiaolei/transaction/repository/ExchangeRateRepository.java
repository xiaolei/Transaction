package io.github.xiaolei.transaction.repository;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import io.github.xiaolei.enterpriselibrary.utility.DateTimeUtils;
import io.github.xiaolei.transaction.entity.ExchangeRate;
import io.github.xiaolei.transaction.entity.Product;
import io.github.xiaolei.transaction.entity.Transaction;

/**
 * TODO: add comment
 */
public class ExchangeRateRepository extends BaseRepository {
    private Dao<ExchangeRate, Long> exchangeRateDao;

    public ExchangeRateRepository(Context context) throws SQLException {
        super(context);

        exchangeRateDao = getDataAccessObject(ExchangeRate.class);
    }

    public int getExchangeRate(String currencyCode) throws SQLException {
        int result = 0;
        List<ExchangeRate> queryResult = exchangeRateDao.queryForEq(ExchangeRate.CURRENCY_CODE, currencyCode);
        if (queryResult != null && queryResult.size() > 0) {
            result = queryResult.get(0).getExchangeRate();
        }

        return result;
    }

    public List<ExchangeRate> getExchangeRateList() throws SQLException {
        QueryBuilder<ExchangeRate, Long> query = exchangeRateDao.queryBuilder();
        query.orderBy(ExchangeRate.FREQUENCY, false).orderBy(ExchangeRate.CURRENCY_CODE, true);

        return exchangeRateDao.query(query.prepare());
    }

    public List<ExchangeRate> query(long offset, long limit) throws SQLException {
        QueryBuilder<ExchangeRate, Long> queryBuilder = exchangeRateDao.queryBuilder();
        queryBuilder.where().eq(ExchangeRate.ACTIVE, true);

        return exchangeRateDao.query(queryBuilder.orderBy(ExchangeRate.LAST_MODIFIED, false).orderBy(ExchangeRate.CREATION_TIME, false).offset(offset).limit(limit).prepare());
    }

    public List<ExchangeRate> getMostFrequencyUsedCurrencyList(long maxRows) throws SQLException {
        QueryBuilder<ExchangeRate, Long> query = exchangeRateDao.queryBuilder();
        query.limit(maxRows)
                .orderBy(ExchangeRate.FREQUENCY, false)
                .orderBy(ExchangeRate.CURRENCY_CODE, true)
                .where().eq(ExchangeRate.ACTIVE, true);

        return exchangeRateDao.query(query.prepare());
    }
}
