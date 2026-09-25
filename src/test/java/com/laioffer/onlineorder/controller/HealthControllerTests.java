// 测试类放在和被测试类相同或相关的包下。
package com.laioffer.onlineorder.controller;

// @Test 标记 JUnit 测试方法。
import org.junit.jupiter.api.Test;

// assertEquals 用来断言“实际结果是否等于预期结果”。
import static org.junit.jupiter.api.Assertions.assertEquals;

// HealthControllerTests 专门测试 HealthController。
class HealthControllerTests {

    // @Test 表示这个方法会被测试框架执行。
    @Test

    // 方法名 health_returnsUpStatus 的意思是：
    // 测试 health 方法是否返回 UP 状态。
    void health_returnsUpStatus() {
        // 手动创建 HealthController。
        // 这个测试不需要启动 Spring，因为 HealthController 没有依赖其他 Bean。
        HealthController controller = new HealthController();

        // 调用 controller.health()，拿到返回的 Map。
        // 再取出 key 为 "status" 的值。
        // 最后断言它应该等于 "UP"。
        assertEquals("UP", controller.health().get("status"));
    }
}
