package com.epam.esm.repository;

import com.epam.esm.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


/**
 * Repository for manipulating tag data in database
 */
@Repository
public interface TagRepository extends JpaRepository<Tag,Long> {

    /**
     * Gets tag by its name from database if it exists
     * @param name - name of the tag
     * @return optional that contains a tag or empty Optional if it wasn't found
     */
    Optional<Tag> findTagByName(String name);


    /**
     * Checks if tag name exists
     * @param name - name of the tag
     * @return true if tag exists, else false
     */
    boolean existsByName(String name);

    /**
     * Gets page of tags by name part
     * @param namePart - part of tags name
     * @param pageable - contains info about page size, current page and sorting params
     * @return page of tags
     */
    Page<Tag> getTagsByNameLike(String namePart, Pageable pageable);


    /**
     * Gets the most widely used tag of a user with the highest cost of all orders from database
     * @return Tag found
     */
    @Query(value = "SELECT t.id, t.name, COUNT(t.id) as t_count from users u" +
            " join orders o on u.id = o.users_id" +
            " join gift_certificate gc on gc.id = o.certificate_id" +
            " join m2m_certificate_tag m2mct on gc.id = m2mct.cert_id" +
            " join tag t on t.id = m2mct.tag_id where u.id =" +
            " (SELECT s.u_id from (SELECT SUM(o.cost) as s_cost, u.id as u_id from orders o" +
            " join users u on u.id = o.users_id group by u_id order by s_cost DESC LIMIT 1) s)" +
            " group by t.name order by t_count desc LIMIT 1", nativeQuery = true)
    Tag getMostUsedTagOfValuableCustomer ();

}
