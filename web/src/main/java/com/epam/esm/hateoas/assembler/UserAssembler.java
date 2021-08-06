package com.epam.esm.hateoas.assembler;

import com.epam.esm.controller.UserController;
import com.epam.esm.criteria.Criteria;
import com.epam.esm.entity.User;
import com.epam.esm.hateoas.model.UserModel;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserAssembler extends RepresentationModelAssemblerSupport<User, UserModel> implements Pageable<User, UserModel> {

    private static final int FIRST = 1;

    private final ModelMapper modelMapper;

    @Autowired
    public UserAssembler(ModelMapper modelMapper) {
        super(UserController.class, UserModel.class);
        this.modelMapper = modelMapper;
    }

    @Override
    public UserModel toModel(User entity) {
        UserModel model = instantiateModel(entity);
        modelMapper.map(entity, model);
        Link selfRel = linkTo(methodOn(UserController.class).getById(entity.getId())).withSelfRel();
        Link ordersRel = linkTo(methodOn(UserController.class).getOrdersOfUser(entity.getId(),10, FIRST,
                new HashMap<>())).withRel("orders");
        model.add(selfRel, ordersRel);
        return model;
    }


    @Override
    public PagedModel<UserModel> toPagedModel(Collection<? extends User> entities, PagedModel.PageMetadata metadata, Criteria criteria) {
        PagedModel<UserModel> pagedModel = PagedModel.of(entities.stream().map(this::toModel).collect(Collectors.toList()), metadata);
        addToPagesLinks(pagedModel, metadata, criteria.getCriteriaAsMap());
        return pagedModel;
    }

    private void addToPagesLinks(PagedModel<UserModel> pagedModel, PagedModel.PageMetadata metadata , Map<String, String> params){
        int size = (int) metadata.getSize();
        int number = (int) metadata.getNumber();
        pagedModel.add(linkTo(methodOn(UserController.class).getUsers(size, number, params)).withRel(IanaLinkRelations.CURRENT));
        if (metadata.getNumber() < metadata.getTotalPages()){
            pagedModel.add(linkTo(methodOn(UserController.class).getUsers(size, number + FIRST, params)).withRel(IanaLinkRelations.NEXT));
        }
        if (metadata.getNumber() != FIRST){
            pagedModel.add(linkTo(methodOn(UserController.class).getUsers(size, number - FIRST, params)).withRel(IanaLinkRelations.PREV));
        }
        pagedModel.add(linkTo(methodOn(UserController.class).getUsers(size, FIRST, params)).withRel(IanaLinkRelations.FIRST));
        pagedModel.add(linkTo(methodOn(UserController.class).getUsers(size, (int) metadata.getTotalPages(), params)).withRel(IanaLinkRelations.LAST));
    }


}
