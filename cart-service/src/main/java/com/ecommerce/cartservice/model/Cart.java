package com.ecommerce.cartservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;

@Table
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

    @Id
    @GeneratedValue
    private Long id;

    private Long userId;

    @Column(unique = true)
    private String sessionId;

    @OneToMany(mappedBy = "cart", cascade = ALL, orphanRemoval = true, fetch = LAZY)
    private List<CartItem> items = new ArrayList<>();
}
