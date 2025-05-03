package io.github.jann1325.repository;

import io.github.jann1325.domain.UserCartItem;
import io.github.jann1325.dto.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserCartItemRepository extends JpaRepository<UserCartItem, Integer> {

    UserCartItem findByUserCartIdAndProductId(Integer userCartId, Integer productId);

    @Query(nativeQuery = true, value =
            "SELECT SUM(uci.quantity) " +
            "FROM pubi_pet.user_cart_item uci " +
            "JOIN pubi_pet.user_cart uc ON uci.user_cart_id = uc.id " +
            "WHERE uc.user_id = :userId")
    Integer getCartCount(@Param("userId") Integer userId);

    List<UserCartItem> findByUserCartId(Integer userCartId);

    @Modifying
    @Query("DELETE FROM UserCartItem u WHERE u.userCartId = :cartId AND u.productId IN :productIds")
    void deleteByUserCartIdAndProductIds(@Param("cartId") Integer cartId, @Param("productIds") List<Integer> productIds);

    @Modifying
    @Query("UPDATE UserCartItem u SET u.quantity = :quantity WHERE u.userCartId = :userCartId AND u.productId = :productId")
    void updateQuantity(@Param("userCartId") Integer userCartId,
                        @Param("productId") Integer productId,
                        @Param("quantity") Integer quantity);

}
