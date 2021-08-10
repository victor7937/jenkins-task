package com.epam.esm.validator;


/**
 * Interface for validation data at the service layer.
 * Validates some model that goes through the service layer, its id and page params.
 * @param <T> - model type for validation
 */
public interface ServiceValidator <T> {

    /**
     * Validates all fields of a model
     * @param model - model object for checking
     * @return - true if model is correct, else false
     */
    boolean validate(T model);

    /**
     * Default validating of long type id of some entity
     * @param id - id field of long type for validating
     * @return true if id is valid, else false
     */
    default boolean isLongIdValid(Long id) {
        return id != null && id > 0L;
    }

    /**
     * Default validating of string type id of some entity
     * @param id - string id field for validating
     * @return true if id is valid, else false
     */
    default boolean isStringIdValid(String id) {
        return id != null && !id.isBlank();
    }


    /**
     * Default validating for pagination params
     * @param pageSize - size of page
     * @param pageNumber - current page number
     * @return true if page params are valid, else false
     */
    default boolean isPageParamsValid(int pageSize, int pageNumber){
        return pageSize > 0 && pageNumber > 0;
    }
}
