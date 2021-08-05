package com.epam.esm.criteria;


import com.epam.esm.entity.Order_;
import lombok.Getter;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.math.NumberUtils;


import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Getter
public class OrderCriteria extends Criteria{

    private static final LocalDateTime MIN_DATE_TIME = LocalDateTime.parse("2021-06-01T00:00:00");
    private static final LocalDateTime MAX_DATE_TIME = LocalDateTime.parse("2025-01-01T00:00:00");


    private SortingField sortingField;

    private SortingOrder sortingOrder;

    private Float minCost;

    private Float maxCost;

    private LocalDateTime minTime;

    private LocalDateTime maxTime;

    private OrderCriteria(Optional<SortingField> sortingField, Optional<SortingOrder> sortingOrder, Optional<Float> minCost,
                         Optional<Float> maxCost, Optional<LocalDateTime> minTime, Optional<LocalDateTime> maxTime){
        this.sortingField = sortingField.orElse(SortingField.ID);
        this.sortingOrder = sortingOrder.orElse(SortingOrder.ASC);
        this.minCost = minCost.orElse(0.0f);
        this.maxCost = maxCost.orElse(Float.MAX_VALUE);
        this.minTime = minTime.orElse(MIN_DATE_TIME);
        this.maxTime = maxTime.orElse(MAX_DATE_TIME);

    }

    public static OrderCriteria createCriteria(Map<String, String> criteriaParams) {
        Optional<SortingField> sortingField = enumOf(criteriaParams.get(RequestParams.SORT.value), SortingField.class);
        Optional<SortingOrder> order = enumOf(criteriaParams.get(RequestParams.ORDER.value), SortingOrder.class);

        Optional<Float> minCost = Optional.ofNullable(criteriaParams.get(RequestParams.COST_GTE.value))
                .filter(NumberUtils::isCreatable)
                .map(Float::parseFloat);

        Optional<Float> maxCost = Optional.ofNullable(criteriaParams.get(RequestParams.COST_LTE.value))
                .filter(NumberUtils::isCreatable)
                .map(Float::parseFloat);

        Optional<LocalDateTime> minTime;
        try {
            minTime = Optional.ofNullable(criteriaParams.get(RequestParams.TIME_GTE.value)).map(LocalDateTime::parse);
        } catch (DateTimeParseException e){
            minTime = Optional.empty();
        }

        Optional<LocalDateTime> maxTime;
        try {
            maxTime = Optional.ofNullable(criteriaParams.get(RequestParams.TIME_LTE.value)).map(LocalDateTime::parse);
        } catch (DateTimeParseException e){
            maxTime = Optional.empty();
        }

        return new OrderCriteria(sortingField, order, minCost, maxCost, minTime, maxTime);
    }

    public enum SortingField {
        ID(Order_.ID), COST(Order_.COST), TIME(Order_.TIME_OF_PURCHASE);

        public final String attribute;

        SortingField(String attribute) {
            this.attribute = attribute;
        }
    }

    public enum RequestParams{
        COST_LTE("cost-lte"), COST_GTE("cost-gte"), TIME_LTE("time-lte"), TIME_GTE("time-gte"),
        SORT("sort"), ORDER("order");

        public final String value;

        RequestParams(String value) {
            this.value = value;
        }
    }

    @Override
    public Map<String, String> getCriteriaAsMap() {
        Map<String, String> paramsMap = new LinkedHashMap<>();

        if (minCost != 0.0f) {
            paramsMap.put(RequestParams.COST_GTE.value, minCost.toString());
        }
        if (maxCost != Float.MAX_VALUE) {
            paramsMap.put(RequestParams.COST_LTE.value, maxCost.toString());
        }

        if (!minTime.isEqual(MIN_DATE_TIME)) {
            paramsMap.put(RequestParams.TIME_GTE.value, minTime.toString());
        }
        if (!maxTime.isEqual(MAX_DATE_TIME)) {
            paramsMap.put(RequestParams.TIME_LTE.value, maxTime.toString());
        }
        paramsMap.put(RequestParams.ORDER.value, sortingOrder.toString().toLowerCase());
        paramsMap.put(RequestParams.SORT.value, sortingField.toString().toLowerCase());
        return paramsMap;
    }

}

