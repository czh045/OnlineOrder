package com.laioffer.onlineorder.controller;

import com.laioffer.onlineorder.model.RecommendationDto;
import com.laioffer.onlineorder.model.RecommendationRequestBody;
import com.laioffer.onlineorder.service.RecommendationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @PostMapping("/recommendations")
    public RecommendationDto recommend(@RequestBody RecommendationRequestBody body) {
        return recommendationService.recommend(body == null ? null : body.message());
    }
}
