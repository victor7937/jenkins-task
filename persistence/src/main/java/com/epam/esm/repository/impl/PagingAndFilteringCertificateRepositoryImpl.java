package com.epam.esm.repository.impl;

import com.epam.esm.criteria.CertificateCriteria;
import com.epam.esm.dto.PagedDTO;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.GiftCertificate_;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.DataNotExistRepositoryException;
import com.epam.esm.exception.IncorrectPageRepositoryException;
import com.epam.esm.exception.RepositoryException;
import com.epam.esm.repository.PagingAndFilteringCertificateRepository;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.util.CriteriaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.List;
import java.util.Set;

@Component
public class PagingAndFilteringCertificateRepositoryImpl implements PagingAndFilteringCertificateRepository {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    TagRepository tagRepository;

    @Override
    public PagedDTO<GiftCertificate> getByCriteria(CertificateCriteria criteria, int pageSize, int pageNumber) throws IncorrectPageRepositoryException {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<GiftCertificate> criteriaQuery = criteriaBuilder.createQuery(GiftCertificate.class);
        Root<GiftCertificate> gcRoot = criteriaQuery.from(GiftCertificate.class);
        Predicate conditions = criteriaBuilder.conjunction();
        criteriaQuery.select(gcRoot).distinct(true);

        if (criteria.isTagAdded()){
            boolean allTagsValid = true;
            try {
                conditions = criteriaBuilder.and(conditions, createTagsMatchingPredicate(criteria, gcRoot));
            } catch (DataNotExistRepositoryException e){
                allTagsValid = false;
            }
            if (!allTagsValid) {
                return new PagedDTO<>();
            }
        }

        conditions = criteriaBuilder.and(conditions, createPredicates(criteria, gcRoot));

        Long count = CriteriaUtil.getResultsCount(entityManager, conditions, GiftCertificate.class);
        if (count == 0L){
            return new PagedDTO<>();
        }

        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(pageSize, pageNumber, count);
        if (metadata.getTotalPages() < metadata.getNumber()) {
            throw new IncorrectPageRepositoryException();
        }

        criteriaQuery.where(conditions);

        CriteriaUtil.applySortingParams(entityManager, gcRoot, criteriaQuery, criteria.getField().attribute,
                criteria.getOrder());

        TypedQuery<GiftCertificate> typedQuery = entityManager.createQuery(criteriaQuery);
        List<GiftCertificate> resultList = typedQuery.setFirstResult((pageNumber - 1) * pageSize)
                .setMaxResults(pageSize)
                .getResultList();

       return new PagedDTO<>(resultList, metadata);
    }

    private Predicate createPredicates(CertificateCriteria criteria, Root<GiftCertificate> gcRoot){
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        Predicate conditions = criteriaBuilder.conjunction();
        conditions = criteriaBuilder.and(conditions, criteriaBuilder.equal(gcRoot.get(GiftCertificate_.deleted),false));
        if (!criteria.getNamePart().isBlank()){
            conditions = criteriaBuilder.and(conditions, criteriaBuilder.like(gcRoot.get(GiftCertificate_.name),"%"
                    + criteria.getNamePart() + "%"));
        }
        if (!criteria.getDescriptionPart().isBlank()){
            conditions = criteriaBuilder.and(conditions, criteriaBuilder.like(gcRoot.get(GiftCertificate_.description),"%"
                    + criteria.getDescriptionPart() + "%"));
        }
        conditions = criteriaBuilder.and(conditions, criteriaBuilder.between(gcRoot.get(GiftCertificate_.price),
                criteria.getMinPrice(), criteria.getMaxPrice()));
        conditions = criteriaBuilder.and(conditions,  criteriaBuilder.between(gcRoot.get(GiftCertificate_.createDate),
                criteria.getMinCreateDate(), criteria.getMaxCreateDate()));

        return conditions;
    }

    private Predicate createTagsMatchingPredicate(CertificateCriteria criteria, Root<GiftCertificate> gcRoot) throws DataNotExistRepositoryException {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        Expression<Set<Tag>> tags = gcRoot.get(GiftCertificate_.tags);
        Predicate tagsPredicate = criteriaBuilder.conjunction();
        for (String name : criteria.getTagNames()){
            Tag tag = tagRepository.findTagByName(name).orElseThrow(DataNotExistRepositoryException::new);
            tagsPredicate = criteriaBuilder.and(tagsPredicate, criteriaBuilder.isMember(tag, tags));
        }
        return tagsPredicate;
    }
}
