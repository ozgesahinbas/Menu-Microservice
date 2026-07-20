package com.example.menumicroservice.dto;

import com.example.menumicroservice.model.MenuStatus;
import com.example.menumicroservice.model.MenuType;
import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "Menu name cannot be blank")
    private String name;
    private String description;
    private MenuType menuType;
    private MenuStatus status;

}
