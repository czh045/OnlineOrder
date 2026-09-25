// model 包放请求体和响应体。
package com.laioffer.onlineorder.model;

// JsonProperty 用来指定 JSON 字段名和 Java 字段名的对应关系。
import com.fasterxml.jackson.annotation.JsonProperty;

// RegisterBody 表示 POST /signup 的请求体。
// 前端传来的 JSON 会被 Spring/Jackson 转成这个 record。
public record RegisterBody(
        // email 对应 JSON 里的 "email"。
        String email,

        // password 对应 JSON 里的 "password"。
        String password,

        // @JsonProperty("first_name") 表示：
        // JSON 里叫 first_name，Java 里叫 firstName。
        @JsonProperty("first_name") String firstName,

        // JSON 里叫 last_name，Java 里叫 lastName。
        @JsonProperty("last_name") String lastName
) {
    // 这个 record 主要用于接收请求，不直接对应数据库表。
}
