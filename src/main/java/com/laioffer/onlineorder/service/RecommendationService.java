package com.laioffer.onlineorder.service;

import com.laioffer.onlineorder.entity.MenuItemEntity;
import com.laioffer.onlineorder.model.RecommendationDto;
import com.laioffer.onlineorder.repository.MenuItemRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// 不依赖付费 API 的本地推荐：按菜名/描述关键词和预算过滤真实菜单项。
// 后续接入大模型时，可以保留此层作为候选集检索和无 API Key 时的降级方案。
@Service
public class RecommendationService {

    private static final Pattern BUDGET_PATTERN =
            Pattern.compile("(?:under|below|less than)\\s*\\$?(\\d+(?:\\.\\d+)?)", Pattern.CASE_INSENSITIVE);

    private final MenuItemRepository menuItemRepository;

    public RecommendationService(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    public RecommendationDto recommend(String message) {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Please describe what you would like to eat");
        }

        String normalizedMessage = message.toLowerCase(Locale.ROOT);
        Double budget = extractBudget(normalizedMessage);
        List<String> keywords = List.of(normalizedMessage.split("[^a-z0-9]+")).stream()
                .filter(word -> word.length() >= 3)
                .filter(word -> !List.of("something", "please", "would", "like", "with", "under", "below", "than")
                        .contains(word))
                .toList();

        List<MenuItemEntity> rankedItems = menuItemRepository.findAll().stream()
                .filter(item -> budget == null || item.price() <= budget)
                .sorted(Comparator
                        .comparingInt((MenuItemEntity item) -> score(item, keywords))
                        .reversed()
                        .thenComparing(MenuItemEntity::price))
                .limit(5)
                .toList();

        List<RecommendationDto.RecommendedItem> recommendations = rankedItems.stream()
                .filter(item -> keywords.isEmpty() || score(item, keywords) > 0)
                .map(item -> new RecommendationDto.RecommendedItem(
                        item.id(),
                        item.name(),
                        item.price(),
                        item.imageUrl(),
                        buildReason(item, budget, keywords)
                ))
                .toList();

        String summary = recommendations.isEmpty()
                ? "I could not find an exact match. Try a broader food preference or a higher budget."
                : "Here are menu items selected from the current catalog based on your preference.";
        return new RecommendationDto(
                summary,
                recommendations,
                "Suggestions are based on menu descriptions. Please confirm ingredients for allergies or dietary needs."
        );
    }

    private int score(MenuItemEntity item, List<String> keywords) {
        String name = item.name().toLowerCase(Locale.ROOT);
        String description = item.description() == null ? "" : item.description().toLowerCase(Locale.ROOT);
        return keywords.stream()
                .mapToInt(keyword -> (name.contains(keyword) ? 5 : 0) + (description.contains(keyword) ? 1 : 0))
                .sum();
    }

    private String buildReason(MenuItemEntity item, Double budget, List<String> keywords) {
        if (budget != null) {
            return "Matches your budget at $" + String.format("%.2f", item.price()) + ".";
        }
        if (!keywords.isEmpty()) {
            return "Matches your request through the current menu name or description.";
        }
        return "A currently available menu item.";
    }

    private Double extractBudget(String message) {
        Matcher matcher = BUDGET_PATTERN.matcher(message);
        return matcher.find() ? Double.parseDouble(matcher.group(1)) : null;
    }
}
