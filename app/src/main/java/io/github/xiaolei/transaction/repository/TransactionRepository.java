package io.github.xiaolei.transaction.repository;

import android.content.Context;
import android.text.TextUtils;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RawRowMapper;
import com.j256.ormlite.stmt.QueryBuilder;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import io.github.xiaolei.transaction.GlobalApplication;
import io.github.xiaolei.transaction.entity.Product;
import io.github.xiaolei.transaction.entity.Transaction;
import io.github.xiaolei.transaction.util.CurrencyHelper;
import io.github.xiaolei.transaction.util.DateTimeUtils;
import io.github.xiaolei.transaction.util.ConfigurationManager;
import io.github.xiaolei.transaction.viewmodel.AmountInfo;
import io.github.xiaolei.transaction.viewmodel.ChartValue;
import io.github.xiaolei.transaction.viewmodel.DailyTransactionSummaryInfo;

/**
 * TODO: add comment
 */
public class TransactionRepository extends BaseRepository {
    private ExchangeRateRepository exchangeRateRepository;
    private Dao<Transaction, Long> transactionDao;
    public static final String AMOUNT_SQL = "select ifnull(sum(case t.currency_code when '%s' then t.product_price else round(t.product_price/er.exchange_rate * %s, 0) end), 0) as total_target_currency_price  from 'transaction' t left join exchange_rate er on er.currency_code = t.currency_code where t.active = 1 ";
    public static final String SQL_EXPENSE_TRANSACTION_AMOUNT_GROUP_BY_DAY = "select abs(ifnull(sum(case t.currency_code when '%s' then t.product_price else round(t.product_price/er.exchange_rate * %s, 0) end), 0)) as price, date(t.creation_time) as creation_time from 'transaction' t left join exchange_rate er on er.currency_code = t.currency_code where t.active = 1 and t.price < 0 group by date(t.creation_time) order by date(t.creation_time) desc";
    public static final String SQL_INCOME_TRANSACTION_AMOUNT_GROUP_BY_DAY = "select ifnull(sum(case t.currency_code when '%s' then t.product_price else round(t.product_price/er.exchange_rate * %s, 0) end), 0) as price, date(t.creation_time) as creation_time from 'transaction' t left join exchange_rate er on er.currency_code = t.currency_code where t.active = 1 and t.price > 0 group by date(t.creation_time) order by date(t.creation_time) desc";
    public static final String SQL_MOST_EXPENSIVE_TRANSACTION = "select t.id, t.product_id, t.product_count, t.product_price, (case t.currency_code when '%s' then t.product_price else round(t.product_price/er.exchange_rate * %s, 0) end) as price, p.name from 'transaction' t left join exchange_rate er on er.currency_code = t.currency_code left join product p on p.id = t.product_id where t.active=1 and t.account_id =? order by abs(t.product_price) desc limit 1";
    public static final String SQL_TOTAL_TRANSACTION_COUNT = "select count(id) from 'transaction' t where t.active=1 and t.account_id=?";

    public TransactionRepository(Context context) throws SQLException {
        super(context);

        transactionDao = getDataAccessObject(Transaction.class);
        exchangeRateRepository = RepositoryProvider.getInstance(getContext()).resolve(ExchangeRateRepository.class);
    }

    public Dao.CreateOrUpdateStatus save(Transaction transaction) throws SQLException {
        return transactionDao.createOrUpdate(transaction);
    }

    public List<Transaction> query(long accountId, Date fromDate, Date toDate, long offset, long limit) throws SQLException {
        Dao<Transaction, Long> dao = getDataAccessObject(Transaction.class);

        QueryBuilder<Transaction, Long> queryBuilder = dao.queryBuilder();
        queryBuilder.where().eq(Product.ACTIVE, true)
                .and().eq(Transaction.ACCOUNT_ID, accountId)
                .and().between(Transaction.CREATION_TIME, DateTimeUtils.getStartTimeOfDate(fromDate), DateTimeUtils.getEndTimeOfDate(toDate));

        return dao.query(queryBuilder.orderBy(Transaction.LAST_MODIFIED, false).orderBy(Transaction.CREATION_TIME, false).offset(offset).limit(limit).prepare());
    }

