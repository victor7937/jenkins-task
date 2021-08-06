package com.epam.esm.hateoas.assembler;

import com.epam.esm.controller.GiftCertificateController;
import com.epam.esm.controller.UserController;
import com.epam.esm.criteria.Criteria;
import com.epam.esm.entity.Order;
import com.epam.esm.hateoas.model.OrderModel;
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
public class OrderAssembler extends RepresentationModelAssemblerSupport<Order, OrderModel> implements Pageable<Order, OrderModel> {

    private final ModelMapper modelMapper;

    private static final int FIRST = 1;

    @Autowired
    public OrderAssembler(ModelMapper modelMapper) {
        super(UserController.class, OrderModel.class);
        this.modelMapper = modelMapper;
    }

    @Override
    public OrderModel toModel(Order entity) {
        OrderModel model = instantiateModel(entity);
        modelMapper.map(entity, model);
        Link self = linkTo(methodOn(UserController.class).getOrderOfUser(entity.getUser().getId(), entity.getId())).withSelfRel();
        Link userLink = linkTo(methodOn(UserController.class).getById(entity.getUser().getId())).withRel("user");
        Link certificateLink = linkTo(methodOn(GiftCertificateController.class).getCertificateById(entity.getGiftCertificate()
                .getId())).withRel("certificate");
        model.add(self, userLink, certificateLink);

        return model;
    }

    @Override
    public PagedModel<OrderModel> toPagedModel(Collection<? extends Order> entities, PagedModel.PageMetadata metadata, Criteria criteria) {
        PagedModel<OrderModel> pagedModel = PagedModel.of(entities.stream().map(this::toModel).collect(Collectors.toList()), metadata);
        Order order = entities.stream().collect(Collectors.toList()).get(0);
        addToPagesLinks(pagedModel, order.getUser().getId(), metadata,criteria.getCriteriaAsMap());
        return pagedModel;
    }

    private void addToPagesLinks(PagedModel<OrderModel> pagedModel, Long id, PagedModel.PageMetadata metadata , Map<String, String> params){
        int size = (int) metadata.getSize();
        int number = (int) metadata.getNumber();
        pagedModel.add(linkTo(methodOn(UserController.class).getOrdersOfUser(id, size, number, params)).withRel(IanaLinkRelations.CURRENT));
        if (metadata.getNumber() < metadata.getTotalPages()){
            pagedModel.add(linkTo(methodOn(UserController.class).getOrdersOfUser(id, size, number + FIRST, params)).withRel(IanaLinkRelations.NEXT));
        }
        if (metadata.getNumber() != FIRST){
            pagedModel.add(linkTo(methodOn(UserController.class).getOrdersOfUser(id, size, number - FIRST, params)).withRel(IanaLinkRelations.PREV));
        }
        pagedModel.add(linkTo(methodOn(UserController.class).getOrdersOfUser(id, size, FIRST, params)).withRel(IanaLinkRelations.FIRST));
        pagedModel.add(linkTo(methodOn(UserController.class).getOrdersOfUser(id, size, (int) metadata.getTotalPages(), params)).withRel(IanaLinkRelations.LAST));
    }
}
