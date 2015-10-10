package io.github.xiaolei.transaction.repository;

import android.test.AndroidTestCase;

import com.j256.ormlite.dao.Dao;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.github.xiaolei.transaction.common.BaseTestCase;
import io.github.xiaolei.transaction.entity.Account;
import io.github.xiaolei.transaction.entity.Product;
import io.github.xiaolei.transaction.entity.Tag;

/**
 * TODO: add comment
 */
public class ProductRepositoryTest extends BaseTestCase {

    public void testSave() throws Exception {
        Account account = new Account("test account", "test@example.com", "password");
        RepositoryProvider.getInstance(getTestContext()).resolve(AccountRepository.class).save(account);

        Product product = new Product("new product", "test product description", account);
        List<Tag> tags = new ArrayList<Tag>();
        Tag tag1 = new Tag("tag1", account);
        Tag tag2 = new Tag("tag2", account);
        tags.add(tag1);
        tags.add(tag2);

        ProductRepository productRepository = RepositoryProvider.getInstance(getTestContext()).resolve(ProductRepository.class);
        Dao.CreateOrUpdateStatus result = productRepository.save(product, tags);
        assertEquals(true, result.isCreated());

        Collection<Tag> expectedTags = productRepository.getProductTags(product.getId());
        assertEquals(tags.size(), expectedTags.size());
    }
}