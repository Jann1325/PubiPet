package io.github.jann1325.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private OrderDTO.OrderDataDTO orderDataDTO;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderDataDTO {
        private List<CartItem> cart;
        private String payment;
        private FormData formData;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartItem {
        private Integer productId;
        private Integer quantity;
        private Integer price;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FormData {
        private String name;
        private String phone;
        private String address;
        private String cardNumber;
        private String expiry;
        private String cvv;
        private String store;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderHistoryDTO {
        private String orderNumber;
        private Integer totalAmount;
        private String paymentMethod;
        private String paymentStatus;
        private String orderStatus;
        private List<OrderItemDTO> items;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDTO {
        private Integer productId;
        private String name;
        private Integer quantity;
        private Integer price;
    }

}
