// entity 包放数据库表对应的 Java 数据结构。
package com.laioffer.onlineorder.entity;

// @Id 标记主键字段。
import org.springframework.data.annotation.Id;

// @Table 指定这个 Entity 对应哪张数据库表。
import org.springframework.data.relational.core.mapping.Table;

// @Table("carts") 表示 CartEntity 对应 carts 表。
@Table("carts")

// CartEntity 表示一个购物车。
// 这个项目里一个 customer 对应一个 cart。
public record CartEntity(
        // id 是 carts 表主键。
        @Id Long id,

        // customerId 对应 carts.customer_id。
        // 它是外键，指向 customers.id。
        Long customerId,

        // totalPrice 对应 carts.total_price。
        // 表示购物车当前总价。
        Double totalPrice
) {
    // record 自动生成 cart.id()、cart.customerId()、cart.totalPrice() 等方法。
}
