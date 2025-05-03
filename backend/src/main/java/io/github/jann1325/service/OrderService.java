package io.github.jann1325.service;


import io.github.jann1325.domain.OrderDetail;
import io.github.jann1325.domain.OrderRecord;
import io.github.jann1325.domain.Product;
import io.github.jann1325.domain.UserCart;
import io.github.jann1325.dto.CartDTO;
import io.github.jann1325.dto.OrderDTO;
import io.github.jann1325.repository.*;
import io.github.jann1325.utils.JwtUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class OrderService {

    @Autowired
    private OrderRecordRepository orderRecordRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private UserCartRepository userCartRepository;

    @Autowired
    private UserCartItemRepository userCartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    // 建立訂單
    @Transactional
    public void submitOrder(String authorizationHeader, OrderDTO.OrderDataDTO orderDataDTO) {
        String token = authorizationHeader.replace("Bearer ", "");
        Integer userId = JwtUtils.extractUserId(token);
        OrderRecord order = new OrderRecord();
        order.setUserId(userId);
        String payment = orderDataDTO.getPayment();
        order.setPaymentMethod(payment);
        order.setOrderStatus("pending");
        // TODO:綁定會員儲存的付款方式
        order.setUserPaymentId(0);
        order.setTotalAmount(calculateTotalAmount(orderDataDTO.getCart()));
        // 如果是信用卡，付款時間為訂單成立時間、付款狀態為已付款
        if(payment.equals("credit")) {
            order.setPaidTime(LocalDateTime.now());
            order.setPaymentStatus("paid");
        } else { // 貨到付款
            order.setPaymentStatus("unpaid");
        }
        order.setCreatedTime(LocalDateTime.now());
        order.setUpdatedTime(LocalDateTime.now());
        orderRecordRepository.save(order);

        // 建立訂單明細
        List<Integer> productIds = new ArrayList<>();
        for (OrderDTO.CartItem item : orderDataDTO.getCart()) {
            OrderDetail detail = new OrderDetail();
            BeanUtils.copyProperties(detail, item);
            detail.setOrderId(order.getId());
            detail.setCreatedTime(LocalDateTime.now());
            detail.setUpdatedTime(LocalDateTime.now());
            orderDetailRepository.save(detail);
            productIds.add(item.getProductId());
        }

        // 刪除購物車資料
        userCartRepository.findByUserId(userId)
                .map(UserCart::getId)
                .ifPresent(cartId -> userCartItemRepository.deleteByUserCartIdAndProductIds(cartId, productIds));

    }

    private int calculateTotalAmount(List<OrderDTO.CartItem> cartItems) {
        return cartItems.stream()
                .mapToInt(item -> item.getQuantity() * getProductPrice(item.getProductId()))
                .sum();
    }

    private int getProductPrice(int productId) {
        return productRepository.findById(productId)
                .map(product -> product.getDiscountPrice() != null
                        ? product.getDiscountPrice()
                        : product.getPrice())
                .orElse(0);
    }

    //取得歷史訂單
    public List<OrderDTO.OrderHistoryDTO> getOrderHistory(String authorizationHeader) {
        Integer userId = JwtUtils.extractUserId(authorizationHeader.replace("Bearer ", ""));
        List<OrderRecord> records = orderRecordRepository.findByUserId(userId);
        List<OrderDTO.OrderHistoryDTO> result = new ArrayList<>();

        for (OrderRecord record : records) {
            List<OrderDetail> details = orderDetailRepository.findByOrderId(record.getId());
            List<OrderDTO.OrderItemDTO> items = new ArrayList<>();

            for (OrderDetail detail : details) {
                Product product = productRepository.findById(detail.getProductId()).orElse(null);
                if (product != null) {
                    OrderDTO.OrderItemDTO itemDTO = new OrderDTO.OrderItemDTO(
                            product.getId(),
                            product.getProdName(),
                            detail.getQuantity(),
                            detail.getPrice()
                    );
                    items.add(itemDTO);
                }
            }

            OrderDTO.OrderHistoryDTO dto = new OrderDTO.OrderHistoryDTO(
                    "ORD-" + record.getId(),
                    record.getTotalAmount(),
                    record.getPaymentMethod(),
                    record.getPaymentStatus(),
                    record.getOrderStatus(),
                    items
            );

            result.add(dto);
        }

        return result;
    }
}
