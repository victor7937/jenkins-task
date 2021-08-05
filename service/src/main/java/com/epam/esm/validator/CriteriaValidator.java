package com.epam.esm.validator;


/**
 * Validator for criteria objects
 * @param <T> type of criteria
 */
public interface CriteriaValidator <T> {

    /**
     * @param criteria criteria for validating
     * @return true if criteria is valid, else false
     */
    boolean validateCriteria (T criteria);
}
