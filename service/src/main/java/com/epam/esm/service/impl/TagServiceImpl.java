package com.epam.esm.service.impl;

import com.epam.esm.dto.PagedDTO;
import com.epam.esm.entity.Tag;
import com.epam.esm.entity.Tag_;
import com.epam.esm.exception.*;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.service.TagService;
import com.epam.esm.validator.ServiceValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;


@Service
public class TagServiceImpl implements TagService{


    private final TagRepository tagRepository;
    private final ServiceValidator<Tag> validator;

    private static final String NOT_EXIST_MSG = "Tag id with number %s doesn't exist";
    private static final String ALREADY_EXIST_MSG = "Tag with name %s already exists";
    private static final String INVALID_PAGE_PARAMS_MSG = "Page params are invalid";
    private static final String INVALID_ID_MSG = "Tag id is invalid";
    private static final String INCORRECT_TAG_MSG = "Tag data is incorrect";


    @Autowired
    public TagServiceImpl(TagRepository tagRepository, ServiceValidator<Tag> validator) {
        this.tagRepository = tagRepository;
        this.validator = validator;
    }

    @Override
    @Transactional
    public PagedDTO<Tag> get(String namePart, int pageSize, int pageNumber){
        if (!validator.isPageParamsValid(pageSize, pageNumber)){
            throw new IncorrectDataServiceException(INVALID_PAGE_PARAMS_MSG);
        }

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by(Tag_.NAME));
        Page<Tag> tagPage = tagRepository.getTagsByNameLike("%" + namePart + "%", pageable);
        return new PagedDTO<>(tagPage.get().collect(Collectors.toList()), new PagedModel.PageMetadata(
                pageSize, pageNumber, tagPage.getTotalElements(), tagPage.getTotalPages()));
    }

    @Override
    @Transactional
    public Tag getById(Long id){
        if (!validator.isLongIdValid(id)){
            throw new IncorrectDataServiceException(INVALID_ID_MSG);
        }
        return tagRepository.findById(id).orElseThrow(() -> new NotFoundServiceException(String.format(NOT_EXIST_MSG, id)));
    }

    @Override
    @Transactional
    public Tag add(Tag tag){
        if (!validator.validate(tag)){
            throw new IncorrectDataServiceException(INCORRECT_TAG_MSG);
        }
        if (tagRepository.existsByName(tag.getName())){
            throw new AlreadyExistServiceException(String.format(ALREADY_EXIST_MSG, tag.getName()));
        }
        return tagRepository.save(tag);
    }

    @Override
    @Transactional
    public void delete(Long id){
        if (!validator.isLongIdValid(id)){
            throw new IncorrectDataServiceException(INVALID_ID_MSG);
        }
        if (!tagRepository.existsById(id)){
            throw new NotFoundServiceException(String.format(NOT_EXIST_MSG, id));
        }
        tagRepository.deleteById(id);
    }

    @Override
    public Tag getMostUsedTagOfValuableCustomer() {
        return tagRepository.getMostUsedTagOfValuableCustomer();
    }
}
