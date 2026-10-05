package com.laioffer.onlineorder.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HealthControllerTests {

    @Test

    void health_returnsUpStatus() {
        HealthController controller = new HealthController();

        assertEquals("UP", controller.health().get("status"));
    }
}
