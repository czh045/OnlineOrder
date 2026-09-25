// repository 包放数据库访问接口。
package com.laioffer.onlineorder.repository;

// OrderItemEntity 对应 order_items 表。
import com.laioffer.onlineorder.entity.OrderItemEntity;

// @Modifying 表示这条 SQL 会修改数据库。
import org.springframework.data.jdbc.repository.query.Modifying;

// @Query 用来手写 SQL。
import org.springframework.data.jdbc.repository.query.Query;

// ListCrudRepository 提供基础增删改查。
import org.springframework.data.repository.ListCrudRepository;

// @Param 用来绑定 SQL 参数。
import org.springframework.data.repository.query.Param;

// List 表示查询结果是多条。
import java.util.List;

// OrderItemRepository 负责访问 order_items 表。
public interface OrderItemRepository extends ListCrudRepository<OrderItemEntity, Long> {

    // 查询某个购物车下的所有订单项。
    // Spring Data JDBC 会根据方法名生成：
    // SELECT * FROM order_items WHERE cart_id = ?
    List<OrderItemEntity> getAllByCartId(Long cartId);

    // 根据 cartId 和 menuItemId 查询一条订单项。
    // 用于判断“购物车里是不是已经有这道菜”。
    // 如果已经有，CartService 会更新数量；如果没有，CartService 会新增一行。
    OrderItemEntity findByCartIdAndMenuItemId(Long cartId, Long menuItemId);

    // DELETE 会修改数据库，所以要加 @Modifying。
    @Modifying

    // 自定义删除 SQL：
    // 删除某个购物车下的全部订单项。
    @Query("DELETE FROM order_items WHERE cart_id = :cartId")

    // 结算/清空购物车时调用。
    void deleteByCartId(
            // 把 Java 参数 cartId 绑定到 SQL 的 :cartId。
            @Param("cartId") Long cartId
    );

    // 每次修改购物车后由数据库重新汇总，避免 Java 内存中的总价和真实行项目不一致。
    @Query("SELECT COALESCE(SUM(price * quantity), 0) FROM order_items WHERE cart_id = :cartId")
    Double sumPriceByCartId(@Param("cartId") Long cartId);
}
