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
@Table(name = "order_record")
public class OrderRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer userId;

    private Integer userPaymentId;

    private Integer totalAmount;

    private String orderStatus;

    private String paymentMethod;

    private String paymentStatus;

    private LocalDateTime paidTime;

    private String transactionId;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;
}