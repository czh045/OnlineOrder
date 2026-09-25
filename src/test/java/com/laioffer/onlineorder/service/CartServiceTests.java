// 测试类放在 service 包下，因为它测试的是 CartService。
package com.laioffer.onlineorder.service;

// CartEntity 是购物车实体。
import com.laioffer.onlineorder.entity.CartEntity;

// MenuItemEntity 是菜单实体。
import com.laioffer.onlineorder.entity.MenuItemEntity;

// OrderItemEntity 是订单项实体。
import com.laioffer.onlineorder.entity.OrderItemEntity;

// CartDto 是 getCart 返回给前端的 DTO。
import com.laioffer.onlineorder.model.CartDto;

// CartRepository 是 CartService 的依赖之一。
import com.laioffer.onlineorder.repository.CartRepository;

// MenuItemRepository 是 CartService 的依赖之一。
import com.laioffer.onlineorder.repository.MenuItemRepository;

// OrderItemRepository 是 CartService 的依赖之一。
import com.laioffer.onlineorder.repository.OrderItemRepository;

// @BeforeEach 表示每个测试执行前都先运行这个方法。
import org.junit.jupiter.api.BeforeEach;

// @Test 表示测试方法。
import org.junit.jupiter.api.Test;

// List 用来构造测试数据列表。
import java.util.List;

// Optional 用来模拟 findById 返回值。
import java.util.Optional;

// Set 用来模拟批量查菜单时传入的 id 集合。
import java.util.Set;

// assertEquals 用来比较预期值和实际值。
import static org.junit.jupiter.api.Assertions.assertEquals;

// mock 用来创建假的 Repository。
import static org.mockito.Mockito.mock;

// verify 用来验证某个 mock 方法是否被调用。
import static org.mockito.Mockito.verify;

// when 用来规定 mock 方法被调用时返回什么。
import static org.mockito.Mockito.when;

// CartServiceTests 测试购物车业务逻辑。
class CartServiceTests {

    // 假的 CartRepository。
    private CartRepository cartRepository;

    // 假的 MenuItemRepository。
    private MenuItemRepository menuItemRepository;

    // 假的 OrderItemRepository。
    private OrderItemRepository orderItemRepository;

    // 被测试的 CartService。
    private CartService cartService;

    // 每个测试方法执行前都会执行 setUp。
    @BeforeEach
    void setUp() {
        // 创建假的 CartRepository。
        cartRepository = mock(CartRepository.class);

        // 创建假的 MenuItemRepository。
        menuItemRepository = mock(MenuItemRepository.class);

        // 创建假的 OrderItemRepository。
        orderItemRepository = mock(OrderItemRepository.class);

        // 用假的 Repository 创建真正的 CartService。
        // 这样测试 CartService 时，不需要真实数据库。
        cartService = new CartService(cartRepository, menuItemRepository, orderItemRepository);
    }

    // 测试场景：购物车里没有这道菜时，加购应该新增订单项并更新总价。
    @Test
    void addMenuItemToCart_newItem_createsOrderItemAndUpdatesTotalPrice() {
        // 创建一个测试用购物车：
        // id=10，customerId=20，当前总价=5.0。
        CartEntity cart = new CartEntity(10L, 20L, 5.0);

        // 创建一个测试用菜单项：
        // id=1，restaurantId=2，名字 beef，价格 10.0。
        MenuItemEntity menuItem = new MenuItemEntity(1L, 2L, "beef", "beef description", 10.0, "image.jpg");

        // 规定：当代码调用 cartRepository.getByCustomerId(20L) 时，返回 cart。
        when(cartRepository.getByCustomerId(20L)).thenReturn(cart);

        // 规定：当代码调用 menuItemRepository.findById(1L) 时，返回 Optional.of(menuItem)。
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(menuItem));

        // 规定：当代码查询购物车里是否已有这道菜时，返回 null。
        // null 表示购物车里还没有这道菜。
        when(orderItemRepository.findByCartIdAndMenuItemId(10L, 1L)).thenReturn(null);

        // 新版实现会从数据库重新汇总总价，测试要模拟这条聚合查询的返回值。
        when(orderItemRepository.sumPriceByCartId(10L)).thenReturn(15.0);

        // 执行被测试的方法：
        // 给 customerId=20 的用户，把 menuItemId=1 的菜加入购物车。
        cartService.addMenuItemToCart(20L, 1L);

        // 验证：应该保存一个新的 OrderItemEntity。
        // id=null 表示新增；menuItemId=1；cartId=10；price=10.0；quantity=1。
        verify(orderItemRepository).save(new OrderItemEntity(null, 1L, 10L, 10.0, 1));

