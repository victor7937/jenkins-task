package com.epam.esm.entity;

import java.time.LocalDateTime;
import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(GiftCertificate.class)
public abstract class GiftCertificate_ {

	public static volatile SingularAttribute<GiftCertificate, Integer> duration;
	public static volatile SingularAttribute<GiftCertificate, Boolean> deleted;
	public static volatile SingularAttribute<GiftCertificate, Float> price;
	public static volatile SingularAttribute<GiftCertificate, LocalDateTime> lastUpdateDate;
	public static volatile SingularAttribute<GiftCertificate, String> name;
	public static volatile SingularAttribute<GiftCertificate, String> description;
	public static volatile SingularAttribute<GiftCertificate, Long> id;
	public static volatile SetAttribute<GiftCertificate, Tag> tags;
	public static volatile SingularAttribute<GiftCertificate, LocalDateTime> createDate;

	public static final String DURATION = "duration";
	public static final String DELETED = "deleted";
	public static final String PRICE = "price";
	public static final String LAST_UPDATE_DATE = "lastUpdateDate";
	public static final String NAME = "name";
	public static final String DESCRIPTION = "description";
	public static final String ID = "id";
	public static final String TAGS = "tags";
	public static final String CREATE_DATE = "createDate";

}

