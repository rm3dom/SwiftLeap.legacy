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

import org.swiftleap.common.security.SecurityContext;
import org.swiftleap.common.security.Tenanted;
import org.swiftleap.common.service.ServiceException;
import org.swiftleap.common.types.Range;
import org.swiftleap.common.util.ArrayUtil;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Created by ruans on 2017/06/03.
 */
public class TenantedJpaDao<T extends TenantedEntity<ID>, ID extends Serializable>
        extends GenericJpaDao<T, ID> {
    public TenantedJpaDao(Supplier<EntityManager> emSupplier) {
        super(emSupplier);
    }

    public TenantedJpaDao(EntityManager entityManager) {
        super(entityManager);
    }

    public TenantedJpaDao() {
    }

    protected Predicate tenantPredicate(Root<T> rt) {
        if (rt == null)
            throw new ServiceException("You must create a root for tenancy to work, create an unused root!");
        Integer tenantId = SecurityContext.getTenantId();
        if (Tenanted.class.isAssignableFrom(idClass))
            return cb().equal(rt.get("id").get("tenantId"), tenantId);
        return cb().equal(rt.get("tenantId"), tenantId);
    }

    protected Predicate tenantPredicate(CriteriaQuery<T> cq) {
        Root<T> rt = getExistingRoot(cq);
        return tenantPredicate(rt);
    }

    protected Predicate tenantPredicate(CriteriaDelete<T> cq) {
        return tenantPredicate(cq.getRoot());
    }

    protected Root<T> getExistingRoot(CriteriaQuery<T> cq) {
        for (Root<?> root : cq.getRoots()) {
            if (root.getJavaType() == entityClass)
                return (Root<T>) root;
        }
        return null;
    }

    @Override
    public Stream<T> findRange(Range range) {
        CriteriaQuery<T> cq = createQuery();
        Root<T> rt = cq.from((Class<T>) entityClass);
        cq.select(rt);
        cq.where(tenantPredicate(rt));
        return getResultList(cq, range);
    }

    @Override
    public int count() {
        CriteriaQuery<Long> cq = em().getCriteriaBuilder().createQuery(Long.class);
        Root<T> rt = cq.from((Class<T>) entityClass);
        cq.select(em().getCriteriaBuilder().count(rt));
        cq.where(tenantPredicate(rt));
        TypedQuery<Long> q = em().createQuery(cq);
        return q.getSingleResult().intValue();
    }

    @Override
    public T findById(ID id) {
        if (id instanceof Tenanted)
            ((Tenanted) id).setTenantId(SecurityContext.getTenantId());
        return super.findById(id);
    }

    @Override
    public T findById(ID id, boolean lock) {
        if (id instanceof Tenanted)
            ((Tenanted) id).setTenantId(SecurityContext.getTenantId());
        return super.findById(id, lock);
    }


    @Override
    protected Stream<T> getResultList(CriteriaQuery<T> cq, Predicate... and) {
        return getResultList(cq, null, and);
    }

    @Override
    protected Stream<T> getResultList(CriteriaQuery<T> cq, Range range, Predicate... and) {
        cq.where(ArrayUtil.concat(and, tenantPredicate(cq)));

        TypedQuery<T> tq = em().createQuery(cq);
        if (range != null && range.isValid()) {
            tq.setFirstResult(range.getStart());
            tq.setMaxResults(range.getCount());
        }
        return tq.getResultList().stream();
    }

    @Override
    protected T getSingleResult(CriteriaQuery<T> cq, Predicate... and) {
        cq.where(ArrayUtil.concat(and, tenantPredicate(cq)));

        TypedQuery<T> tq = em().createQuery(cq);
        try {
            tq.setMaxResults(1);
            return tq.getSingleResult();
        } catch (NoResultException ex) {
            //Nothing
        }
        return null;
    }

    @Override
    protected T getUniqueResult(CriteriaQuery<T> cq, Predicate... and) {
        cq.where(ArrayUtil.concat(and, tenantPredicate(cq)));
        try {
            return em().createQuery(cq).getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    @Override
    public void delete(CriteriaDelete<T> del, Predicate... and) {
        del.where(ArrayUtil.concat(and, tenantPredicate(del)));
        em().createQuery(del).executeUpdate();
    }
}
