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
public class MenuUpdateRequest {

    @NotBlank(message = "Menu name can't be blank")
    private String name;
    private String description;
    private MenuType menuType;
    private MenuStatus status;

    public void updateEntity(Menu menu) {
        menu.setName(name);
        menu.setDescription(description);
        menu.setMenuType(menuType);
        menu.setStatus(status);
        menu.setUpdatedAt(LocalDateTime.now());
    }
}