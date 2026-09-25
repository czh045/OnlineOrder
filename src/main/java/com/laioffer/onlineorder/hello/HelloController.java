// hello 包是早期课程里的简单示例。
// 它不属于核心点餐业务，主要用来练习 Spring Controller 和 JSON 返回。
package com.laioffer.onlineorder.hello;

// GetMapping 表示处理 GET 请求。
import org.springframework.web.bind.annotation.GetMapping;

// RequestParam 表示从 URL 查询参数里取值。
// 例如 /hello?name=Tom 里的 name=Tom。
import org.springframework.web.bind.annotation.RequestParam;

// RestController 表示返回值自动变成 JSON。
import org.springframework.web.bind.annotation.RestController;

// @RestController 让 Spring 把这个类当作接口控制器。
@RestController
public class HelloController {

    // GET /hello：
    // 浏览器访问 http://localhost:8080/hello 会执行这个方法。
    @GetMapping("/hello")

    // sayHello 返回 Person 对象。
    // Spring/Jackson 会把 Person 自动序列化成 JSON。
    public Person sayHello(
            // @RequestParam 从 URL 参数里取 name。
            // name = "name" 表示参数名叫 name。
            // required = false 表示这个参数不是必须传。
            // defaultValue = "Guest" 表示没传 name 时默认用 Guest。
            @RequestParam(name = "name", required = false, defaultValue = "Guest") String name
    ) {
        // 创建并返回一个 Person 对象。
        return new Person(
                // 使用 URL 里传进来的 name。
                name,

                // company 字段写死为 laioffer。
                "laioffer",

                // 创建 Address 对象作为 homeAddress。
                new Address("123 Happy Street", "San Francisco", "California", null),

                // 创建 Book 对象作为 favoriteBook。
                new Book("Clean Code", "John Smith")
        );
    }
}
