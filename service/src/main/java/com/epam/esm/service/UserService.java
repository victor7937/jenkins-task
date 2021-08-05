package com.epam.esm.service;

import com.epam.esm.criteria.UserCriteria;
import com.epam.esm.dto.PagedDTO;
import com.epam.esm.dto.UserDTO;
import com.epam.esm.entity.User;
import com.epam.esm.exception.IncorrectDataServiceException;
import com.epam.esm.exception.NotFoundServiceException;
import com.epam.esm.exception.ServiceException;


/**
 * Service for manipulating users data
 */
public interface UserService {

    /**
     * Gets page with users
     * @param criteria - criteria with params for filtering and sorting
     * @param pageSize - size of one page
     * @param pageNumber - number of a current page
     * @return page with users found
     * @throws IncorrectDataServiceException if criteria or pagination params are incorrect
     */
    PagedDTO<User> get(UserCriteria criteria, int pageSize, int pageNumber);

    /**
     * Gets user by its email
     * @param email email of a user
     * @return user found
     * @throws IncorrectDataServiceException if email is incorrect
     * @throws NotFoundServiceException if user wasn't found
     */
    User getByEmail(String email);


    /**
     * Gets user by its id
     * @param id - id of a user
     * @return user found
     * @throws IncorrectDataServiceException if id is incorrect
     * @throws NotFoundServiceException if user wasn't found
     */
    User getById(Long id);

    /**
     * Add new user if its data is valid
     * @param userDTO - users data for registration
     * @throws IncorrectDataServiceException if users data is incorrect
     */
    void registration(UserDTO userDTO);
}
