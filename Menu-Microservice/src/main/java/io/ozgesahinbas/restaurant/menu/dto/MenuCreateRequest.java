package io.ozgesahinbas.restaurant.menu.dto;

import io.ozgesahinbas.restaurant.menu.model.Menu;
import io.ozgesahinbas.restaurant.menu.model.MenuItem;
import io.ozgesahinbas.restaurant.menu.model.MenuStatus;
import io.ozgesahinbas.restaurant.menu.model.MenuType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
    private List<MenuItem> items;
    private MenuStatus status;

    public Menu toEntity(){
        LocalDateTime now = LocalDateTime.now();

        return Menu.builder()
                .restaurantId(restaurantId)
                .name(name)
                .description(description)
                .menuType(menuType)
                .status(status)
                .items(items)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}
