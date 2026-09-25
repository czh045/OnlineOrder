// controller 包放接口层，负责接收前端或 Postman 发来的 HTTP 请求。
package com.laioffer.onlineorder.controller;

// RegisterBody 是注册请求体。
// 前端传来的 JSON 会被 Spring 自动转换成这个 record。
import com.laioffer.onlineorder.model.RegisterBody;
import com.laioffer.onlineorder.model.CurrentUserDto;

// CustomerService 负责用户业务逻辑，比如注册用户、查询用户。
import com.laioffer.onlineorder.service.CustomerService;

// HttpStatus 是 HTTP 状态码枚举。
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;

// PostMapping 表示处理 POST 请求。
import org.springframework.web.bind.annotation.PostMapping;

// RequestBody 表示从请求体读取 JSON。
import org.springframework.web.bind.annotation.RequestBody;

// ResponseStatus 可以指定接口成功时返回哪个 HTTP 状态码。
import org.springframework.web.bind.annotation.ResponseStatus;

// RestController 表示该类里的方法返回 JSON/状态码，而不是页面。
import org.springframework.web.bind.annotation.RestController;

// @RestController 让 Spring 把这个类注册成接口控制器。
@RestController

// CustomerController 专门处理用户相关接口。
public class CustomerController {

    // 保存 CustomerService。
    // Controller 不直接写数据库逻辑，只调用 Service。
    private final CustomerService customerService;

    // 构造函数注入。
    // Spring 创建 CustomerController 时会自动传入 CustomerService。
    public CustomerController(CustomerService customerService) {
        // 把传入的 customerService 保存下来，给接口方法使用。
        this.customerService = customerService;
    }

    // POST /signup：
    // 注册接口。用户没登录也可以调用，所以 AppConfig 里放行了这个路径。
    @PostMapping("/signup")

    // 注册成功后返回 201 CREATED。
    // 201 比 200 更准确，因为这里创建了一个新用户。
    @ResponseStatus(value = HttpStatus.CREATED)

    // @RequestBody RegisterBody body：
    // 把请求体 JSON 转成 RegisterBody。
    // 例如 { "email": "...", "password": "...", "first_name": "...", "last_name": "..." }。
    public void signUp(@RequestBody RegisterBody body) {
        // Controller 只负责接请求和转发参数。
        // 真正的注册流程交给 CustomerService.signUp。
        customerService.signUp(body.email(), body.password(), body.firstName(), body.lastName());
    }

    // GET /me 让 React 前端得到当前登录用户和管理员身份，不需要猜测角色。
    @GetMapping("/me")
    public CurrentUserDto getCurrentUser(Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);
        return new CurrentUserDto(authentication.getName(), isAdmin);
    }
}
