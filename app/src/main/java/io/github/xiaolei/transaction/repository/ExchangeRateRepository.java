package io.github.xiaolei.transaction.repository;

import android.content.Context;
import android.text.TextUtils;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import io.github.xiaolei.enterpriselibrary.utility.CurrencyHelper;
import io.github.xiaolei.transaction.entity.ExchangeRate;

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

    public List<ExchangeRate> query(String searchKeywords, long offset, long limit) throws SQLException {
        QueryBuilder<ExchangeRate, Long> queryBuilder = exchangeRateDao.queryBuilder();

        if (!TextUtils.isEmpty(searchKeywords) && searchKeywords.trim().length() > 0) {
            queryBuilder
                    .where()
                    .eq(ExchangeRate.ACTIVE, true)
                    .and().like(ExchangeRate.CURRENCY_CODE, "%" + searchKeywords + "%");
        } else {
            queryBuilder.where().eq(ExchangeRate.ACTIVE, true);
        }

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

    public void updateExchangeRate(long exchangeRateId, BigDecimal newValue) throws SQLException {
        UpdateBuilder<ExchangeRate, Long> updateBuilder = exchangeRateDao.updateBuilder();
        updateBuilder.updateColumnValue(ExchangeRate.EXCHANGE_RATE, CurrencyHelper.castToInteger(newValue))
                .where().eq(ExchangeRate.ID, exchangeRateId);
        exchangeRateDao.update(updateBuilder.prepare());
    }
}
