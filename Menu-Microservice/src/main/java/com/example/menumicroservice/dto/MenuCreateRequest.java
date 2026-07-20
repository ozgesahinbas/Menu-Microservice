package com.example.menumicroservice.dto;

import com.example.menumicroservice.model.MenuStatus;
import com.example.menumicroservice.model.MenuType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuCreateRequest {

    private String restaurantId;
    private String name;
    private String description;
    private MenuType menuType;
    private MenuStatus status;
}
