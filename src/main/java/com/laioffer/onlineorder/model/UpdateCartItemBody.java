package com.laioffer.onlineorder.model;

// PATCH /cart/items/{id} 的请求体；quantity 为 0 表示从购物车删除该商品。
public record UpdateCartItemBody(Integer quantity) {
}
