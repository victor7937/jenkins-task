package com.epam.esm.repository;

import com.epam.esm.criteria.CertificateCriteria;
import com.epam.esm.dto.PagedDTO;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.exception.IncorrectPageRepositoryException;


/**
 * Repository for getting paged certificates data
 */
public interface PagingAndFilteringCertificateRepository {

    /**
     * Get gift certificates with some criteria
     * @param criteria - searching certificates criteria
     * @param pageSize - size of one page
     * @param pageNumber - number of a current page
     * @throws IncorrectPageRepositoryException if the page wasn't found
     * @return page with certificates which match the criteria
     */
    PagedDTO<GiftCertificate> getByCriteria(CertificateCriteria criteria, int pageSize, int pageNumber) throws IncorrectPageRepositoryException;
}
