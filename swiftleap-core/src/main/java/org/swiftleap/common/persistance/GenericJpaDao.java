/*
 * Copyright (C) 2018 SwiftLeap.com, Ruan Strydom
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.swiftleap.common.persistance;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.swiftleap.common.types.Range;

import javax.persistence.*;
import javax.persistence.criteria.*;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * @author ruan
 */
public abstract class GenericJpaDao<T extends GenericEntity<ID>, ID extends Serializable>
        implements GenericDao<T, ID> {

    protected Log log = LogFactory.getLog(getClass());
    protected Class<? extends T> entityClass;
    protected Class<? extends ID> idClass;
    protected Supplier<EntityManager> emSupplier;


    public GenericJpaDao(Supplier<EntityManager> emSupplier) {
        this.emSupplier = emSupplier;
        solveEntityClass();
    }

    public GenericJpaDao(EntityManager entityManager) {
        emSupplier = () -> entityManager;
        solveEntityClass();
    }

    public GenericJpaDao() {
        solveEntityClass();
    }

    public static boolean isNullOrEmpty(Collection o) {
        return o == null || o.isEmpty();
    }

    public static boolean isNullOrEmpty(Enum o) {
        return o == null;
    }

    public static boolean isNullOrEmpty(Number o) {
        return o == null;
    }

    public static boolean isNullOrEmpty(String o) {
        return o == null || o.trim().isEmpty();
    }

    public static boolean isNullOrEmpty(Object o) {
        return o == null || o.toString().trim().isEmpty();
    }

    @SuppressWarnings("unchecked")
    private void solveEntityClass() {
        this.entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        this.idClass = (Class<ID>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
    }

    /**
     * Return the entityManager.
     * You may override this if needed.
     *
     * @return EntityManager
     */
    protected EntityManager getEntityManager() {
        if (emSupplier != null) {
            return emSupplier.get();
        }
        return null;
    }

    protected final EntityManager em() {
        return getEntityManager();
    }

    protected CriteriaBuilder cb() {
        return em().getCriteriaBuilder();
    }

    @Override
    public boolean exists(ID id) {
        return em().find(entityClass, id) != null;
    }

    @Override
    public T findById(ID id) {
        if (id == null) return null;
        return em().find(entityClass, id);
    }

    @Override
    public T findById(ID id, boolean lock) {
        if (id == null) return null;
        if (!lock) {
            return em().find(entityClass, id);
        }
        return em().find(entityClass, id, LockModeType.PESSIMISTIC_WRITE);
    }

    @Override
    public Stream<T> findAll() {
        CriteriaQuery<T> cq = createQuery();
        cq.select(cq.from(entityClass));
        return getResultList(cq);
    }

    @Override
    public <E extends T> E persist(E entity) {
        em().persist(entity);
        return entity;
    }

    public <E extends T> E refresh(E entity) {
        if (entity == null) return null;
        em().refresh(entity);
        return entity;
    }

    @Override
    public void delete(Object entity) {
        em().remove(entity);
    }

    public void deleteById(ID id) {
        T t = findById(id);
        if (t != null) {
            em().remove(t);
        }
    }

    @Override
    public void flush() {
        em().flush();
    }

    public void detach(Object o) {
        em().detach(o);
    }

    @Override
    public void clear() {
        em().clear();
    }

    public Stream<T> findRange(Range range) {
        CriteriaQuery<T> cq = createQuery();
        Root<T> rt = cq.from((Class<T>) entityClass);
        cq.select(rt);
        return getResultList(cq, range);
    }

    @Override
    public int count() {
        CriteriaQuery<Long> cq = em().getCriteriaBuilder().createQuery(Long.class);
        Root<T> rt = cq.from((Class<T>) entityClass);
        cq.select(em().getCriteriaBuilder().count(rt));
        TypedQuery<Long> q = em().createQuery(cq);
        return q.getSingleResult().intValue();
    }

    public FlushModeType getFlushMode() {
        return em().getFlushMode();
    }

    public void setFlushMode(FlushModeType flushMode) {
        em().setFlushMode(flushMode);
    }

    public LockModeType getLockMode(Object object) {
        return em().getLockMode(object);
    }

    protected CriteriaQuery<T> createQuery() {
        CriteriaQuery<T> cq = cb().createQuery((Class<T>) entityClass);
        return cq;
    }

    protected Stream<T> getResultList(CriteriaQuery<T> cq, Predicate... and) {
        return getResultList(cq, null, and);
    }

    protected Stream<T> getResultList(CriteriaQuery<T> cq, Range range, Predicate... and) {
        if (and != null && and.length > 0) {
            cq.where(and);
        }
        TypedQuery<T> tq = em().createQuery(cq);
        if (range != null && range.isValid()) {
            tq.setFirstResult(range.getStart());
            tq.setMaxResults(range.getCount());
        }
        return tq.getResultList().stream();
    }

    protected Stream<T> getResultList(CriteriaQuery<T> cq, Collection<Predicate> and) {
        return getResultList(cq, and, null);
    }

    protected Stream<T> getResultList(CriteriaQuery<T> cq, Collection<Predicate> and, Range range) {
        return getResultList(cq, range, and.toArray(new Predicate[]{}));
    }

    protected T getSingleResult(CriteriaQuery<T> cq, Collection<Predicate> and) {
        return getSingleResult(cq, and.toArray(new Predicate[]{}));
    }

    protected T getSingleResult(CriteriaQuery<T> cq, Predicate... and) {
        if (and != null && and.length > 0) {
            cq.where(and);
        }
        TypedQuery<T> tq = em().createQuery(cq);
        try {
            tq.setMaxResults(1);
            return tq.getSingleResult();
        } catch (NoResultException ex) {
        }
        return null;
    }

    protected T getUniqueResult(CriteriaQuery<T> cq, Predicate... and) {
        if (and != null && and.length > 0) {
            cq.where(and);
        }
        return em().createQuery(cq).getSingleResult();
    }

    public void delete(CriteriaDelete<T> del, Predicate... preds) {
        del.where(preds);
        em().createQuery(del).executeUpdate();
    }

    public T merge(T entity) {
        return em().merge(entity);
    }

    public void lock(T b) {
        em().lock(b, LockModeType.PESSIMISTIC_WRITE);
    }

    public void lock(T b, LockModeType lockMode) {
        em().lock(b, lockMode);
    }

}