    public List<Transaction> query(long accountId, Date fromDate, Date toDate) throws SQLException {
        Dao<Transaction, Long> dao = getDataAccessObject(Transaction.class);
        QueryBuilder<Transaction, Long> queryBuilder = dao.queryBuilder();
        queryBuilder.where().eq(Product.ACTIVE, true)
                .and().eq(Transaction.ACCOUNT_ID, accountId)
                .and().between(Transaction.CREATION_TIME, DateTimeUtils.getStartTimeOfDate(fromDate), DateTimeUtils.getEndTimeOfDate(toDate));

        return dao.query(queryBuilder.orderBy(Transaction.LAST_MODIFIED, false).orderBy(Transaction.CREATION_TIME, false).prepare());
    }

    public List<Transaction> query(long accountId, Date transactionDate) throws SQLException {
        return query(accountId, transactionDate, transactionDate);
    }

    public BigDecimal getLastTransactionPrice(String productName) throws SQLException {
        if (TextUtils.isEmpty(productName)) {
            return BigDecimal.ZERO;
        }

        GenericRawResults<BigDecimal> result = transactionDao.queryRaw("select t.product_price from 'transaction' t left join 'product' p on t.product_id = p.id where p.name = ? order by t.last_modified desc limit 1",
                new RawRowMapper<BigDecimal>() {
                    @Override
                    public BigDecimal mapRow(String[] columnNames, String[] resultColumns) throws SQLException {
                        String value = resultColumns[0];
                        if (!TextUtils.isEmpty(value)) {
                            return new BigDecimal(value).movePointLeft(ConfigurationManager.DECIMAL_POINT_LEFT);
                        } else {
                            return null;
                        }
                    }
                }, productName);
        BigDecimal value = result.getFirstResult();
        if (value != null) {
            return value;
        } else {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getTotalIncoming(final Date date, String targetCurrencyCode) throws SQLException {
        int targetCurrencyExchangeRate = exchangeRateRepository.getExchangeRate(targetCurrencyCode);
        String sql = String.format(AMOUNT_SQL, targetCurrencyCode, String.valueOf(targetCurrencyExchangeRate));
        GenericRawResults<BigDecimal> result = transactionDao.queryRaw(sql + " and t.price > 0 and t.creation_time between ? and ?", new RawRowMapper<BigDecimal>() {
            @Override
            public BigDecimal mapRow(String[] columnNames, String[] resultColumns) throws SQLException {
                String value = resultColumns[0];
                if (!TextUtils.isEmpty(value)) {
                    return new BigDecimal(value).movePointLeft(ConfigurationManager.DECIMAL_POINT_LEFT);
                } else {
                    return null;
                }
            }
        }, DateTimeUtils.getStartTimeStringOfDate(date), DateTimeUtils.getEndTimeStringOfDate(date));

        BigDecimal value = result.getFirstResult();
        if (value != null) {
            return value;
        } else {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getTotalIncoming(String targetCurrencyCode) throws SQLException {
        int targetCurrencyExchangeRate = exchangeRateRepository.getExchangeRate(targetCurrencyCode);
        String sql = String.format(AMOUNT_SQL, targetCurrencyCode, String.valueOf(targetCurrencyExchangeRate));
        GenericRawResults<BigDecimal> result = transactionDao.queryRaw(sql + " and t.price > 0 ", new RawRowMapper<BigDecimal>() {
            @Override
            public BigDecimal mapRow(String[] columnNames, String[] resultColumns) throws SQLException {
                String value = resultColumns[0];
                if (!TextUtils.isEmpty(value)) {
                    return new BigDecimal(value).movePointLeft(ConfigurationManager.DECIMAL_POINT_LEFT);
                } else {
                    return null;
                }
            }
        });

        BigDecimal value = result.getFirstResult();
        if (value != null) {
            return value;
        } else {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getTotalIncoming(final Date startDate, final Date endDate, String targetCurrencyCode) throws SQLException {
        int targetCurrencyExchangeRate = exchangeRateRepository.getExchangeRate(targetCurrencyCode);
        String sql = String.format(AMOUNT_SQL, targetCurrencyCode, String.valueOf(targetCurrencyExchangeRate));
        GenericRawResults<BigDecimal> result = transactionDao.queryRaw(sql + " and t.price > 0 and t.creation_time between ? and ?", new RawRowMapper<BigDecimal>() {
            @Override
            public BigDecimal mapRow(String[] columnNames, String[] resultColumns) throws SQLException {
                String value = resultColumns[0];
                if (!TextUtils.isEmpty(value)) {
                    return new BigDecimal(value).movePointLeft(ConfigurationManager.DECIMAL_POINT_LEFT);
                } else {
                    return null;
                }
            }
        }, DateTimeUtils.getStartTimeStringOfDate(startDate), DateTimeUtils.getEndTimeStringOfDate(endDate));

        BigDecimal value = result.getFirstResult();
        if (value != null) {
            return value;
        } else {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getTotalOutgoing(final Date date, String targetCurrencyCode) throws SQLException {
        int targetCurrencyExchangeRate = exchangeRateRepository.getExchangeRate(targetCurrencyCode);
        String sql = String.format(AMOUNT_SQL, targetCurrencyCode, String.valueOf(targetCurrencyExchangeRate));
        GenericRawResults<BigDecimal> result = transactionDao.queryRaw(sql + " and t.price < 0 and t.creation_time between ? and ?", new RawRowMapper<BigDecimal>() {
            @Override
            public BigDecimal mapRow(String[] columnNames, String[] resultColumns) throws SQLException {
                String value = resultColumns[0];
                if (!TextUtils.isEmpty(value)) {
                    return new BigDecimal(value).movePointLeft(ConfigurationManager.DECIMAL_POINT_LEFT);
                } else {
                    return null;
                }
            }
        }, DateTimeUtils.getStartTimeStringOfDate(date), DateTimeUtils.getEndTimeStringOfDate(date));

        BigDecimal value = result.getFirstResult();
        if (value != null) {
            return value;
        } else {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getTotalOutgoing(String targetCurrencyCode) throws SQLException {
        int targetCurrencyExchangeRate = exchangeRateRepository.getExchangeRate(targetCurrencyCode);
        String sql = String.format(AMOUNT_SQL, targetCurrencyCode, String.valueOf(targetCurrencyExchangeRate));
        GenericRawResults<BigDecimal> result = transactionDao.queryRaw(sql + " and t.price < 0 ", new RawRowMapper<BigDecimal>() {
            @Override
            public BigDecimal mapRow(String[] columnNames, String[] resultColumns) throws SQLException {
                String value = resultColumns[0];
                if (!TextUtils.isEmpty(value)) {
                    return new BigDecimal(value).movePointLeft(ConfigurationManager.DECIMAL_POINT_LEFT);
                } else {
                    return null;
                }
            }
        });

        BigDecimal value = result.getFirstResult();
        if (value != null) {
            return value;
        } else {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getTotalOutgoing(final Date startDate, final Date endDate, String targetCurrencyCode) throws SQLException {
        int targetCurrencyExchangeRate = exchangeRateRepository.getExchangeRate(targetCurrencyCode);
        String sql = String.format(AMOUNT_SQL, targetCurrencyCode, String.valueOf(targetCurrencyExchangeRate));
        GenericRawResults<BigDecimal> result = transactionDao.queryRaw(sql + " and t.price < 0 and t.creation_time between ? and ?", new RawRowMapper<BigDecimal>() {
            @Override
            public BigDecimal mapRow(String[] columnNames, String[] resultColumns) throws SQLException {
                String value = resultColumns[0];
                if (!TextUtils.isEmpty(value)) {
                    return new BigDecimal(value).movePointLeft(ConfigurationManager.DECIMAL_POINT_LEFT);
                } else {
                    return null;
                }
            }
        }, DateTimeUtils.getStartTimeStringOfDate(startDate), DateTimeUtils.getEndTimeStringOfDate(endDate));

        BigDecimal value = result.getFirstResult();
        if (value != null) {
            return value;
        } else {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getTotalAmount(final Date date, String targetCurrencyCode) throws SQLException {
        int targetCurrencyExchangeRate = exchangeRateRepository.getExchangeRate(targetCurrencyCode);
        String sql = String.format(AMOUNT_SQL, targetCurrencyCode, String.valueOf(targetCurrencyExchangeRate)) + " and t.creation_time between ? and ?";
        GenericRawResults<BigDecimal> result = transactionDao.queryRaw(sql, new RawRowMapper<BigDecimal>() {
            @Override
            public BigDecimal mapRow(String[] columnNames, String[] resultColumns) throws SQLException {
                String value = resultColumns[0];
                if (!TextUtils.isEmpty(value)) {
                    return new BigDecimal(value).movePointLeft(ConfigurationManager.DECIMAL_POINT_LEFT);
                } else {
                    return null;
                }
            }
        }, DateTimeUtils.getStartTimeStringOfDate(date), DateTimeUtils.getEndTimeStringOfDate(date));

        BigDecimal value = result.getFirstResult();
        if (value != null) {
            return value;
        } else {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getTotalAmount(String targetCurrencyCode) throws SQLException {
        int targetCurrencyExchangeRate = exchangeRateRepository.getExchangeRate(targetCurrencyCode);
        String sql = String.format(AMOUNT_SQL, targetCurrencyCode, String.valueOf(targetCurrencyExchangeRate));
        GenericRawResults<BigDecimal> result = transactionDao.queryRaw(sql, new RawRowMapper<BigDecimal>() {
            @Override
            public BigDecimal mapRow(String[] columnNames, String[] resultColumns) throws SQLException {
                String value = resultColumns[0];
                if (!TextUtils.isEmpty(value)) {
                    return new BigDecimal(value).movePointLeft(ConfigurationManager.DECIMAL_POINT_LEFT);
                } else {
                    return null;
                }
            }
        });

        BigDecimal value = result.getFirstResult();
        if (value != null) {
            return value;
        } else {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getTotalAmount(final Date startDate, final Date endDate, String targetCurrencyCode) throws SQLException {
        int targetCurrencyExchangeRate = exchangeRateRepository.getExchangeRate(targetCurrencyCode);
        String sql = String.format(AMOUNT_SQL, targetCurrencyCode, String.valueOf(targetCurrencyExchangeRate)) + " and t.creation_time between ? and ?";
        GenericRawResults<BigDecimal> result = transactionDao.queryRaw(sql, new RawRowMapper<BigDecimal>() {
            @Override
            public BigDecimal mapRow(String[] columnNames, String[] resultColumns) throws SQLException {
                String value = resultColumns[0];
                if (!TextUtils.isEmpty(value)) {
                    return new BigDecimal(value).movePointLeft(ConfigurationManager.DECIMAL_POINT_LEFT);
                } else {
                    return null;
                }
            }
        }, DateTimeUtils.getStartTimeStringOfDate(startDate), DateTimeUtils.getEndTimeStringOfDate(endDate));

        BigDecimal value = result.getFirstResult();
        if (value != null) {
            return value;
        } else {
            return BigDecimal.ZERO;
        }
    }

    public List<ChartValue> getExpenseTransactionsGroupByDay(String targetCurrencyCode) throws SQLException {
        int targetCurrencyExchangeRate = exchangeRateRepository.getExchangeRate(targetCurrencyCode);
        String sql = String.format(SQL_EXPENSE_TRANSACTION_AMOUNT_GROUP_BY_DAY, targetCurrencyCode, String.valueOf(targetCurrencyExchangeRate));
        GenericRawResults<ChartValue> result = transactionDao.queryRaw(sql, new RawRowMapper<ChartValue>() {
            @Override
            public ChartValue mapRow(String[] columnNames, String[] resultColumns) throws SQLException {
                BigDecimal price = new BigDecimal(resultColumns[0]).movePointLeft(ConfigurationManager.DECIMAL_POINT_LEFT);
                String creationTime = resultColumns[1];

                return new ChartValue(creationTime, price);
            }
        });

        return result.getResults();
    }

    public List<ChartValue> getIncomeTransactionsGroupByDay(String targetCurrencyCode) throws SQLException {
        int targetCurrencyExchangeRate = exchangeRateRepository.getExchangeRate(targetCurrencyCode);
        String sql = String.format(SQL_INCOME_TRANSACTION_AMOUNT_GROUP_BY_DAY, targetCurrencyCode, String.valueOf(targetCurrencyExchangeRate));
        GenericRawResults<ChartValue> result = transactionDao.queryRaw(sql, new RawRowMapper<ChartValue>() {
            @Override
            public ChartValue mapRow(String[] columnNames, String[] resultColumns) throws SQLException {
                BigDecimal price = new BigDecimal(resultColumns[0]).movePointLeft(ConfigurationManager.DECIMAL_POINT_LEFT);
                String creationTime = resultColumns[1];

                return new ChartValue(creationTime, price);
            }
        });

        return result.getResults();
    }

    public AmountInfo getAmountInfo(final Date startDate, final Date endDate, String targetCurrencyCode) throws SQLException {
        AmountInfo result = new AmountInfo();
        result.currencyCode = targetCurrencyCode;
        result.amount = getTotalAmount(startDate, endDate, targetCurrencyCode);
        result.totalExpense = getTotalOutgoing(startDate, endDate, targetCurrencyCode);
        result.totalIncome = getTotalIncoming(startDate, endDate, targetCurrencyCode);

        return result;
    }

    public AmountInfo getAmountInfo(String targetCurrencyCode) throws SQLException {
        AmountInfo result = new AmountInfo();
        result.currencyCode = targetCurrencyCode;
        result.amount = getTotalAmount(targetCurrencyCode);
        result.totalExpense = getTotalOutgoing(targetCurrencyCode);
        result.totalIncome = getTotalIncoming(targetCurrencyCode);

        return result;
    }

    public DailyTransactionSummaryInfo getTransactionSummaryByDate(final Date date, String targetCurrencyCode) throws SQLException {
        final DailyTransactionSummaryInfo result = new DailyTransactionSummaryInfo();
        result.date = date;
        result.totalIncoming = getTotalIncoming(date, targetCurrencyCode);
        result.totalOutgoing = getTotalOutgoing(date, targetCurrencyCode);
        result.totalPrice = getTotalAmount(date, targetCurrencyCode);
        result.currencyCode = targetCurrencyCode;

        return result;
    }

    public Transaction getMostExpensiveTransaction(String targetCurrencyCode, long accountId) throws SQLException {
        int targetCurrencyExchangeRate = exchangeRateRepository.getExchangeRate(targetCurrencyCode);
        String sql = String.format(SQL_MOST_EXPENSIVE_TRANSACTION, targetCurrencyCode, String.valueOf(targetCurrencyExchangeRate));
        GenericRawResults<Transaction> result = transactionDao.queryRaw(sql, new RawRowMapper<Transaction>() {
            @Override
            public Transaction mapRow(String[] columnNames, String[] resultColumns) throws SQLException {
                String id = resultColumns[0];
                long productId = Long.parseLong(resultColumns[1]);
                int productCount = Integer.parseInt(resultColumns[2]);
                int productPrice = Integer.parseInt(resultColumns[3]);
                int price = Integer.parseInt(resultColumns[4]);
                String productName = resultColumns[5];

                Product product = new Product();
                product.setId(productId);
                product.setName(productName);

                Transaction result = new Transaction();
                result.setAccountId(GlobalApplication.getCurrentAccount().getId());
                result.setId(Long.parseLong(id));
                result.setProductPrice(productPrice);
                result.setProduct(product);
                result.setPrice(price);

                return result;
            }
        }, new String[]{String.valueOf(accountId)});

        return result.getFirstResult();
    }

    public long getTotalTransactionCount(long accountId) throws SQLException {
        String sql = SQL_TOTAL_TRANSACTION_COUNT;
        GenericRawResults<Long> result = transactionDao.queryRaw(sql, new RawRowMapper<Long>() {
            @Override
            public Long mapRow(String[] columnNames, String[] resultColumns) throws SQLException {
                return Long.parseLong(resultColumns[0]);
            }
        }, new String[]{String.valueOf(accountId)});

        Long value = result.getFirstResult();
        return value != null ? value.longValue() : 0;
    }
}
