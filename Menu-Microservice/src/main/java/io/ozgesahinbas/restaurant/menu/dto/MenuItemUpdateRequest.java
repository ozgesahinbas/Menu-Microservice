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
public class MenuItemUpdateRequest {

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

    /**
     * Replaces every client-owned field, so anything left out of the request is
     * reset rather than kept. The item's identity and creation time are owned by
     * the server and stay untouched.
     */
    public void updateEntity(MenuItem menuItem) {
        menuItem.setName(name);
        menuItem.setDescription(description);
        menuItem.setCategory(category);
        menuItem.setPrice(price);
        menuItem.setCurrency(currency);
        menuItem.setPhotoUrls(Objects.requireNonNullElse(photoUrls, List.of()));
        menuItem.setVideoUrls(Objects.requireNonNullElse(videoUrls, List.of()));
        menuItem.setAllergens(Objects.requireNonNullElse(allergens, List.of()));
        menuItem.setIngredients(Objects.requireNonNullElse(ingredients, List.of()));
        menuItem.setStatus(Objects.requireNonNullElse(status, MenuItemStatus.ACTIVE));
        menuItem.setUpdatedAt(LocalDateTime.now());
    }
}
