package com.example.menumicroservice.dto;

import com.example.menumicroservice.model.MenuStatus;
import com.example.menumicroservice.model.MenuType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MenuCreateRequest {

    private String restaurantId;
    private String name;
    private String description;
    private MenuType menuType;
    private MenuStatus status;
}