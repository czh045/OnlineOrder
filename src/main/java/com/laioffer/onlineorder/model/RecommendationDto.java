package com.laioffer.onlineorder.model;

import java.util.List;

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
