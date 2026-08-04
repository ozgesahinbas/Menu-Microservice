package io.ozgesahinbas.restaurant.menu.dto;

import io.ozgesahinbas.restaurant.menu.entity.Menu;
import io.ozgesahinbas.restaurant.menu.enums.MenuStatus;
import io.ozgesahinbas.restaurant.menu.enums.MenuType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "Menu name cannot be blank")
    @Size(max = 100, message = "Menu name cannot exceed 100 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotNull(message = "Menu type cannot be null")
    private MenuType menuType;

    @NotNull(message = "Status cannot be null")
    private MenuStatus status;

    public void updateEntity(Menu menu) {
        menu.setName(name);
        menu.setDescription(description);
        menu.setMenuType(menuType);
        menu.setStatus(status);
        menu.setUpdatedAt(LocalDateTime.now());
    }
}
