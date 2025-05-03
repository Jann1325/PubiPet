package io.github.jann1325.service;

import io.github.jann1325.domain.Product;
import io.github.jann1325.domain.UserCart;
import io.github.jann1325.domain.UserCartItem;
import io.github.jann1325.dto.CartDTO;
import io.github.jann1325.repository.UserCartItemRepository;
import io.github.jann1325.repository.UserCartRepository;
import io.github.jann1325.utils.JwtUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class CartService {
    @Autowired
    private UserCartRepository userCartRepository;
    @Autowired
    private UserCartItemRepository userCartItemRepository;


    // 根據 token 查詢購物車商品數量
    public int getCartCount(String authorizationHeader) {
        Integer userId = JwtUtils.extractUserId(authorizationHeader.replace("Bearer ", ""));
        Optional<UserCart> cart = userCartRepository.findByUserId(userId); // 根據用戶查找購物車
        if (cart == null) {
            return 0;
        }
        // 返回購物車中商品的總數量
        Integer count = userCartItemRepository.getCartCount(userId);
        return count != null ? count : 0;
    }

    // 同步購物車資料
    @Transactional
    public void syncCart(String authorizationHeader, List<CartDTO> cartDTOList) {
        Integer userId = JwtUtils.extractUserId(authorizationHeader.replace("Bearer ", ""));
        // 找出使用者的購物車
        UserCart cart = userCartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserCart newCart = new UserCart();
                    newCart.setUserId(userId);
                    newCart.setCreatedTime(LocalDateTime.now());
                    newCart.setUpdatedTime(LocalDateTime.now());
                    return userCartRepository.save(newCart);
                });

        for (CartDTO dto : cartDTOList) {
            // 檢查是否已經有這個商品
            Optional<UserCartItem> existingItem = Optional.ofNullable(userCartItemRepository
                    .findByUserCartIdAndProductId(cart.getId(), dto.getProductId()));
            if (existingItem.isPresent()) {
                // 如果已存在，更新數量
                UserCartItem item = existingItem.get();
                item.setQuantity(item.getQuantity() + dto.getQuantity());
                item.setUpdatedTime(LocalDateTime.now());
                userCartItemRepository.save(item);
            } else {
                // 否則新增一筆新的資料
                UserCartItem newItem = new UserCartItem();
                newItem.setUserCartId(cart.getId());
                newItem.setProductId(dto.getProductId());
                newItem.setQuantity(dto.getQuantity());
                newItem.setCreatedTime(LocalDateTime.now());
                newItem.setUpdatedTime(LocalDateTime.now());
                userCartItemRepository.save(newItem);
            }
        }
    }

    // 取得購物車商品資料
    public List<Map<String, Object>> getCartItems(String authorizationHeader) {
        Integer userId = JwtUtils.extractUserId(authorizationHeader.replace("Bearer ", ""));
        // 1. 找是否有相對應的購物車id（UserCart）
        Optional<UserCart> cartOptional = userCartRepository.findByUserId(userId);
        if (!cartOptional.isPresent()) {
            return Collections.emptyList();
        }
        // 1. 找相對應的購物車商品資料（UserCartItem）
        Integer cartId = cartOptional.get().getId();
        List<UserCartItem> cartItems = userCartItemRepository.findByUserCartId(cartId);

        return cartItems.stream().map(item -> {
            Map<String, Object> map = new HashMap<>();
            map.put("productId", item.getProductId());
            map.put("quantity", item.getQuantity());
            return map;
        }).collect(Collectors.toList());
    }

    // 加入購物車
    public void addItem(String authorizationHeader, CartDTO cartDTO) {
        Integer userId = JwtUtils.extractUserId(authorizationHeader.replace("Bearer ", ""));
        // 1. 找購物車 id，沒有則建立一筆 UserCart
        UserCart cart = userCartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserCart newCart = new UserCart();
                    newCart.setUserId(userId);
                    return userCartRepository.save(newCart);
                });

        // 2. 檢查 UserCartItem 是否存在
        Optional<UserCartItem> existingItem =
                Optional.ofNullable(userCartItemRepository.findByUserCartIdAndProductId(cart.getId(), cartDTO.getProductId()));
        if (existingItem.isPresent()) {
            UserCartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + cartDTO.getQuantity());
            userCartItemRepository.save(item);
        } else {
            UserCartItem item = new UserCartItem();
            BeanUtils.copyProperties(cartDTO, item);
            item.setUserCartId(cart.getId());
            item.setCreatedTime(LocalDateTime.now());
            userCartItemRepository.save(item);
        }
    }

    // 刪除商品
    @Transactional
    public void deleteItems(String authorizationHeader, List<Integer> productIds) {
        Integer userId = JwtUtils.extractUserId(authorizationHeader.replace("Bearer ", ""));
        Optional<UserCart> cartOpt = userCartRepository.findByUserId(userId);
        if (cartOpt.isEmpty()) return;

        Integer cartId = cartOpt.get().getId();

        if (productIds != null && !productIds.isEmpty()) {
            userCartItemRepository.deleteByUserCartIdAndProductIds(cartId, productIds);
        }
    }

    // 更新購物車商品數量
    @Transactional
    public void updateQuantities(String authorizationHeader, List<CartDTO> cartDTOList) {
        Integer userId = JwtUtils.extractUserId(authorizationHeader.replace("Bearer ", ""));
        UserCart cart = userCartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserCart newCart = new UserCart();
                    newCart.setUserId(userId);
                    return userCartRepository.save(newCart);
                });
        for (CartDTO item : cartDTOList) {
            userCartItemRepository.updateQuantity(cart.getId(), item.getProductId(), item.getQuantity());
        }
    }
}
