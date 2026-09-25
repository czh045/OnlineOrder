// controller 包负责处理 HTTP 请求。
package com.laioffer.onlineorder.controller;

// GetMapping 表示这个方法处理 GET 请求。
import org.springframework.web.bind.annotation.GetMapping;

// RestController 表示返回值会被转成 JSON。
import org.springframework.web.bind.annotation.RestController;

// Map 是 Java 的键值对集合。
// 这里用它快速返回一个简单 JSON：{"status":"UP"}。
import java.util.Map;

// @RestController 让 Spring 管理这个接口类。
@RestController

// HealthController 是健康检查接口。
// 健康检查常用于确认服务是否成功启动。
public class HealthController {

    // GET /health：
    // 浏览器或 Postman 访问 http://localhost:8080/health 会进入这里。
    @GetMapping("/health")

    // 返回 Map<String, String>，Spring 会自动序列化成 JSON。
    public Map<String, String> health() {
        // Map.of 创建一个不可修改的小 Map。
        // 返回结果是 {"status":"UP"}。
        return Map.of("status", "UP");
    }
}
