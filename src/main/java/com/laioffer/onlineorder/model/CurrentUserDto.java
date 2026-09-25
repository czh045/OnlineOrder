package com.laioffer.onlineorder.model;

// 前端用它判断是否展示管理员操作，不把整个 Security 用户对象暴露出去。
public record CurrentUserDto(String email, boolean isAdmin) {
}
