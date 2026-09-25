-- database-init.sql 会在 Spring Boot 启动时自动执行。
-- 它的作用是创建数据库表，并插入一些餐厅和菜单的演示数据。

-- 先删旧表，保证每次启动时都能重新初始化一套干净的数据。
-- 注意删除顺序：先删子表，再删父表。
-- 例如 order_items 依赖 menu_items 和 carts，所以必须先删 order_items。
DROP TABLE IF EXISTS order_line_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS payment_methods;
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS menu_items;
DROP TABLE IF EXISTS restaurants;
DROP TABLE IF EXISTS carts;
DROP TABLE IF EXISTS authorities;
DROP TABLE IF EXISTS customers;

-- 用户主表：存登录邮箱、密码、是否启用，以及姓名信息。
CREATE TABLE customers
(
    -- id 是主键。
    -- SERIAL 表示 PostgreSQL 自动生成递增整数。
    id SERIAL PRIMARY KEY,

    -- email 是用户登录名。
    -- UNIQUE 表示不能重复，NOT NULL 表示不能为空。
    email TEXT UNIQUE NOT NULL,

    -- enabled 是 Spring Security 要用的字段。
    -- TRUE 表示账号启用，FALSE 表示账号禁用。
    enabled BOOLEAN DEFAULT TRUE NOT NULL,

    -- password 存加密后的密码。
    -- 不要把明文密码存数据库。
    password TEXT NOT NULL,

    -- first_name 是用户名字。
    first_name TEXT,

    -- last_name 是用户姓氏。
    last_name TEXT
);

-- 权限表：Spring Security 用它来判断用户角色。
CREATE TABLE authorities
(
    -- 权限记录自己的主键。
    id SERIAL PRIMARY KEY,

    -- email 对应 customers.email。
    -- Spring Security 默认会用 username 查权限，这里 username 就是 email。
    email TEXT NOT NULL,

    -- authority 表示权限，比如 ROLE_USER。
    authority TEXT NOT NULL,

    -- 给这条外键约束起名字，方便数据库报错时定位。
    CONSTRAINT fk_authorities_customer

        -- authorities.email 引用 customers.email。
        -- ON DELETE CASCADE 表示删除用户时，自动删除他的权限记录。
        FOREIGN KEY (email) REFERENCES customers (email) ON DELETE CASCADE,

    CONSTRAINT uq_authorities_email_authority UNIQUE (email, authority)
);

-- 每个用户只有一个购物车，所以 customer_id 设成 UNIQUE。
CREATE TABLE carts
(
    -- 购物车主键。
    id SERIAL PRIMARY KEY,

    -- customer_id 指向 customers.id。
    -- UNIQUE 表示一个用户最多只有一个购物车。
    customer_id INTEGER UNIQUE NOT NULL,

    -- total_price 表示购物车总价。
    -- NUMERIC(10, 2) 表示最多 10 位数字，小数点后 2 位。
    total_price NUMERIC(10, 2) DEFAULT 0.00 NOT NULL,

    -- carts.customer_id 到 customers.id 的外键约束。
    CONSTRAINT fk_carts_customer

        -- 删除用户时，自动删除他的购物车。
        FOREIGN KEY (customer_id) REFERENCES customers (id) ON DELETE CASCADE
);

-- 餐厅主表。
CREATE TABLE payment_methods
(
    id           SERIAL PRIMARY KEY,
    customer_id  INTEGER NOT NULL,
    card_holder  TEXT NOT NULL,
    brand        TEXT NOT NULL,
    last_four    TEXT NOT NULL,
    expiry_month INTEGER NOT NULL,
    expiry_year  INTEGER NOT NULL,
    CONSTRAINT fk_payment_methods_customer
        FOREIGN KEY (customer_id) REFERENCES customers (id) ON DELETE CASCADE
);

CREATE TABLE restaurants
(
    -- 餐厅主键。
    id SERIAL PRIMARY KEY,

    -- 餐厅名称。
    name TEXT NOT NULL,

    -- 餐厅地址。
    address TEXT NOT NULL,

    -- 餐厅图片 URL。
    image_url TEXT,

    -- 餐厅电话。
    phone TEXT
);

