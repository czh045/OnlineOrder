// 测试类所在包要和主应用包一致或在其子包下。
package com.laioffer.onlineorder;

// 导入各个 Repository，是为了在测试里用 mock 替代真实数据库访问。
import com.laioffer.onlineorder.repository.CartRepository;
import com.laioffer.onlineorder.repository.CustomerRepository;
import com.laioffer.onlineorder.repository.MenuItemRepository;
import com.laioffer.onlineorder.repository.OrderItemRepository;
import com.laioffer.onlineorder.repository.OrderLineItemRepository;
import com.laioffer.onlineorder.repository.OrderRepository;
import com.laioffer.onlineorder.repository.PaymentMethodRepository;
import com.laioffer.onlineorder.repository.RestaurantRepository;

// @Test 表示这是 JUnit 测试方法。
import org.junit.jupiter.api.Test;

// @SpringBootTest 会启动 Spring Boot 测试上下文。
import org.springframework.boot.test.context.SpringBootTest;

// @MockitoBean 表示用 Mockito mock 对象替换 Spring 容器里的 Bean。
import org.springframework.test.context.bean.override.mockito.MockitoBean;

// @SpringBootTest 用来验证 Spring 应用能否正常加载。
// properties 里覆盖一些配置，避免测试时真的初始化数据库或跑 DevRunner。
@SpringBootTest(properties = {
        // 测试时不要执行 database-init.sql。
        "spring.sql.init.mode=never",

        // 测试时不要自动创建 demo 用户。
        "onlineorder.dev-runner.enabled=false",

        // 排除 Data JDBC Repository 自动配置，避免测试依赖真实数据库。
        "spring.autoconfigure.exclude=org.springframework.boot.data.jdbc.autoconfigure.DataJdbcRepositoriesAutoConfiguration"
})
class OnlineOrderApplicationTests {

    // 用 mock 的 CartRepository 替代真实数据库 Repository。
    @MockitoBean
    private CartRepository cartRepository;

    // 用 mock 的 CustomerRepository 替代真实数据库 Repository。
    @MockitoBean
    private CustomerRepository customerRepository;

    // 用 mock 的 MenuItemRepository 替代真实数据库 Repository。
    @MockitoBean
    private MenuItemRepository menuItemRepository;

    // 用 mock 的 OrderItemRepository 替代真实数据库 Repository。
    @MockitoBean
    private OrderItemRepository orderItemRepository;

    @MockitoBean
    private OrderRepository orderRepository;

    @MockitoBean
    private OrderLineItemRepository orderLineItemRepository;

    @MockitoBean
    private PaymentMethodRepository paymentMethodRepository;

    // 用 mock 的 RestaurantRepository 替代真实数据库 Repository。
    @MockitoBean
    private RestaurantRepository restaurantRepository;

    // 这个测试方法方法体是空的。
    // 只要 Spring Boot 测试上下文能成功启动，测试就算通过。
    @Test
    void contextLoads() {
    }

}
