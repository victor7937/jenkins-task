package com.epam.esm.repository;

import com.epam.esm.entity.GiftCertificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for manipulating certificate data in database
 */
@Repository
public interface GiftCertificateRepository extends JpaRepository<GiftCertificate, Long>, PagingAndFilteringCertificateRepository {

    /**
     * Find certificate by its id if it is not deleted
     * @param id - id of the certificate
     * @return Optional that contains a certificate or empty optional if it is not found or deleted
     */
    Optional<GiftCertificate> findByIdAndDeletedIsFalse(Long id);
}