-- 菜单表：一条菜单属于一家餐厅。
CREATE TABLE menu_items
(
    -- 菜品主键。
    id SERIAL PRIMARY KEY,

    -- restaurant_id 指向 restaurants.id。
    restaurant_id INTEGER NOT NULL,

    -- 菜名。
    name TEXT NOT NULL,

    -- 价格。
    price NUMERIC(10, 2) NOT NULL,

    -- 菜品描述。
    description TEXT,

    -- 菜品图片 URL。
    image_url TEXT,

    -- 菜单到餐厅的外键约束。
    CONSTRAINT fk_menu_items_restaurant

        -- 删除餐厅时，自动删除这家餐厅的菜单。
        FOREIGN KEY (restaurant_id) REFERENCES restaurants (id) ON DELETE CASCADE
);

-- 订单项表：购物车里的每一行菜品。
CREATE TABLE orders
(
    id                SERIAL PRIMARY KEY,
    customer_id       INTEGER NOT NULL,
    payment_method_id INTEGER,
    total_price       NUMERIC(10, 2) NOT NULL,
    status            TEXT NOT NULL,
    created_at        TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT fk_orders_customer
        FOREIGN KEY (customer_id) REFERENCES customers (id) ON DELETE CASCADE,
    CONSTRAINT fk_orders_payment_method
        FOREIGN KEY (payment_method_id) REFERENCES payment_methods (id) ON DELETE SET NULL
);

CREATE TABLE order_line_items
(
    id             SERIAL PRIMARY KEY,
    order_id       INTEGER NOT NULL,
    menu_item_id   INTEGER,
    menu_item_name TEXT NOT NULL,
    price          NUMERIC(10, 2) NOT NULL,
    quantity       INTEGER NOT NULL,
    CONSTRAINT fk_order_line_items_order
        FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE,
    CONSTRAINT fk_order_line_items_menu_item
        FOREIGN KEY (menu_item_id) REFERENCES menu_items (id) ON DELETE SET NULL
);

CREATE TABLE order_items
(
    -- 订单项主键。
    id SERIAL PRIMARY KEY,

    -- menu_item_id 指向 menu_items.id，表示这行买的是哪道菜。
    menu_item_id INTEGER NOT NULL,

    -- cart_id 指向 carts.id，表示这行商品属于哪个购物车。
    cart_id INTEGER NOT NULL,

    -- price 是加入购物车时的单价。
    price NUMERIC(10, 2) NOT NULL,

    -- quantity 是购买数量。
    quantity INTEGER NOT NULL,

    -- 订单项到购物车的外键约束。
    CONSTRAINT fk_order_items_cart

        -- 删除购物车时，自动删除它下面的订单项。
        FOREIGN KEY (cart_id) REFERENCES carts (id) ON DELETE CASCADE,

    -- 订单项到菜单项的外键约束。
    CONSTRAINT fk_order_items_menu_item

        -- 删除菜品时，自动删除引用它的订单项。
        FOREIGN KEY (menu_item_id) REFERENCES menu_items (id) ON DELETE CASCADE
);

-- 插入餐厅演示数据。
-- INSERT INTO restaurants 后面的列顺序是 name、address、image_url、phone。
-- VALUES 里每一组括号就是一条餐厅记录。
INSERT INTO restaurants (name, address, image_url, phone)
VALUES
    (
        'Burger King',
        '773 N Mathilda Ave, Sunnyvale, CA 94085',
        'https://img.cdn4dd.com/cdn-cgi/image/fit=contain,width=1920,format=auto,quality=50/https://cdn.doordash.com/media/store%2Fheader%2F10171.png',
        '(408) 736-0101'
    ),
    (
        'SGD Tofu House',
        '3450 El Camino Real #105, Santa Clara, CA 95051',
        'https://img.cdn4dd.com/cdn-cgi/image/fit=contain,width=1920,format=auto,quality=50/https://cdn.doordash.com/media/store%2Fheader%2F1579.jpg',
        '(408) 261-3030'
    ),
    (
        'Fashion Wok',
        '163 S Murphy Ave, Sunnyvale, CA 94086',
        'https://img.cdn4dd.com/cdn-cgi/image/fit=contain,width=1920,format=auto,quality=50/https://cdn.doordash.com/media/store%2Fheader%2F273997.jpg',
        '(408) 739-8866'
    );

