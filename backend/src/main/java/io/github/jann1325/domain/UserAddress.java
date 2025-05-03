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
@Table(name = "user_address")
public class UserAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer userId;

    private String recipient;

    private String city;

    private String area;

    private String country;

    private String phone;

    private String address;

    private Boolean isDefault;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;
}
