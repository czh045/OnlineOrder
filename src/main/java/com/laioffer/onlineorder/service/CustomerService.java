// service 包放业务层代码。
// 用户注册、创建购物车这类流程不应该写在 Controller 里，而应该写在 Service 里。
package com.laioffer.onlineorder.service;

// CartEntity 对应 carts 表，表示购物车。
import com.laioffer.onlineorder.entity.CartEntity;

// CustomerEntity 对应 customers 表，表示用户。
import com.laioffer.onlineorder.entity.CustomerEntity;

// CartRepository 负责访问 carts 表。
import com.laioffer.onlineorder.repository.CartRepository;

// CustomerRepository 负责访问 customers 表。
import com.laioffer.onlineorder.repository.CustomerRepository;

// User 是 Spring Security 提供的用户构建工具。
import org.springframework.security.core.userdetails.User;

// UserDetails 是 Spring Security 表示“登录用户信息”的接口。
import org.springframework.security.core.userdetails.UserDetails;

// PasswordEncoder 用来加密密码。
import org.springframework.security.crypto.password.PasswordEncoder;

// UserDetailsManager 是 Spring Security 用来创建、查询、修改用户的接口。
import org.springframework.security.provisioning.UserDetailsManager;

// Service 注解表示这是业务层组件。
import org.springframework.stereotype.Service;

// Transactional 表示方法在数据库事务中执行。
import org.springframework.transaction.annotation.Transactional;

// @Service 让 Spring 扫描并管理这个类。
@Service
public class CustomerService {

    // cartRepository 用来创建用户注册后的空购物车。
    private final CartRepository cartRepository;

    // customerRepository 用来查询和更新 customers 表里的业务资料。
    private final CustomerRepository customerRepository;

    // passwordEncoder 用来把明文密码变成哈希密码。
    private final PasswordEncoder passwordEncoder;

    // userDetailsManager 用来调用 Spring Security 的用户创建逻辑。
    private final UserDetailsManager userDetailsManager;

    // 构造函数注入。
    // Spring 会自动把需要的 Repository、PasswordEncoder、UserDetailsManager 传进来。
    public CustomerService(
            // 购物车数据访问对象。
            CartRepository cartRepository,
            // 用户数据访问对象。
            CustomerRepository customerRepository,
            // 密码加密器。
            PasswordEncoder passwordEncoder,
            // Spring Security 用户管理器。
            UserDetailsManager userDetailsManager
    ) {
        // 保存 CartRepository。
        this.cartRepository = cartRepository;

        // 保存 CustomerRepository。
        this.customerRepository = customerRepository;

        // 保存 PasswordEncoder。
        this.passwordEncoder = passwordEncoder;

        // 保存 UserDetailsManager。
        this.userDetailsManager = userDetailsManager;
    }

    // 注册流程必须是一个事务。
    // 因为它包含三件事：创建登录账号、更新姓名、创建购物车。
    // 如果中间任意一步失败，前面的数据库操作应该回滚。
    @Transactional

    // signUp 是注册用户的业务方法。
    // Controller 接到 POST /signup 后，会把 email/password/firstName/lastName 传进来。
    public void signUp(String email, String password, String firstName, String lastName) {
        // 把邮箱转成小写，避免 Foo@mail.com 和 foo@mail.com 被当成两个账号。
        email = email.toLowerCase();

        // 使用 Spring Security 的 User.builder() 构造登录用户对象。
        UserDetails user = User.builder()

                // username 是 Spring Security 的叫法。
                // 在这个项目里，username 实际上就是 email。
                .username(email)

                // passwordEncoder.encode(password) 会把明文密码加密。
                // 例如用户输入 123456，数据库里不会存 123456，而是存哈希结果。
                .password(passwordEncoder.encode(password))

                // roles("USER") 表示给这个用户添加 USER 角色。
                // Spring Security 会自动把它变成 ROLE_USER 存进 authorities 表。
                .roles("USER")

                // build() 真正创建 UserDetails 对象。
                .build();

        // 让 Spring Security 创建用户。
        // 根据 AppConfig 里的 SQL，它会往 customers 表和 authorities 表插入数据。
        userDetailsManager.createUser(user);

        // Spring Security 创建用户时只会写 email/password/enabled。
        // firstName 和 lastName 是我们项目自己的业务字段，所以这里再单独更新。
        customerRepository.updateNameByEmail(email, firstName, lastName);

        // 查出刚创建好的用户。
        // 需要它的 id，因为 carts 表要用 customer_id 关联用户。
        CustomerEntity savedCustomer = customerRepository.findByEmail(email);

        // 创建一个新的购物车实体。
        // 第一个参数 id 传 null，表示让数据库自动生成主键。
        // 第二个参数是 customer_id，表示这个购物车属于哪个用户。
        // 第三个参数 totalPrice 初始为 0.0。
        CartEntity cart = new CartEntity(null, savedCustomer.id(), 0.0);

        // 把新购物车保存进 carts 表。
        cartRepository.save(cart);
    }

    // 按邮箱查询用户。
    // Controller 经常需要根据当前登录 email 找到 customers.id。
    public CustomerEntity getCustomerByEmail(String email) {
        // 调用 Repository 查询数据库。
        return customerRepository.findByEmail(email);
    }
}
