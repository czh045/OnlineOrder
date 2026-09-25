// model 包放接口传输对象。
package com.laioffer.onlineorder.model;

// JsonProperty 用来指定 JSON 字段和 Java 字段之间的映射。
import com.fasterxml.jackson.annotation.JsonProperty;

// AddToCartBody 表示 POST /cart 的请求体。
// 例如 Postman 里发送：
// {
//   "menu_id": 4
// }
public record AddToCartBody(
        // JSON 字段叫 menu_id。
        // Java 字段叫 menuId，符合 Java camelCase 命名习惯。
        @JsonProperty("menu_id") Long menuId,

        // quantity 是可选字段；旧版 Postman 只传 menu_id 时，Controller 会默认按 1 件处理。
        Integer quantity
) {
    // Controller 里可以通过 body.menuId() 取到前端传来的菜品 ID。
}
