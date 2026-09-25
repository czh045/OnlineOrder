// hello 包里的示例数据类。
package com.laioffer.onlineorder.hello;

// Address 表示地址对象。
// 它会作为 Person 的嵌套字段返回给前端。
public record Address(
        // street 表示街道地址。
        String street,

        // city 表示城市。
        String city,

        // state 表示州或省。
        String state,

        // country 表示国家。
        // HelloController 里传了 null，所以返回 JSON 时这个字段可能不显示或显示 null，取决于 Jackson 配置。
        String country
) {
    // record 自动生成 address.street()、address.city() 等方法。
}
