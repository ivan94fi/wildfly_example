package daos;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import domain.BaseEntity;

public abstract class BaseDAO<T extends BaseEntity> implements DAO<T> {

    @PersistenceContext(unitName = "swam-example")
    protected EntityManager em;

    @Resource
    protected UserTransaction transaction;

    private Class<T> entityClass;

    public BaseDAO() {}

    public BaseDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public List<T> findAll() {
        StringBuilder queryBuilder = new StringBuilder("from ")
                .append(entityClass.getName());
        return em.createQuery(queryBuilder.toString(), entityClass)
                .getResultList();
    }

    @Override
    public T findById(Long id) {
        return em.find(entityClass, id);
    }

    @Override
    public boolean save(T entity) {
        boolean success = false;
        try {
            transaction.begin();
            em.persist(entity);
            transaction.commit();
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

}
