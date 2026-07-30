package io.ozgesahinbas.restaurant.menu.dto;

import io.ozgesahinbas.restaurant.menu.entity.Menu;
import io.ozgesahinbas.restaurant.menu.enums.MenuStatus;
import io.ozgesahinbas.restaurant.menu.enums.MenuType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    public Menu toEntity() {
        LocalDateTime now = LocalDateTime.now();

        return Menu.builder()
                .restaurantId(restaurantId)
                .name(name)
                .description(description)
                .menuType(menuType)
                .status(status)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}