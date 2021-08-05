package com.epam.esm.criteria;

import org.apache.commons.lang3.EnumUtils;

import java.util.Map;
import java.util.Optional;

public abstract class Criteria {

    public abstract Map<String, String> getCriteriaAsMap();

    protected static <T extends Enum<T>> Optional<T> enumOf(String value, Class<T> tClass){
        return Optional.ofNullable(value)
                .filter(s -> EnumUtils.isValidEnum(tClass, s.toUpperCase()))
                .map(s -> T.valueOf(tClass, s.toUpperCase()));
    }

}