-- 插入菜单演示数据。
-- INSERT INTO menu_items 后面的列顺序是 description、image_url、name、price、restaurant_id。
-- 所以每组值分别表示：描述、图片、菜名、价格、所属餐厅 ID。
INSERT INTO menu_items (description, image_url, name, price, restaurant_id)
VALUES
    (
        'Made with white meat chicken, our Chicken Fries are coated in a light crispy breading seasoned with savory spices and herbs.',
        'https://img.cdn4dd.com/cdn-cgi/image/fit=contain,width=300,format=auto,quality=50/https://cdn.doordash.com/media/photos/f439436f-c5ab-47af-bac4-7b73ab60a24b-retina-large.jpg',
        'Chicken Fries - 9 Pc',
        4.89,
        1
    ),
    (
        'Our Whopper Sandwich is a 1/4 lb* of savory flame-grilled beef topped with juicy tomatoes, fresh lettuce, creamy mayonnaise, ketchup, crunchy pickles, and sliced white onions on a soft sesame seed bun.',
        'https://img.cdn4dd.com/cdn-cgi/image/fit=contain,width=300,format=auto,quality=50/https://cdn.doordash.com/media/photos/f878a689-618b-4c70-a00f-e7b1f320adc9-retina-large.jpg',
        'Whopper Meal',
        10.59,
        1
    ),
    (
        'Our Impossible Whopper Sandwich features a savory flame-grilled patty made from plants topped with juicy tomatoes, fresh lettuce, creamy mayonnaise, ketchup, crunchy pickles, and sliced white onions on a soft sesame seed bun.',
        'https://img.cdn4dd.com/cdn-cgi/image/fit=contain,width=1920,format=auto,quality=50/https://cdn.doordash.com/media/photos/5c306a5f-fdd2-41d2-a660-9762aaa8eee8-retina-large.jpg',
        'Impossible Whopper',
        7.99,
        1
    ),
    (
        'Say hello to our HERSHEY''S Sundae Pie. One part crunchy chocolate crust and one part chocolate creme filling, garnished with a delicious topping and real HERSHEY''S Chocolate Chips.',
        'https://img.cdn4dd.com/cdn-cgi/image/fit=contain,width=1920,format=auto,quality=50/https://cdn.doordash.com/media/photos/80b1670d-e9c0-4886-a5b7-1ad48edd24ca-retina-large.jpg',
        'HERSHEY''S Sundae Pie',
        3.09,
        1
    ),
    (
        'Our Whopper Sandwich is a 1/4 lb* of savory flame-grilled beef topped with juicy tomatoes, fresh lettuce, creamy mayonnaise, ketchup, crunchy pickles, and sliced white onions on a soft sesame seed bun.',
        'https://img.cdn4dd.com/cdn-cgi/image/fit=contain,width=1920,format=auto,quality=50/https://cdn.doordash.com/media/photos/9b3d7985-e457-43b3-938d-5184f48c2687-retina-large-jpeg',
        'Whopper',
        6.39,
        1
    ),
    (
        'Our Double Whopper Sandwich is a pairing of two 1/4 lb* savory flame-grilled beef patties topped with juicy tomatoes, fresh lettuce, creamy mayonnaise, ketchup, crunchy pickles, and sliced white onions on a soft sesame seed bun.',
        'https://img.cdn4dd.com/cdn-cgi/image/fit=contain,width=1920,format=auto,quality=50/https://cdn.doordash.com/media/photos/45addf4a-e8a8-47cb-a705-cce1d10ce86d-retina-large.jpg',
        'Double Whopper Meal',
        11.69,
        1
    ),
    (
        'Our Spicy Crispy Chicken Sandwich is crispy, juicy, and served on a soft sesame seed bun.',
        'https://img.cdn4dd.com/cdn-cgi/image/fit=contain,width=1920,format=auto,quality=50/https://cdn.doordash.com/media/photos/31dd68c2-06ec-42ad-bcd4-da7bd3425437-retina-large-jpeg',
        'Spicy Crispy Chicken Sandwich',
        6.09,
        1
    ),
    (
        'Our Original Chicken Sandwich is lightly breaded and topped with shredded lettuce and creamy mayonnaise on a sesame seed bun.',
        'https://img.cdn4dd.com/cdn-cgi/image/fit=contain,width=1920,format=auto,quality=50/https://cdn.doordash.com/media/photos/3e437f54-fa4e-4e9d-bf80-8a1e5b120f32-retina-large-jpeg',
        'Original Chicken Sandwich',
        6.09,
        1
    ),
    (
        'Our Bacon King Sandwich features two flame-grilled beef patties, bacon, American cheese, ketchup, and creamy mayonnaise on a soft sesame seed bun.',
        'https://img.cdn4dd.com/cdn-cgi/image/fit=contain,width=1920,format=auto,quality=50/https://cdn.doordash.com/media/photos/adb96c32-3c5b-4375-ba92-b30767d2513d-retina-large.jpg',
        'Bacon King Sandwich Meal',
        12.19,
        1
    ),
    (
        'Cool down with our creamy hand spun OREO Shake.',
        'https://img.cdn4dd.com/cdn-cgi/image/fit=contain,width=1920,format=auto,quality=50/https://cdn.doordash.com/media/photos/c3ad483f-bad7-44f1-96af-4c3dcfc63c6d-retina-large.jpg',
        'Classic OREO Shake',
        3.99,
        1
    ),
    (
        'Tofu boiled with your choice of meat and mushrooms. Served with your choice of side and an assortment of kimchi dishes.',
        'https://img.cdn4dd.com/cdn-cgi/image/fit=contain,width=1920,format=auto,quality=50/https://cdn.doordash.com/media/photos/b7055ca9-3caf-4d9d-9c99-04be1e36dbbf-retina-large-jpeg',
        'Original Soft Tofu',
        17.06,
        2
    ),
    (
        'Tofu boiled with beef, shrimp, and clams. Served with your choice of side and an assortment of kimchi dishes.',
        'https://img.cdn4dd.com/cdn-cgi/image/fit=contain,width=1920,format=auto,quality=50/https://cdn.doordash.com/media/photos/37ad1974-1395-4e5c-86ff-fdf120cf8c58-retina-large-jpeg',
        'Combination Soft Tofu',
        17.06,
        2
    ),
    (
        'Tofu boiled with mussels, shrimp, and clam. Served with your choice of side and an assortment of kimchi dishes.',
        'https://img.cdn4dd.com/cdn-cgi/image/fit=contain,width=1920,format=auto,quality=50/https://cdn.doordash.com/media/photos/96bc8289-1950-4b4f-823d-12f33349a5fe-retina-large-jpeg',
        'Seafood Soft Tofu',
        17.06,
        2
    ),
    (
        'Squid, clam, imitation crab, and grilled onions fried in batter.',
        'https://img.cdn4dd.com/cdn-cgi/image/fit=contain,width=1920,format=auto,quality=50/https://cdn.doordash.com/media/photos/0a94b7e9-903d-49b7-937a-7940c8b56ad5-retina-large-jpeg',
        'Seafood Pancake',
        20.27,
        2
    ),
    (
        'Tofu boiled with kimchi and your choice of meat. Served with your choice of side and an assortment of kimchi dishes.',
        'https://img.cdn4dd.com/cdn-cgi/image/fit=contain,width=1920,format=auto,quality=50/https://cdn.doordash.com/media/photos/0c062cff-1868-40e1-946d-29d3e46f1541-retina-large-jpeg',
        'Kimchi Soft Tofu',
        17.06,
        2
    ),
    (
        'Beef short ribs served with rice and an assortment of kimchi dishes.',
        'https://img.cdn4dd.com/cdn-cgi/image/fit=contain,width=1920,format=auto,quality=50/https://cdn.doordash.com/media/photos/6340c369-2485-4d60-afcf-ca9068448d84-retina-large.jpg',
        'Beef Short Ribs',
        29.36,
        2
    ),
    (
        'Tofu boiled with dumplings, rice cake, and beef. Served with your choice of side and an assortment of kimchi dishes.',
        'https://img.cdn4dd.com/cdn-cgi/image/fit=contain,width=1920,format=auto,quality=50/https://cdn.doordash.com/media/photos/b7055ca9-3caf-4d9d-9c99-04be1e36dbbf-retina-large-jpeg',
        'Dumpling Soft Tofu',
        17.06,
        2
    ),
    (
        'Tofu boiled with assorted mushrooms. Served with your choice of side and an assortment of kimchi dishes.',
        'https://img.cdn4dd.com/cdn-cgi/image/fit=contain,width=1920,format=auto,quality=50/https://cdn.doordash.com/media/photos/b7055ca9-3caf-4d9d-9c99-04be1e36dbbf-retina-large-jpeg',
        'Assorted Mushroom Tofu',
        17.06,
        2
    ),
    (
        'Rice, BBQ beef, and vegetables served in stoneware with an assortment of kimchi dishes.',
        'https://img.cdn4dd.com/cdn-cgi/image/fit=contain,width=1920,format=auto,quality=50/https://cdn.doordash.com/media/photos/9844dd4e-3c74-4942-8f90-2b3f4be25049-retina-large-jpeg',
        'BBQ Beef & Vegetables in Stoneware',
        20.27,
        2
    ),
    (
        'Tofu boiled with ham and cheese. Served with your choice of side and an assortment of kimchi dishes.',
        'https://img.cdn4dd.com/cdn-cgi/image/fit=contain,width=1920,format=auto,quality=50/https://cdn.doordash.com/media/photos/9c6b2a1c-1e2c-4d80-a111-2bebbcadd64c-retina-large.jpg',
        'Ham & Cheese Soft Tofu',
        17.06,
        2
    ),
    (
        'Medium spicy.',
        'https://img.cdn4dd.com/cdn-cgi/image/fit=contain,width=1920,format=auto,quality=50/https://cdn.doordash.com/media/photos/5b34852e-d253-461c-8be8-1bb0bc5e39be-retina-large.jpg',
        '农家小炒肉Stir Fried Pork with Pepper',
        13.99,
        3
    ),
    (
        '',
        'https://img.cdn4dd.com/cdn-cgi/image/fit=contain,width=1920,format=auto,quality=50/https://cdn.doordash.com/media/photos/bf70f262-0c55-41e1-89bc-84c061ae485f-retina-large.jpg',
        'Eggplant with Minced Pork, Garlic, Cilantro',
        14.99,
        3
    ),
    (
        'Mild spicy.',
        'https://img.cdn4dd.com/cdn-cgi/image/fit=contain,width=1920,format=auto,quality=50/https://cdn.doordash.com/media/photos/cb870c77-ace1-49ec-aa2f-9e18de102242-retina-large.jpg',
        '大盆花菜Stir Fried Cauliflower with Pork',
        14.99,
        3
    ),
    (
        'Mild spicy.',
        'https://img.cdn4dd.com/cdn-cgi/image/fit=contain,width=1920,format=auto,quality=50/https://cdn.doordash.com/media/photos/1acf9c6b-189d-4583-a151-7ef522c283d9-retina-large.jpg',
        '酸汤鱼片Poached Fish Fillets in Sour Soup',
        17.99,
        3
    ),
    (
        'Very spicy.',
        'https://img.cdn4dd.com/cdn-cgi/image/fit=contain,width=1920,format=auto,quality=50/https://cdn.doordash.com/media/photos/7f05859d-5e83-476d-a45a-73a3eb8a94e0-retina-large.jpg',
        '小炒黄牛肉Stir Fried Beef with Pepper',
        16.99,
        3
    ),
    (
        'Medium spicy.',
        'https://img.cdn4dd.com/cdn-cgi/image/fit=contain,width=1920,format=auto,quality=50/https://cdn.doordash.com/media/photos/8b2ca9fc-2c1d-4bf2-96ff-d0bd3c415e8d-retina-large.jpg',
        '武冈香干炒肚丝Stir Fried Shredded Tripe with Wugang Tofu',
        19.99,
        3
    ),
    (
        'Very spicy.',
        'https://img.cdn4dd.com/cdn-cgi/image/fit=contain,width=1920,format=auto,quality=50/https://cdn.doordash.com/media/photos/89ad8679-346e-41d8-b98f-3501fff4b277-retina-large.jpg',
        '水煮牛肉Poached Sliced Beef in Hot Chili Oil',
        17.99,
        3
    ),
    (
        'With chopped broccoli, peas, carrots, bok choy, and egg.',
        'https://img.cdn4dd.com/cdn-cgi/image/fit=contain,width=1920,format=auto,quality=50/https://cdn.doordash.com/media/photos/ec06c431-9426-4971-a129-920440e1c9ce-retina-large.jpg',
        'Fried Rice炒饭',
        9.50,
        3
    ),
    (
        'Very spicy.',
        'https://img.cdn4dd.com/cdn-cgi/image/fit=contain,width=1920,format=auto,quality=50/https://cdn.doordash.com/media/photos/2fe1b87f-d41f-4fa4-8cae-5f2ee5bb97e4-retina-large.jpg',
        '擂辣椒茄子皮蛋Smashed Green Pepper, Chinese Eggplant & Preserved Egg',
        11.99,
        3
    ),
    (
        '',
        'https://img.cdn4dd.com/cdn-cgi/image/fit=contain,width=1920,format=auto,quality=50/https://cdn.doordash.com/media/photos/a307e73d-dd12-4841-be14-6f5825a64c59-retina-large.jpg',
        '蒜蓉油麦菜Stir Fried A-Choy with Minced Garlic',
        10.99,
        3
    );
