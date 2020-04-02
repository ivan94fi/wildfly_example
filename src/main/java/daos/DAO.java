package daos;

import java.util.List;

import domain.BaseEntity;

public interface DAO<T extends BaseEntity> {
    public List<T> findAll();

    public T findById(Long id);

    public boolean save(T entity);

    boolean delete(T entity);
}
