package io.github.xiaolei.transaction.repository;

import android.test.AndroidTestCase;

import com.j256.ormlite.dao.Dao;

import junit.framework.TestCase;

import java.lang.reflect.InvocationTargetException;

import io.github.xiaolei.transaction.common.BaseTestCase;
import io.github.xiaolei.transaction.entity.Account;

/**
 * TODO: add comment
 */
public class AccountRepositoryTest extends BaseTestCase {

    private AccountRepository mAccountRepository;

    public AccountRepositoryTest() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

    }

    public void testGetAccountDao() throws Exception {
        AccountRepository accountRepository = RepositoryProvider.getInstance(getTestContext()).resolve(AccountRepository.class);
        assertNotNull(accountRepository);
    }

    public void testSave() throws Exception {
        Account account = new Account();
        account.setDisplayName("Test");
        account.setEmail("test@example.com");
        account.setPassword("password");

        AccountRepository accountRepository = RepositoryProvider.getInstance(getTestContext()).resolve(AccountRepository.class);
        Dao.CreateOrUpdateStatus result = accountRepository.save(account);
        assertEquals(true, result.isCreated());

        account.setDisplayName("Test_modified");
        result = accountRepository.save(account);
        assertEquals(true, result.isUpdated() && result.getNumLinesChanged() == 1);
    }
}