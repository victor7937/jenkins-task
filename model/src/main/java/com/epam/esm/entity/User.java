package com.epam.esm.entity;

import com.epam.esm.audit.UserAuditListener;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
@EntityListeners(UserAuditListener.class)
@NoArgsConstructor @Getter @Setter @ToString
public class User implements Serializable  {

    private static final long serialVersionUID = -7299750937016032393L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "active")
    private boolean active;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;


    @OneToMany(mappedBy = "user", cascade = {CascadeType.REFRESH, CascadeType.DETACH,
            CascadeType.MERGE})
    @JsonIgnore
    @ToString.Exclude
    private List<Order> orders = new ArrayList<>();

    public User(String email, String password, String firstName, String lastName) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public User(String email, String firstName, String lastName) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public void addOrder(Order order){
        this.orders.add(order);
        order.setUser(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

}
