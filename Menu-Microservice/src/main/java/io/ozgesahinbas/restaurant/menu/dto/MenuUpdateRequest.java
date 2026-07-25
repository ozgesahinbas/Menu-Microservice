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

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class MenuUpdateRequest {
    @NotBlank(message = "Menu name can't be blank")
    private String name;
    private String description;
    private MenuType menuType;
    private List<MenuItem> items;
    private MenuStatus status;

    public void updateEntity(Menu menu) {
        menu.setName(name);
        menu.setDescription(description);
        menu.setMenuType(menuType);
        menu.setItems(items);
        menu.setStatus(status);
        menu.setUpdatedAt(LocalDateTime.now());
    }

}
