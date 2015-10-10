package io.github.xiaolei.transaction.repository;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

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

    public int getExchangeRate(String currency_code) throws SQLException {
        int result = 0;
        List<ExchangeRate> queryResult = exchangeRateDao.queryForEq(ExchangeRate.CURRENCY_CODE, currency_code);
        if(queryResult != null && queryResult.size() > 0){
            result = queryResult.get(0).getExchangeRate();
        }

        return result;
    }
}