        // 验证：购物车总价应该从 5.0 更新到 15.0。
        verify(cartRepository).updateTotalPrice(10L, 15.0);
    }

    // 测试场景：购物车里已经有这道菜时，加购应该数量加一并更新总价。
    @Test
    void addMenuItemToCart_existingItem_increasesQuantityAndUpdatesTotalPrice() {
        // 创建测试购物车。
        CartEntity cart = new CartEntity(10L, 20L, 5.0);

        // 创建测试菜单项。
        MenuItemEntity menuItem = new MenuItemEntity(1L, 2L, "beef", "beef description", 10.0, "image.jpg");

        // 创建已有订单项：
        // id=30，menuItemId=1，cartId=10，price=10.0，quantity=2。
        OrderItemEntity orderItem = new OrderItemEntity(30L, 1L, 10L, 10.0, 2);

        // mock 查询购物车。
        when(cartRepository.getByCustomerId(20L)).thenReturn(cart);

        // mock 查询菜单。
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(menuItem));

        // mock 查询已有订单项，这次返回 orderItem，表示购物车里已经有这道菜。
        when(orderItemRepository.findByCartIdAndMenuItemId(10L, 1L)).thenReturn(orderItem);

        when(orderItemRepository.sumPriceByCartId(10L)).thenReturn(15.0);

        // 执行加购。
        cartService.addMenuItemToCart(20L, 1L);

        // 验证：保存的订单项 id 仍然是 30，数量从 2 变成 3。
        verify(orderItemRepository).save(new OrderItemEntity(30L, 1L, 10L, 10.0, 3));

        // 验证：购物车总价从 5.0 更新到 15.0。
        verify(cartRepository).updateTotalPrice(10L, 15.0);
    }

    // 测试场景：购物车有一条订单项时，getCart 应该返回 CartDto。
    @Test
    void getCart_oneOrderItem_returnsCartDto() {
        // 创建测试购物车。
        CartEntity cart = new CartEntity(10L, 20L, 10.0);

        // 创建测试订单项。
        OrderItemEntity orderItem = new OrderItemEntity(30L, 1L, 10L, 10.0, 1);

        // 创建测试菜单项。
        MenuItemEntity menuItem = new MenuItemEntity(1L, 2L, "beef", "beef description", 10.0, "image.jpg");

        // mock 根据 customerId 查询购物车。
        when(cartRepository.getByCustomerId(20L)).thenReturn(cart);

        // mock 根据 cartId 查询订单项列表。
        when(orderItemRepository.getAllByCartId(10L)).thenReturn(List.of(orderItem));

        // mock 根据 menuItemId 集合批量查询菜单。
        when(menuItemRepository.findAllById(Set.of(1L))).thenReturn(List.of(menuItem));

        // 执行 getCart，拿到返回 DTO。
        CartDto cartDto = cartService.getCart(20L);

        // 断言购物车 id 正确。
        assertEquals(10L, cartDto.id());

        // 断言 customerId 正确。
        assertEquals(20L, cartDto.customerId());

        // 断言总价正确。
        assertEquals(10.0, cartDto.totalPrice());

        // 断言购物车里有 1 条订单项。
        assertEquals(1, cartDto.orderItems().size());

        // 断言第一条订单项 id 是 30。
        assertEquals(30L, cartDto.orderItems().get(0).id());

        // 断言第一条订单项里的菜品 id 是 1。
        assertEquals(1L, cartDto.orderItems().get(0).menuItemId());

        // 断言第一条订单项里的菜名是 beef。
        // 后端字段叫 menuItemName，返回 JSON 时会被 Jackson 转成 menu_item_name。
        assertEquals("beef", cartDto.orderItems().get(0).menuItemName());
    }

    // 测试场景：清空购物车应该删除订单项并重置总价。
    @Test
    void clearCart_deletesOrderItemsAndResetsTotalPrice() {
        // 创建测试购物车。
        CartEntity cart = new CartEntity(10L, 20L, 10.0);

        // mock 根据 customerId 查询购物车。
        when(cartRepository.getByCustomerId(20L)).thenReturn(cart);

        // 执行清空购物车。
        cartService.clearCart(20L);

        // 验证：应该删除 cartId=10 下的全部订单项。
        verify(orderItemRepository).deleteByCartId(10L);

        // 验证：应该把购物车总价重置为 0.0。
        verify(cartRepository).updateTotalPrice(10L, 0.0);
    }
}
