package io.ozgesahinbas.restaurant.menu.dto;

import io.ozgesahinbas.restaurant.menu.model.MenuStatus;
import io.ozgesahinbas.restaurant.menu.model.MenuType;
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
