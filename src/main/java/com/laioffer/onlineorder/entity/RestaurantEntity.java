// entity 包放数据库表对应的 Java 类型。
package com.laioffer.onlineorder.entity;

// @Id 标记主键字段。
import org.springframework.data.annotation.Id;

// @Table 指定数据库表名。
import org.springframework.data.relational.core.mapping.Table;

// RestaurantEntity 对应 restaurants 表。
@Table("restaurants")
public record RestaurantEntity(
        // id 是餐厅主键。
        @Id Long id,

        // name 是餐厅名称，例如 Burger King。
        String name,

        // address 是餐厅地址。
        String address,

        // phone 是餐厅电话。
        String phone,

        // imageUrl 是餐厅图片地址，用于前端展示。
        String imageUrl
) {
    // record 自动生成 restaurant.id()、restaurant.name() 等访问方法。
}
