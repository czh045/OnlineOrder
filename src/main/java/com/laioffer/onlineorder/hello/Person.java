// hello 包里的示例数据类。
package com.laioffer.onlineorder.hello;

// Person 是一个 Java record。
// record 适合表示“只有数据、没有复杂行为”的对象。
public record Person(
        // name 表示人的名字。
        String name,

        // company 表示公司或组织。
        String company,

        // homeAddress 是嵌套对象。
        // 返回 JSON 时，它会变成一个嵌套 JSON object。
        Address homeAddress,

        // favoriteBook 也是嵌套对象。
        Book favoriteBook
) {
    // record 会自动生成构造函数和访问方法：
    // person.name()、person.company()、person.homeAddress()、person.favoriteBook()。
}
