package com.epam.esm;

import com.epam.esm.criteria.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class CriteriaTests {

    @Nested
    class CertificateCriteriaTests{

        @Test
        void sortingOrderShouldBeSetCorrectly(){
            CertificateCriteria criteria = CertificateCriteria.createCriteria(Map.of("order", "desc"));
            assertEquals(SortingOrder.DESC, criteria.getOrder());
        }

        @Test
        void defaultSortingOrderShouldBeASC(){
            CertificateCriteria criteria = CertificateCriteria.createCriteria(Map.of("some_other", "some_other"));
            assertEquals(SortingOrder.ASC, criteria.getOrder());
        }

        @Test
        void sortingFieldShouldBeSetCorrectly(){
            CertificateCriteria criteria = CertificateCriteria.createCriteria(Map.of("sort", "price"));
            assertEquals(CertificateCriteria.SortingField.PRICE, criteria.getField());
        }

        @Test
        void defaultSortingFieldShouldBeSetCorrectly(){
            CertificateCriteria criteria = CertificateCriteria.createCriteria(Map.of("some_param", "some_param"));
            assertEquals(CertificateCriteria.SortingField.ID, criteria.getField());
        }

        @Test
        void namePartShouldBeSetCorrectly(){
            String expected = "some_part";
            CertificateCriteria criteria = CertificateCriteria.createCriteria(Map.of("name_part", expected));
            assertEquals(expected, criteria.getNamePart());
        }

        @Test
        void descriptionPartShouldBeSetCorrectly(){
            String expected = "some_part";
            CertificateCriteria criteria = CertificateCriteria.createCriteria(Map.of("description_part", expected));
            assertEquals(expected, criteria.getDescriptionPart());
        }

        @Test
        void tagListShouldBeSetCorrectly(){
            Set<String> expected = Set.of("tag1", "tag2", "tag3");
            CertificateCriteria criteria = CertificateCriteria.createCriteria(Map.of("tags", "tag1,tag2,tag3"));
            assertEquals(expected, criteria.getTagNames());
        }

        @Test
        void minPriceShouldBeSetCorrectly(){
            Float expected = 11.1f;
            CertificateCriteria criteria = CertificateCriteria.createCriteria(Map.of("price-gte", "11.1"));
            assertEquals(expected, criteria.getMinPrice());
        }

        @Test
        void defaultMinPriceShouldBeSetCorrectly(){
            Float expected = 0.0f;
            CertificateCriteria criteria = CertificateCriteria.createCriteria(Map.of("some_param", "param"));
            assertEquals(expected, criteria.getMinPrice());
        }

        @Test
        void maxPriceShouldBeSetCorrectly(){
            Float expected = 11.1f;
            CertificateCriteria criteria = CertificateCriteria.createCriteria(Map.of("price-lte", "11.1"));
            assertEquals(expected, criteria.getMaxPrice());
        }

        @Test
        void defaultMaxPriceShouldBeSetCorrectly(){
            Float expected = Float.MAX_VALUE;
            CertificateCriteria criteria = CertificateCriteria.createCriteria(Map.of("some_param", "param"));
            assertEquals(expected, criteria.getMaxPrice());
        }

        @Test
        void minCreateDateShouldBeSetCorrectly(){
            String dateStr = "2021-08-05T11:33:22";
            LocalDateTime expected = LocalDateTime.parse(dateStr);
            CertificateCriteria criteria = CertificateCriteria.createCriteria(Map.of("create_date-gte", dateStr));
            assertEquals(expected, criteria.getMinCreateDate());
        }

        @Test
        void defaultMinCreateDateShouldBeSetCorrectly(){
            CertificateCriteria criteria = CertificateCriteria.createCriteria(Map.of("some_param", "some_value"));
            assertEquals(CertificateCriteria.MIN_DATE_TIME, criteria.getMinCreateDate());
        }

        @Test
        void maxCreateDateShouldBeSetCorrectly(){
            String dateStr = "2021-08-11T11:33:22";
            LocalDateTime expected = LocalDateTime.parse(dateStr);
            CertificateCriteria criteria = CertificateCriteria.createCriteria(Map.of("create_date-lte", dateStr));
            assertEquals(expected, criteria.getMaxCreateDate());
        }

        @Test
        void defaultMaxCreateDateShouldBeSetCorrectly(){
            CertificateCriteria criteria = CertificateCriteria.createCriteria(Map.of("some_param", "some_value"));
            assertEquals(CertificateCriteria.MAX_DATE_TIME, criteria.getMaxCreateDate());
        }

        @Test
        void criteriaAsMapShouldBeCreatedCorrectly(){
            Map<String,String> mapGiven = Map.of("sort", "duration", "name_part", "name", "price-gte", "10");
            Map<String,String> mapExpected = Map.of("sort", "duration", "order", "asc", "name_part", "name", "price-gte", "10.0");
            Criteria certificateCriteria = CertificateCriteria.createCriteria(mapGiven);
            assertEquals(mapExpected, certificateCriteria.getCriteriaAsMap());
        }
    }

    @Nested
    class UserCriteriaTests{

        @Test
        void sortingOrderShouldBeSetCorrectly(){
            UserCriteria criteria = UserCriteria.createCriteria(Map.of("order", "desc"));
            assertEquals(SortingOrder.DESC, criteria.getSortingOrder());
        }

        @Test
        void defaultSortingOrderShouldBeASC(){
            UserCriteria criteria = UserCriteria.createCriteria(Map.of("some_other", "some_other"));
            assertEquals(SortingOrder.ASC, criteria.getSortingOrder());
        }

        @Test
        void sortingFieldShouldBeSetCorrectly(){
            UserCriteria criteria = UserCriteria.createCriteria(Map.of("sort", "first_name"));
            assertEquals(UserCriteria.SortingField.FIRST_NAME, criteria.getSortingField());
        }

        @Test
        void defaultSortingFieldShouldBeSetCorrectly(){
            UserCriteria criteria = UserCriteria.createCriteria(Map.of("some_param", "some_param"));
            assertEquals(UserCriteria.SortingField.ID, criteria.getSortingField());
        }

        @Test
        void firstNamePartShouldBeSetCorrectly(){
            String expected = "part";
            UserCriteria criteria = UserCriteria.createCriteria(Map.of("first_name", expected));
            assertEquals(expected, criteria.getFirstNamePart());
        }

        @Test
        void lastNamePartShouldBeSetCorrectly(){
            String expected = "part";
            UserCriteria criteria = UserCriteria.createCriteria(Map.of("last_name", expected));
            assertEquals(expected, criteria.getLastNamePart());
        }

        @Test
        void criteriaAsMapShouldBeCreatedCorrectly(){
            Map<String,String> mapGiven = Map.of("order", "desc", "first_name", "name");
            Map<String,String> mapExpected = Map.of("order", "desc", "sort","id", "first_name", "name");
            Criteria userCriteria = UserCriteria.createCriteria(mapGiven);
            assertEquals(mapExpected, userCriteria.getCriteriaAsMap());
        }

    }

    @Nested
    class OrderCriteriaTests{

        @Test
        void sortingOrderShouldBeSetCorrectly(){
            OrderCriteria criteria = OrderCriteria.createCriteria(Map.of("order", "desc"));
            assertEquals(SortingOrder.DESC, criteria.getSortingOrder());
        }

        @Test
        void defaultSortingOrderShouldBeASC(){
            OrderCriteria criteria = OrderCriteria.createCriteria(Map.of("some_other", "some_other"));
            assertEquals(SortingOrder.ASC, criteria.getSortingOrder());
        }

        @Test
        void sortingFieldShouldBeSetCorrectly(){
            OrderCriteria criteria = OrderCriteria.createCriteria(Map.of("sort", "cost"));
            assertEquals(OrderCriteria.SortingField.COST, criteria.getSortingField());
        }

        @Test
        void defaultSortingFieldShouldBeSetCorrectly(){
            OrderCriteria criteria = OrderCriteria.createCriteria(Map.of("some_param", "some_param"));
            assertEquals(OrderCriteria.SortingField.ID, criteria.getSortingField());
        }

        @Test
        void minCostShouldBeSetCorrectly(){
            Float expected = 11.1f;
            OrderCriteria criteria = OrderCriteria.createCriteria(Map.of("cost-gte", "11.1"));
            assertEquals(expected, criteria.getMinCost());
        }

        @Test
        void defaultMinCostShouldBeSetCorrectly(){
            Float expected = 0.0f;
            OrderCriteria criteria = OrderCriteria.createCriteria(Map.of("some_param", "param"));
            assertEquals(expected, criteria.getMinCost());
        }

        @Test
        void maxCostShouldBeSetCorrectly(){
            Float expected = 11.1f;
            OrderCriteria criteria = OrderCriteria.createCriteria(Map.of("cost-lte", "11.1"));
            assertEquals(expected, criteria.getMaxCost());
        }

        @Test
        void defaultMaxCostShouldBeSetCorrectly(){
            Float expected = Float.MAX_VALUE;
            OrderCriteria criteria = OrderCriteria.createCriteria(Map.of("some_param", "param"));
            assertEquals(expected, criteria.getMaxCost());
        }

        @Test
        void minTimeShouldBeSetCorrectly(){
            String dateStr = "2021-08-05T11:33:22";
            LocalDateTime expected = LocalDateTime.parse(dateStr);
            OrderCriteria criteria = OrderCriteria.createCriteria(Map.of("time-gte", dateStr));
            assertEquals(expected, criteria.getMinTime());
        }

        @Test
        void defaultMinTimeShouldBeSetCorrectly(){
            OrderCriteria criteria = OrderCriteria.createCriteria(Map.of("some_param", "some_value"));
            assertEquals(OrderCriteria.MIN_DATE_TIME, criteria.getMinTime());
        }

        @Test
        void maxTimeShouldBeSetCorrectly(){
            String dateStr = "2021-08-11T11:33:22";
            LocalDateTime expected = LocalDateTime.parse(dateStr);
            OrderCriteria criteria = OrderCriteria.createCriteria(Map.of("time-lte", dateStr));
            assertEquals(expected, criteria.getMaxTime());
        }

        @Test
        void defaultMaximeShouldBeSetCorrectly(){
            OrderCriteria criteria = OrderCriteria.createCriteria(Map.of("some_param", "some_value"));
            assertEquals(OrderCriteria.MAX_DATE_TIME, criteria.getMaxTime());
        }

        @Test
        void criteriaAsMapShouldBeCreatedCorrectly(){
            Map<String,String> mapGiven = Map.of("sort", "cost",  "time-gte", "2021-08-11T11:33:22", "time-lte", "2021-08-11T11:35:22");
            Map<String,String> mapExpected = Map.of("order", "asc", "sort", "cost",  "time-gte", "2021-08-11T11:33:22", "time-lte", "2021-08-11T11:35:22");
            Criteria orderCriteria = OrderCriteria.createCriteria(mapGiven);
            assertEquals(mapExpected, orderCriteria.getCriteriaAsMap());
        }

    }
}
