package io.github.xiaolei.transaction.repository;

import android.content.Context;
import android.text.TextUtils;
import android.view.TextureView;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import de.greenrobot.event.EventBus;
import io.github.xiaolei.transaction.GlobalApplication;
import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.common.ValidationException;
import io.github.xiaolei.transaction.entity.Product;
import io.github.xiaolei.transaction.entity.ProductTag;
import io.github.xiaolei.transaction.entity.Tag;
import io.github.xiaolei.transaction.event.RefreshProductListEvent;

/**
 * TODO: add comment
 */
public class ProductRepository extends BaseRepository {
    public static final double FREQUENCY_INCREMENTAL_STEP = 0.0001;

    private Dao<Product, Long> productDao;
    private Dao<ProductTag, Long> productTagDao;
    private TagRepository tagRepository;

    public ProductRepository(Context context) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        super(context);

        productDao = getDataAccessObject(Product.class);
        productTagDao = getDataAccessObject(ProductTag.class);
        tagRepository = RepositoryProvider.getInstance(getContext()).resolve(TagRepository.class);
    }

    /**
     * Increases the using frequency of the specified product.
     *
     * @param productId
     * @param increaseFrequency
     * @return
     * @throws SQLException
     */
    public int increaseProductFrequency(long productId, double increaseFrequency) throws SQLException {
        return productDao.executeRaw("update product set frequency = frequency + " + String.valueOf(increaseFrequency) + " where id = ? ", String.valueOf(productId));
    }

    public boolean exists(Product product) throws SQLException {
        QueryBuilder<Product, Long> queryBuilder = productDao.queryBuilder();
        queryBuilder.where().eq(Product.NAME, product.getName()).and().ne(Product.ID, product.getId()).queryForFirst();

        return productDao.queryForFirst(queryBuilder.prepare()) != null;
    }

    public boolean exists(String productName) throws SQLException {
        if (TextUtils.isEmpty(productName)) {
            return false;
        }

        QueryBuilder<Product, Long> queryBuilder = productDao.queryBuilder();
        queryBuilder.where().eq(Product.NAME, productName).queryForFirst();

        return productDao.queryForFirst(queryBuilder.prepare()) != null;
    }

    public boolean isNameDuplicate(long productId, String productName) throws SQLException {
        if (TextUtils.isEmpty(productName) || productId <= 0) {
            return true;
        }

        QueryBuilder<Product, Long> queryBuilder = productDao.queryBuilder();
        queryBuilder.where().ne(Product.ID, productId)
                .and().eq(Product.NAME, productName).queryForFirst();

        return productDao.queryForFirst(queryBuilder.prepare()) != null;
    }

    public Product getProductByName(String productName) throws SQLException {
        if (TextUtils.isEmpty(productName)) {
            return null;
        }

        QueryBuilder<Product, Long> queryBuilder = productDao.queryBuilder();
        queryBuilder.where().eq(Product.NAME, productName).queryForFirst();

        return productDao.queryForFirst(queryBuilder.prepare());
    }

    public void remove(long productId) throws SQLException {
        DeleteBuilder<Product, Long> deleteBuilder = productDao.deleteBuilder();
        deleteBuilder.where().eq(Product.ID, productId);
        productDao.delete(deleteBuilder.prepare());
    }

    public Product rename(Product product, String newName) throws SQLException, ValidationException {
        if (product == null || TextUtils.isEmpty(newName) || TextUtils.equals(newName, product.getName())) {
            return product;
        }

        newName = newName.trim();

        if (isNameDuplicate(product.getId(), newName)) {
            throw new ValidationException(getContext().getString(R.string.name_duplicate));
        }

        product.setName(newName);

        UpdateBuilder<Product, Long> updateBuilder = productDao.updateBuilder();
        updateBuilder.updateColumnValue(Product.LAST_MODIFIED, new Date());
        updateBuilder.updateColumnValue(Product.NAME, newName).where()
                .eq(Product.ID, product.getId())
                .and().eq(Product.ACCOUNT_ID, product.getAccountId());
        productDao.update(updateBuilder.prepare());

        Product result = productDao.queryForId(product.getId());

        return result;
    }

    public Product createOrGetProductByName(String productName) throws SQLException {
        if (TextUtils.isEmpty(productName)) {
            return null;
        }

        Product existProduct = getProductByName(productName);
        if (existProduct != null) {
            return existProduct;
        }

        Product product = new Product();
        product.setAccount(GlobalApplication.getCurrentAccount());
        product.setName(productName.trim());
        productDao.create(product);

        return product;
    }

    public Dao.CreateOrUpdateStatus save(final Product product, final Collection<Tag> tags) throws SQLException, ValidationException {
        if (exists(product)) {
            throw new ValidationException(getContext().getString(R.string.validation_error_product_name_duplicate));
        }

        Dao.CreateOrUpdateStatus result = TransactionManager.callInTransaction(getDatabase().getConnectionSource(), new Callable<Dao.CreateOrUpdateStatus>() {
            @Override
            public Dao.CreateOrUpdateStatus call() throws Exception {
                Dao.CreateOrUpdateStatus status = productDao.createOrUpdate(product);
                updateProductTags(product, tags);

                return status;
            }
        });

        return result;
    }

    public List<Tag> getProductTags(long productId) throws SQLException {
        List<Tag> result = new ArrayList<Tag>();
        List<ProductTag> productTags = productTagDao.queryForEq(ProductTag.PRODUCT_ID, productId);
        for (ProductTag pt : productTags) {
            if (pt != null) {
                result.add(pt.getTag());
            }
        }

        return result;
    }

    public List<Product> query(String keywords, long offset, long limit) throws SQLException {
        Dao<Product, Long> dao = getDataAccessObject(Product.class);
        QueryBuilder<Product, Long> queryBuilder = dao.queryBuilder();
        if (!TextUtils.isEmpty(keywords)) {
            queryBuilder.where().eq(Product.ACTIVE, true).and().like(Tag.NAME, "%" + keywords + "%");
        }

        return dao.query(queryBuilder
                .orderBy(Product.FREQUENCY, false)
                .orderBy(Product.LAST_MODIFIED, false)
                .orderBy(Product.CREATION_TIME, false)
                .offset(offset).limit(limit).prepare());
    }

    public List<Product> query(long offset, long limit) throws SQLException {
        return query(null, offset, limit);
    }

    private List<Tag> getUniqueTags(Collection<Tag> tags) {
        ArrayList<Tag> result = new ArrayList<Tag>();
        if (tags == null || tags.size() == 0) {
            return result;
        }

        Set<Tag> uniqueTags = new LinkedHashSet<Tag>(tags);
        result = new ArrayList<Tag>(uniqueTags);

        return result;
    }

    private void updateProductTags(Product product, final Collection<Tag> tags) throws SQLException {
        if (product == null) {
            return;
        }

        // Delete existing tags of the product.
        DeleteBuilder<ProductTag, Long> deleteBuilder =
                productTagDao.deleteBuilder();
        deleteBuilder.where().eq(ProductTag.PRODUCT_ID, product.getId());
        deleteBuilder.delete();
        productTagDao.delete(deleteBuilder.prepare());

        // Create new tags relationship of the product
        if (tags != null && tags.size() > 0) {
            List<Tag> uniqueTags = getUniqueTags(tags);

            for (Tag tag : uniqueTags) {
                if (TextUtils.isEmpty(tag.getName())) {
                    continue;
                }

                Tag persistedTag = tagRepository.getTagByName(tag.getName());
                if (persistedTag == null) {
                    tagRepository.save(tag);
                    persistedTag = tag;
                }

                ProductTag productTag = new ProductTag();
                productTag.setProduct(product);
                productTag.setTag(persistedTag);
                productTag.setAccount(product.getAccount());

                productTagDao.create(productTag);
            }
        }
    }
}
