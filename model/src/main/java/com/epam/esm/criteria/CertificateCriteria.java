package com.epam.esm.criteria;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.GiftCertificate_;
import lombok.Getter;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.math.NumberUtils;

import javax.persistence.metamodel.SingularAttribute;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * Criteria for searching gift certificates
 */
@Getter
public class CertificateCriteria extends Criteria {

    private static final LocalDateTime MIN_DATE_TIME = LocalDateTime.parse("2021-06-01T00:00:00");
    private static final LocalDateTime MAX_DATE_TIME = LocalDateTime.parse("2025-01-01T00:00:00");

    private Set<String> tagNames;

    private String namePart;

    private String descriptionPart;

    private SortingField field;

    private SortingOrder order;

    private Float minPrice;

    private Float maxPrice;

    private LocalDateTime minCreateDate;

    private LocalDateTime maxCreateDate;

    private CertificateCriteria(Optional<Set<String>> tagNames, Optional<String> namePart, Optional<String> descriptionPart,
                                Optional<CertificateCriteria.SortingField> field, Optional<SortingOrder> order, Optional<Float> minPrice,
                                Optional<Float> maxPrice, Optional<LocalDateTime> minDate, Optional<LocalDateTime> maxDate) {
        this.tagNames = tagNames.orElse(new HashSet<>());
        this.namePart = namePart.orElse("");
        this.descriptionPart = descriptionPart.orElse("");
        this.field = field.orElse(SortingField.ID);
        this.order = order.orElse(SortingOrder.ASC);
        this.minPrice = minPrice.orElse(0.0f);
        this.maxPrice = maxPrice.orElse(Float.MAX_VALUE);
        this.minCreateDate = minDate.orElse(MIN_DATE_TIME);
        this.maxCreateDate = maxDate.orElse(MAX_DATE_TIME);
    }

    public static CertificateCriteria createCriteria(Map<String, String> criteriaParams) {
        Optional<SortingField> field = enumOf(criteriaParams.get(RequestParams.SORT.value), SortingField.class);
        Optional<SortingOrder> order = enumOf(criteriaParams.get(RequestParams.ORDER.value), SortingOrder.class);

        Optional<Set<String>> tagNames = Optional.ofNullable(criteriaParams.get(RequestParams.TAGS.value))
                .map(s -> Set.of(s.split(",")));

        Optional<String> namePart = Optional.ofNullable(criteriaParams.get(RequestParams.NAME_PART.value));
        Optional<String> descriptionPart = Optional.ofNullable(criteriaParams.get(RequestParams.DESCRIPTION_PART.value));

        Optional<Float> minPrice = Optional.ofNullable(criteriaParams.get(RequestParams.PRICE_GTE.value))
                .filter(NumberUtils::isCreatable)
                .map(Float::parseFloat);

        Optional<Float> maxPrice = Optional.ofNullable(criteriaParams.get(RequestParams.PRICE_LTE.value))
                .filter(NumberUtils::isCreatable)
                .map(Float::parseFloat);

        Optional<LocalDateTime> minDate;
        try {
            minDate = Optional.ofNullable(criteriaParams.get(RequestParams.CREATE_GTE.value)).map(LocalDateTime::parse);
        } catch (DateTimeParseException e) {
            minDate = Optional.empty();
        }

        Optional<LocalDateTime> maxDate;
        try {
            maxDate = Optional.ofNullable(criteriaParams.get(RequestParams.CREATE_LTE.value)).map(LocalDateTime::parse);
        } catch (DateTimeParseException e) {
            maxDate = Optional.empty();
        }

        return new CertificateCriteria(tagNames, namePart, descriptionPart, field, order, minPrice, maxPrice, minDate, maxDate);
    }

    public boolean isTagAdded() {
        return !tagNames.isEmpty();
    }

    public enum SortingField {
        NAME(GiftCertificate_.name), CREATE_DATE(GiftCertificate_.createDate), ID(GiftCertificate_.id), PRICE(GiftCertificate_.price),
        DURATION(GiftCertificate_.duration), LAST_UPDATE_DATE(GiftCertificate_.lastUpdateDate);

        public final SingularAttribute<GiftCertificate, ?> attribute;

        SortingField(SingularAttribute<GiftCertificate, ?> attribute) {
            this.attribute = attribute;
        }
    }

    public enum RequestParams {
        SORT("sort"), NAME_PART("name_part"), DESCRIPTION_PART("description_part"), ORDER("order"),
        TAGS("tags"), PRICE_GTE("price-gte"), PRICE_LTE("price-lte"), CREATE_GTE("create_date-gte"),
        CREATE_LTE("create_date-lte");

        public final String value;

        RequestParams(String value) {
            this.value = value;
        }
    }

    @Override
    public Map<String, String> getCriteriaAsMap() {
        Map<String, String> paramsMap = new LinkedHashMap<>();
        if (isTagAdded()) {
            paramsMap.put(RequestParams.TAGS.value, String.join(",", tagNames));
        }
        if (!namePart.isBlank()) {
            paramsMap.put(RequestParams.NAME_PART.value, namePart);
        }
        if (!descriptionPart.isBlank()) {
            paramsMap.put(RequestParams.DESCRIPTION_PART.value, descriptionPart);
        }
        if (minPrice != 0.0f) {
            paramsMap.put(RequestParams.PRICE_GTE.value, minPrice.toString());
        }
        if (maxPrice != Float.MAX_VALUE) {
            paramsMap.put(RequestParams.PRICE_LTE.value, maxPrice.toString());
        }

        if (!minCreateDate.isEqual(MIN_DATE_TIME)) {
            paramsMap.put(RequestParams.CREATE_GTE.value, minCreateDate.toString());
        }
        if (!maxCreateDate.isEqual(MAX_DATE_TIME)) {
            paramsMap.put(RequestParams.CREATE_LTE.value, maxCreateDate.toString());
        }
        paramsMap.put(RequestParams.ORDER.value, order.toString().toLowerCase());
        paramsMap.put(RequestParams.SORT.value, field.toString().toLowerCase());
        return paramsMap;
    }

}
