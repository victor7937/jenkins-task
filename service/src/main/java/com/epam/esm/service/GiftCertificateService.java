package com.epam.esm.service;

import com.epam.esm.dto.CertificateDTO;
import com.epam.esm.dto.PagedDTO;
import com.epam.esm.criteria.CertificateCriteria;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.exception.IncorrectDataServiceException;
import com.epam.esm.exception.NotFoundServiceException;
import com.epam.esm.exception.ServiceException;


/**
 * Service for manipulating gift certificates data
 */
public interface GiftCertificateService {

    /**
     * Gets page of gift certificates
     * @param certificateCriteria - criteria with params for filtering and sorting
     * @param pageSize - size of one page
     * @param pageNumber - number of a current page
     * @return page with certificates found
     * @throws IncorrectDataServiceException if criteria or pagination params are incorrect
     */
    PagedDTO<GiftCertificate> get (CertificateCriteria certificateCriteria, int pageSize, int pageNumber);

    /**
     * Get one gift certificate if id is correct
     * @param id - id of gift certificate
     * @return certificate found
     * @throws NotFoundServiceException if no such certificate
     * @throws IncorrectDataServiceException if id is incorrect
     */
    GiftCertificate getById(Long id);

    /**
     * Add new gift certificate
     * @param certificateDTO - certificate dto with necessary fields for adding
     * @return Added gift certificate with new generated data
     * @throws IncorrectDataServiceException if params in CertificateDTO are incorrect
     */
    GiftCertificate add(CertificateDTO certificateDTO);

    /**
     * Delete gift certificate
     * @param id - id of gift certificate for deleting
     * @throws NotFoundServiceException if no such certificate
     * @throws IncorrectDataServiceException if id is incorrect
     */
    void delete(Long id);

    /**
     * @param modified - gift certificate dto that contains modified fields
     * @param id - id of certificate for modifying
     * @return modified gift certificate with some generated data
     * @throws IncorrectDataServiceException if params for updating are invalid
     */
    GiftCertificate update(CertificateDTO modified, Long id);


}
