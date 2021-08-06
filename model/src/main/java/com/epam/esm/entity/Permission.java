package com.epam.esm.entity;

public enum Permission {

    CERTIFICATES_WRITE("certificates:write"),
    CERTIFICATES_BUY("certificates:buy"), TAGS_READ("tags:read"), TAGS_WRITE("tags:write"),
    USERS_READ("users:read"), USERS_READ_SELF("users:read-self"),
    ORDERS_READ("orders:read");

    public final String title;

    Permission(String title) {
        this.title = title;
    }

}
