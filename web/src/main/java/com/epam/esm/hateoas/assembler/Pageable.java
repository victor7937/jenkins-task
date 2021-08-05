package com.epam.esm.hateoas.assembler;

import com.epam.esm.criteria.Criteria;
import org.springframework.hateoas.PagedModel;

import java.util.Collection;

public interface Pageable <E,M> {
    PagedModel<M> toPagedModel(Collection<? extends E> entities, PagedModel.PageMetadata metadata, Criteria criteria);
}
