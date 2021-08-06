package com.epam.esm.criteria;


import com.epam.esm.entity.User_;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Getter
public class UserCriteria extends Criteria {

    private String firstNamePart;

    private String lastNamePart;

    private SortingField sortingField;

    private SortingOrder sortingOrder;

    private UserCriteria(Optional<SortingField> sortingField, Optional<SortingOrder> sortingOrder, Optional<String> firstNamePart,
                        Optional<String> lastNamePart){
        this.sortingField = sortingField.orElse(SortingField.ID);
        this.sortingOrder = sortingOrder.orElse(SortingOrder.ASC);
        this.firstNamePart = firstNamePart.orElse("");
        this.lastNamePart = lastNamePart.orElse("");
    }

    public static UserCriteria createCriteria(Map<String, String> criteriaParams) {
        Optional<SortingField> sortingField = enumOf(criteriaParams.get(RequestParams.SORT.value), SortingField.class);
        Optional<SortingOrder> order = enumOf(criteriaParams.get(RequestParams.ORDER.value), SortingOrder.class);

        Optional<String> namePart = Optional.ofNullable(criteriaParams.get(RequestParams.FIRST_NAME.value));
        Optional<String> surnamePart = Optional.ofNullable(criteriaParams.get(RequestParams.LAST_NAME.value));

        return new UserCriteria(sortingField, order, namePart, surnamePart);
    }

    public enum SortingField {

        FIRST_NAME(User_.FIRST_NAME), LAST_NAME(User_.LAST_NAME), EMAIL(User_.EMAIL), ID(User_.ID);

        public final String attribute;

        SortingField(String attribute) {
            this.attribute = attribute;
        }
    }

    public enum RequestParams{
        FIRST_NAME("first_name"), LAST_NAME("last_name"), SORT("sort"), ORDER("order");

        public final String value;

        RequestParams(String value) {
            this.value = value;
        }
    }

    @Override
    public Map<String, String> getCriteriaAsMap(){
        Map<String, String> paramsMap = new LinkedHashMap<>();
        if (!firstNamePart.isBlank()){
            paramsMap.put(RequestParams.FIRST_NAME.value, firstNamePart);
        }
        if (!lastNamePart.isBlank()){
            paramsMap.put(RequestParams.LAST_NAME.value, lastNamePart);
        }
        paramsMap.put(RequestParams.ORDER.value, sortingOrder.toString().toLowerCase());
        paramsMap.put(RequestParams.SORT.value, sortingField.toString().toLowerCase());
        return paramsMap;
    }

}
