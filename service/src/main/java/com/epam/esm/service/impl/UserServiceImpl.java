package com.epam.esm.service.impl;

import com.epam.esm.criteria.SortingOrder;
import com.epam.esm.criteria.UserCriteria;
import com.epam.esm.dto.PagedDTO;
import com.epam.esm.dto.UserDTO;
import com.epam.esm.entity.User;
import com.epam.esm.entity.User_;
import com.epam.esm.exception.AlreadyExistServiceException;
import com.epam.esm.exception.IncorrectDataServiceException;
import com.epam.esm.exception.NotFoundServiceException;
import com.epam.esm.repository.UserRepository;
import com.epam.esm.service.UserService;
import com.epam.esm.validator.CriteriaValidator;
import com.epam.esm.validator.ServiceValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.hateoas.PagedModel;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;


@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CriteriaValidator<UserCriteria> criteriaValidator;
    private final ServiceValidator<UserDTO> serviceValidator;

    private static final String NOT_EXIST_EMAIL_MSG = "User with email %s doesn't exist";
    private static final String NOT_EXIST_MSG = "User with id %s doesn't exist";
    private static final String INCORRECT_PARAMS_MSG = "Incorrect request parameter values";
    private static final String INCORRECT_USERS_EMAIL_MSG = "Users email is incorrect";
    private static final String INCORRECT_USER_MSG = "Users id is incorrect";
    private static final String PASSWORD_IS_INVALID_MSG = "password is invalid";
    private static final String ALREADY_EXISTS_EMAIL_MSG = "Email %s already exists";


    @Autowired
    public UserServiceImpl(UserRepository userRepository, CriteriaValidator<UserCriteria> criteriaValidator, ServiceValidator<UserDTO> serviceValidator) {
        this.userRepository = userRepository;
        this.criteriaValidator = criteriaValidator;
        this.serviceValidator = serviceValidator;
    }

    @Override
    @Transactional
    public PagedDTO<User> get(UserCriteria criteria, int pageSize, int pageNumber){
        if (!(criteriaValidator.validateCriteria(criteria) && serviceValidator.isPageParamsValid(pageSize, pageNumber))){
            throw new IncorrectDataServiceException(INCORRECT_PARAMS_MSG);
        }

        Sort sort = Sort.by(criteria.getSortingField().attribute);
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, criteria.getSortingOrder() == SortingOrder.DESC ?
                sort.descending() : sort.ascending());
        ExampleMatcher usersMatcher = ExampleMatcher.matchingAll()
                .withMatcher(User_.FIRST_NAME, ExampleMatcher.GenericPropertyMatcher.of(ExampleMatcher.StringMatcher.STARTING, true))
                .withMatcher(User_.LAST_NAME, ExampleMatcher.GenericPropertyMatcher.of(ExampleMatcher.StringMatcher.STARTING, true))
                .withIgnorePaths(User_.ACTIVE, User_.ORDERS);
        Example<User> example = Example.of(new User(null, criteria.getFirstNamePart(), criteria.getLastNamePart()), usersMatcher);

        Page<User> usersPage = userRepository.findAll(example, pageable);
        return new PagedDTO<>(usersPage.get().collect(Collectors.toList()), new PagedModel.PageMetadata(
                pageSize, pageNumber, usersPage.getTotalElements(), usersPage.getTotalPages()));
    }

    @Override
    @Transactional
    public User getByEmail(String email){
        if (!serviceValidator.isStringIdValid(email)){
            throw new IncorrectDataServiceException(INCORRECT_USERS_EMAIL_MSG);
        }
        return userRepository.findByEmail(email).orElseThrow(() -> new NotFoundServiceException(String.format(NOT_EXIST_EMAIL_MSG, email)));
    }

    @Override
    @Transactional
    public User getById(Long id){
        if (!serviceValidator.isLongIdValid(id)){
            throw new IncorrectDataServiceException(INCORRECT_USER_MSG);
        }
        return userRepository.findById(id).orElseThrow(() -> new NotFoundServiceException(String.format(NOT_EXIST_MSG, id)));
    }

    @Override
    @Transactional
    public void registration(UserDTO userDTO){
        if (!serviceValidator.validate(userDTO)){
            throw new IncorrectDataServiceException(INCORRECT_PARAMS_MSG);
        }
        if (userRepository.existsUserByEmail(userDTO.getEmail())){
            throw new AlreadyExistServiceException(String.format(ALREADY_EXISTS_EMAIL_MSG, userDTO.getEmail()));
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
        User user = new User(userDTO.getEmail(), passwordEncoder.encode(userDTO.getPassword()), userDTO.getFirstName(), userDTO.getLastName());
        userRepository.save(user);
    }
}
