package io.ozgesahinbas.restaurant.menu.dto;

import io.ozgesahinbas.restaurant.menu.entity.MenuItem;
import io.ozgesahinbas.restaurant.menu.enums.MenuItemStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemCreateRequest {

    @NotBlank(message = "Menu item name cannot be blank")
    @Size(max = 100, message = "Menu item name cannot exceed 100 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    private String category;

    @NotNull(message = "Price cannot be null")
    @Positive(message = "Price must be greater than zero")
    private BigDecimal price;

    @Size(min = 3, max = 3, message = "Currency must be a 3 letter ISO code")
    private String currency;

    private List<String> photoUrls;
    private List<String> videoUrls;
    private List<String> allergens;
    private List<String> ingredients;

    private MenuItemStatus status;

    public MenuItem toEntity(String menuId, String restaurantId) {
        LocalDateTime now = LocalDateTime.now();

        return MenuItem.builder()
                .id(MenuItem.newId())
                .menuId(menuId)
                .restaurantId(restaurantId)
                .name(name)
                .description(description)
                .category(category)
                .price(price)
                .currency(currency)
                .photoUrls(Objects.requireNonNullElse(photoUrls, List.of()))
                .videoUrls(Objects.requireNonNullElse(videoUrls, List.of()))
                .allergens(Objects.requireNonNullElse(allergens, List.of()))
                .ingredients(Objects.requireNonNullElse(ingredients, List.of()))
                .status(Objects.requireNonNullElse(status, MenuItemStatus.ACTIVE))
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}
