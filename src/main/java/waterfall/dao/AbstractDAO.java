package waterfall.dao;

import org.hibernate.query.Query;
import waterfall.util.HibernateUtil;

import java.io.Serializable;
import java.util.List;

public class AbstractDAO<PK extends Serializable, T> {

    private Class<T> entityClass;

    public AbstractDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public void save(T entity) {
        HibernateUtil.openSessionWithTransaction();
        HibernateUtil.getCurrentSession().save(entity);
        HibernateUtil.closeSessionWithTransaction();
    }

    public void remove(T entity) {
        HibernateUtil.openSessionWithTransaction();
        HibernateUtil.getCurrentSession().remove(entity);
        HibernateUtil.closeSessionWithTransaction();
    }

    public void update(T entity) {
        HibernateUtil.openSessionWithTransaction();
        HibernateUtil.getCurrentSession().update(entity);
        HibernateUtil.closeSessionWithTransaction();
    }

    public T findById(PK id) {
        HibernateUtil.openSessionWithTransaction();
        T entity = HibernateUtil.getCurrentSession().get(entityClass, id);
        HibernateUtil.closeSessionWithTransaction();

        return entity;
    }

    public List<T> findAll() {
        HibernateUtil.openSessionWithTransaction();
        Query<T> query = HibernateUtil.getCurrentSession().createQuery("FROM " + entityClass.getName());
        List<T> entityList = query.list();
        HibernateUtil.closeSessionWithTransaction();

        return entityList;
    }
}
