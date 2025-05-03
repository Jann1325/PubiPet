package io.github.jann1325.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_cart_item")
public class UserCartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer userCartId;

    private Integer productId;

    private Integer quantity;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;
}