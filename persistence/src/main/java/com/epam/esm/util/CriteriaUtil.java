package com.epam.esm.util;

import com.epam.esm.criteria.SortingOrder;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.SingularAttribute;

public class CriteriaUtil {

    public static Long getResultsCount(EntityManager entityManager, Predicate conditions, Class<?> tClass){
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = criteriaBuilder.createQuery(Long.class);
        cq.select(criteriaBuilder.count(cq.from(tClass)));
        entityManager.createQuery(cq);
        cq.where(conditions);
        return entityManager.createQuery(cq).getSingleResult();
    }

    public static <T> void applySortingParams(EntityManager entityManager, Root<T> root, CriteriaQuery<T> criteriaQuery,
                                            SingularAttribute<T,?> attribute, SortingOrder order){
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        Path<?> sortPath = root.get(attribute);
        if (order == SortingOrder.DESC) {
            criteriaQuery.orderBy(criteriaBuilder.desc(sortPath));
        } else {
            criteriaQuery.orderBy(criteriaBuilder.asc(sortPath));
        }
    }

}
