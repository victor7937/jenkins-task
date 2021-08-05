package com.epam.esm.entity;

import java.time.LocalDateTime;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Order.class)
public abstract class Order_ {

	public static volatile SingularAttribute<Order, Float> cost;
	public static volatile SingularAttribute<Order, LocalDateTime> timeOfPurchase;
	public static volatile SingularAttribute<Order, Long> id;
	public static volatile SingularAttribute<Order, GiftCertificate> giftCertificate;
	public static volatile SingularAttribute<Order, User> user;

	public static final String COST = "cost";
	public static final String TIME_OF_PURCHASE = "timeOfPurchase";
	public static final String ID = "id";
	public static final String GIFT_CERTIFICATE = "giftCertificate";
	public static final String USER = "user";

}

