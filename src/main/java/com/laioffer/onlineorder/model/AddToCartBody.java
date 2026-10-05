package com.laioffer.onlineorder.model;

import com.fasterxml.jackson.annotation.JsonProperty;

// {
//   "menu_id": 4
// }
public record AddToCartBody(
        @JsonProperty("menu_id") Long menuId,

        Integer quantity
) {
}
