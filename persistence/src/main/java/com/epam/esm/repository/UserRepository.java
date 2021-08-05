package com.epam.esm.repository;

import com.epam.esm.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


/**
 * Repository for manipulating users data in database
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>{

    /**
     * Gets user by its email if the user exists
     * @param email - email of a user
     * @return optional that contains a user or empty Optional if it wasn't found
     */
    Optional<User> findByEmail(String email);


    /**
     * Checks if such user is exist by its email
     * @param email - email of a user
     * @return true if user exists, else false
     */
    boolean existsUserByEmail(String email);
}
