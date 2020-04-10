package daos;

import java.util.List;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import org.jboss.logging.Logger;

import domain.BaseEntity;

public abstract class BaseDAO<T extends BaseEntity> implements DAO<T> {

    @PersistenceContext(unitName = "swam-example")
    protected EntityManager em;

    @Resource
    private UserTransaction transaction;

    @Inject
    private Logger logger;

    private Class<T> entityClass;

    public BaseDAO() {}

    public BaseDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public List<T> findAll() {
        StringBuilder queryBuilder = new StringBuilder("from ").append(
                entityClass.getName());
        return em.createQuery(queryBuilder.toString(), entityClass)
                 .getResultList();
    }

    @Override
    public T findById(Long id) {
        return em.find(entityClass, id);
    }

    @Override
    public boolean save(T entity) {
        return doInTransaction(() -> em.persist(entity));
    }

    @Override
    public boolean delete(T entity) {
        return doInTransaction(() -> {
            if (em.contains(entity)) {
                em.remove(entity);
            } else {
                em.remove(em.merge(entity));
            }
        });

    }

    @Override
    public boolean merge(T entity) {
        return doInTransaction(() -> em.merge(entity));
    }

    private boolean doInTransaction(Runnable lambda) {
        boolean success = false;
        try {
            transaction.begin();
            lambda.run();
            transaction.commit();
            success = true;
        } catch (Exception e) {
            try {
                if (transaction.getStatus() == Status.STATUS_ACTIVE) {
                    transaction.rollback();
                }
            } catch (Exception e1) {
                logger.error("Cannot rollback transaction", e1);
            }
            logger.error("Cannot complete database operation", e);
        }
        return success;
    }

    protected UserTransaction getTransaction() {
        return transaction;
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

}
