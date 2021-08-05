package com.epam.esm.hateoas.assembler;

import com.epam.esm.controller.GiftCertificateController;
import com.epam.esm.criteria.Criteria;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.hateoas.model.GiftCertificateModel;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class GiftCertificateAssembler extends RepresentationModelAssemblerSupport<GiftCertificate, GiftCertificateModel> implements Pageable<GiftCertificate, GiftCertificateModel> {

    private final ModelMapper modelMapper;

    private static final int FIRST = 1;

    @Autowired
    public GiftCertificateAssembler(ModelMapper modelMapper) {
        super(GiftCertificateController.class, GiftCertificateModel.class);
        this.modelMapper = modelMapper;
    }

    @Override
    public GiftCertificateModel toModel(GiftCertificate entity) {
        GiftCertificateModel model = instantiateModel(entity);
        modelMapper.map(entity, model);
        Link self = linkTo(methodOn(GiftCertificateController.class).getCertificateById(entity.getId())).withSelfRel();
        model.add(self);
        return model;
    }

    @Override
    public PagedModel<GiftCertificateModel> toPagedModel(Collection<? extends GiftCertificate> entities, PagedModel.PageMetadata metadata, Criteria criteria) {
        PagedModel<GiftCertificateModel> pagedModel = PagedModel.of(entities.stream().map(this::toModel).collect(Collectors.toList()), metadata);
        addToPagesLinks(pagedModel, metadata, criteria.getCriteriaAsMap());
        return pagedModel;
    }

    private void addToPagesLinks(PagedModel<GiftCertificateModel> pagedModel, PagedModel.PageMetadata metadata , Map<String, String> params){
        int size = (int) metadata.getSize();
        int number = (int) metadata.getNumber();
        pagedModel.add(linkTo(methodOn(GiftCertificateController.class).getCertificates(number, size, params)).withRel(IanaLinkRelations.CURRENT));
        if (metadata.getNumber() < metadata.getTotalPages()){
            pagedModel.add(linkTo(methodOn(GiftCertificateController.class).getCertificates(number + FIRST, size, params)).withRel(IanaLinkRelations.NEXT));
        }
        if (metadata.getNumber() != FIRST){
            pagedModel.add(linkTo(methodOn(GiftCertificateController.class).getCertificates(number - FIRST, size, params)).withRel(IanaLinkRelations.PREV));
        }
        pagedModel.add(linkTo(methodOn(GiftCertificateController.class).getCertificates(FIRST, size, params)).withRel(IanaLinkRelations.FIRST));
        pagedModel.add(linkTo(methodOn(GiftCertificateController.class).getCertificates((int) metadata.getTotalPages(), size, params)).withRel(IanaLinkRelations.LAST));
    }
}
