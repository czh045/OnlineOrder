// repository 包放数据库访问代码。
package com.laioffer.onlineorder.repository;

// CartEntity 对应 carts 表。
import com.laioffer.onlineorder.entity.CartEntity;

// @Modifying 表示 SQL 会修改数据库。
import org.springframework.data.jdbc.repository.query.Modifying;

// @Query 用来手写 SQL。
import org.springframework.data.jdbc.repository.query.Query;

// ListCrudRepository 提供基础增删改查方法。
// 和 CrudRepository 相比，它的批量查询返回 List，更贴合课程里其他 Repository 的写法。
import org.springframework.data.repository.ListCrudRepository;

// @Param 绑定 SQL 参数。
import org.springframework.data.repository.query.Param;

// CartRepository 负责访问 carts 表。
// ListCrudRepository<CartEntity, Long> 表示：
// 1. 管理 CartEntity；
// 2. 主键类型是 Long。
public interface CartRepository extends ListCrudRepository<CartEntity, Long> {

    // 根据 customerId 查购物车。
    // Spring Data JDBC 会根据方法名生成类似：
    // SELECT * FROM carts WHERE customer_id = ?
    CartEntity getByCustomerId(Long customerId);

    // 下面这条 SQL 是 UPDATE，所以要加 @Modifying。
    @Modifying

    // 自定义 SQL：根据购物车 id 更新 total_price。
    @Query("UPDATE carts SET total_price = :totalPrice WHERE id = :cartId")

    // 更新购物车总价。
    void updateTotalPrice(
            // 绑定 SQL 里的 :cartId。
            @Param("cartId") Long cartId,

            // 绑定 SQL 里的 :totalPrice。
            @Param("totalPrice") Double totalPrice
    );
}
