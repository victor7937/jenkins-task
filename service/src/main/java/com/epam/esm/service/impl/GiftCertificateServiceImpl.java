package com.epam.esm.service.impl;

import com.epam.esm.criteria.CertificateCriteria;
import com.epam.esm.dto.CertificateDTO;
import com.epam.esm.dto.PagedDTO;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.GiftCertificate_;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.*;
import com.epam.esm.repository.GiftCertificateRepository;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.service.GiftCertificateService;
import com.epam.esm.util.PartialUpdater;
import com.epam.esm.validator.CriteriaValidator;
import com.epam.esm.validator.ServiceValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
public class GiftCertificateServiceImpl implements GiftCertificateService {



    private final GiftCertificateRepository giftCertificateRepository;

    private final TagRepository tagRepository;

    private final ServiceValidator<CertificateDTO> validator;

    private final CriteriaValidator<CertificateCriteria> criteriaValidator;


    private static final String NAME_AFTER_DELETE = "DELETED";
    private static final String INVALID_ID_MSG = "Certificate id is invalid";
    private static final String NOT_EXIST_MSG = "Gift Certificate id with number %s doesn't exist";
    private static final String ALREADY_DELETED_MSG = "The certificate with number %s has already been deleted";
    private static final String INCORRECT_CERTIFICATE_MSG = "Incorrect certificate data";
    private static final String INCORRECT_PARAMS_MSG = "Incorrect request parameter values";

    @Autowired
    public GiftCertificateServiceImpl(GiftCertificateRepository giftCertificateRepository, TagRepository tagRepository, ServiceValidator<CertificateDTO> validator,
                                      CriteriaValidator<CertificateCriteria> criteriaValidator) {
        this.giftCertificateRepository = giftCertificateRepository;
        this.tagRepository = tagRepository;
        this.validator = validator;
        this.criteriaValidator = criteriaValidator;
    }


    @Override
    @Transactional
    public PagedDTO<GiftCertificate> get(CertificateCriteria certificateCriteria, int pageSize, int pageNumber){
        if (!(validator.isPageParamsValid(pageSize, pageNumber) && criteriaValidator.validateCriteria(certificateCriteria))){
            throw new IncorrectDataServiceException(INCORRECT_PARAMS_MSG);
        }

        PagedDTO<GiftCertificate> pagedDTO;
        try {
            pagedDTO = giftCertificateRepository.getByCriteria(certificateCriteria, pageSize, pageNumber);
        } catch (IncorrectPageRepositoryException e) {
            return new PagedDTO<>();
        }
        return pagedDTO;
    }

    @Override
    @Transactional
    public GiftCertificate getById(Long id){
        if (!validator.isLongIdValid(id)){
            throw new IncorrectDataServiceException(INVALID_ID_MSG);
        }
        return giftCertificateRepository.findById(id).orElseThrow(() -> new NotFoundServiceException(String.format(NOT_EXIST_MSG, id)));
    }

    @Override
    @Transactional
    public GiftCertificate add(CertificateDTO certificateDTO){
        if (!validator.validate(certificateDTO)){
            throw new IncorrectDataServiceException(INCORRECT_CERTIFICATE_MSG);
        }

        GiftCertificate giftCertificate = new GiftCertificate(certificateDTO.getName(), certificateDTO.getDescription(),
                certificateDTO.getPrice(), certificateDTO.getDuration());
        Set<Tag> tagsForAdding = certificateDTO.getTags();
        tagsForAdding.stream().filter(t -> !tagRepository.existsByName(t.getName())).forEach(giftCertificate::addTag);

        giftCertificateRepository.save(giftCertificate);

        tagsForAdding.stream().filter(t -> !giftCertificate.getTags().contains(t)).map(t -> tagRepository.findTagByName(t.getName()).get())
                .forEach(giftCertificate::addTag);

        return giftCertificate;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!validator.isLongIdValid(id)){
            throw new IncorrectDataServiceException(INVALID_ID_MSG);
        }
        GiftCertificate giftCertificate = giftCertificateRepository.findById(id).orElseThrow(() ->
                new NotFoundServiceException(String.format(NOT_EXIST_MSG, id)));
        boolean deleted = giftCertificate.getDeleted();
        if (deleted){
            throw new AlreadyExistServiceException(String.format(ALREADY_DELETED_MSG, id));
        }
        giftCertificate.setDeleted(true);
        giftCertificate.setName(NAME_AFTER_DELETE);
        giftCertificate.setDescription(null);
        giftCertificate.setPrice(null);
        giftCertificate.setDuration(null);
        giftCertificate.setTags(Collections.emptySet());
    }

    @Override
    @Transactional
    public GiftCertificate update(CertificateDTO modified, Long id) {
        if (!(validator.validate(modified) && validator.isLongIdValid(id))){
            throw new IncorrectDataServiceException(INCORRECT_CERTIFICATE_MSG);
        }

        GiftCertificate current = giftCertificateRepository.findByIdAndDeletedIsFalse(id).orElseThrow(()
                -> new NotFoundServiceException(String.format(NOT_EXIST_MSG, id)));

        PartialUpdater<GiftCertificate, CertificateDTO> partialUpdater = new PartialUpdater<>(current, modified,
                List.of(GiftCertificate_.NAME, GiftCertificate_.DESCRIPTION, GiftCertificate_.PRICE, GiftCertificate_.DURATION));
        try {
            partialUpdater.generatePartialUpdateData();
        } catch (PartialUpdateException e) {
            throw new ServiceException(e);
        }

        modified.getTags().stream().filter(tag -> !current.getTags().contains(tag))
                .map(t -> tagRepository.findTagByName(t.getName()).orElse(t))
                .forEach(current::addTag);
        current.getTags().retainAll(modified.getTags());

        return current;
    }

}
