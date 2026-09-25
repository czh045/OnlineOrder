// hello 包里的示例数据类。
package com.laioffer.onlineorder.hello;

// Book 表示一本书。
// 它会作为 Person.favoriteBook 的嵌套对象返回。
public record Book(
        // title 表示书名。
        String title,

        // author 表示作者。
        String author
) {
    // record 自动生成 book.title() 和 book.author() 方法。
}
