// model 包放接口请求体和响应体。
package com.laioffer.onlineorder.model;

// CartEntity 是数据库购物车表对应的实体。
import com.laioffer.onlineorder.entity.CartEntity;

// List 表示购物车里有多个订单项。
import java.util.List;

// CartDto 是返回给前端的购物车数据。
// DTO 的意思是 Data Transfer Object，专门用于接口传输。
public record CartDto(
        // 购物车 id。
        Long id,

        // 这个购物车属于哪个用户。
        Long customerId,

        // 购物车总价。
        Double totalPrice,

        // 购物车里的商品行列表。
        List<OrderItemDto> orderItems
) {
    // 这是一个自定义构造函数。
    // 它的作用是把数据库里的 CartEntity + 订单项列表转换成 CartDto。
    public CartDto(CartEntity cartEntity, List<OrderItemDto> orderItems) {
        // record 的自定义构造函数里，必须调用 this(...) 给所有字段赋值。
        this(
                // 从 CartEntity 里取购物车 id。
                cartEntity.id(),

                // 从 CartEntity 里取 customerId。
                cartEntity.customerId(),

                // 从 CartEntity 里取 totalPrice。
                cartEntity.totalPrice(),

                // 使用 Service 层已经组装好的 orderItems。
                orderItems
        );
    }
}
