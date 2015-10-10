package io.github.xiaolei.transaction.repository;

import java.math.BigDecimal;
import java.util.UUID;

import io.github.xiaolei.transaction.common.BaseTestCase;
import io.github.xiaolei.transaction.entity.Account;
import io.github.xiaolei.transaction.entity.Product;
import io.github.xiaolei.transaction.entity.Transaction;
import io.github.xiaolei.transaction.util.ConfigurationManager;

/**
 * TODO: add comment
 */
public class TransactionRepositoryTest extends BaseTestCase {

    public void testGetLastTransactionPrice() throws Exception {
        Account account = new Account("test account", "test@example.com", "password");

        TransactionRepository repository = RepositoryProvider.getInstance(getTestContext()).resolve(TransactionRepository.class);
        Transaction transaction = new Transaction();
        Product product = new Product();

        BigDecimal price = new BigDecimal("1.23");
        product.setAccount(account);
        product.setName("test product - " + UUID.randomUUID().toString());
        transaction.setProductPrice(price.movePointRight(ConfigurationManager.DECIMAL_POINT_LEFT).intValue());
        transaction.setPrice(price.movePointRight(ConfigurationManager.DECIMAL_POINT_LEFT).intValue());
        transaction.setProduct(product);
        transaction.setProductCount(1);
        transaction.setAccount(account);
        repository.save(transaction);

        BigDecimal actualPrice = repository.getLastTransactionPrice(product.getName());
        assertEquals(true, price.equals(actualPrice));
    }
}