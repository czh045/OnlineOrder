package com.laioffer.onlineorder.model;

import java.util.List;

// 这个 DTO 把推荐结果限制为真实存在的菜单项，前端可直接加入购物车。
public record RecommendationDto(
        String summary,
        List<RecommendedItem> recommendations,
        String disclaimer
) {
    public record RecommendedItem(
            Long menuItemId,
            String name,
            Double price,
            String imageUrl,
            String reason
    ) {
    }
}
