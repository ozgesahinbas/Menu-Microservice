package io.ozgesahinbas.restaurant.menu.dto;

import io.ozgesahinbas.restaurant.menu.entity.MenuItem;
import io.ozgesahinbas.restaurant.menu.enums.MenuItemStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MenuItemRequestMappingTest {

    @Test
    void shouldMapCreateRequestToEntityIncludingMedia() {
        MenuItemCreateRequest request = MenuItemCreateRequest.builder()
                .name("Tiramisu")
                .description("Classic dessert")
                .category("Dessert")
                .price(BigDecimal.valueOf(120))
                .currency("TRY")
                .photoUrls(List.of("https://cdn.example.com/image-1.jpg"))
                .videoUrls(List.of("https://cdn.example.com/video-1.mp4"))
                .allergens(List.of("lactose", "egg"))
                .ingredients(List.of("mascarpone", "coffee"))
                .status(MenuItemStatus.INACTIVE)
                .build();

        MenuItem item = request.toEntity("menu::1", "restaurant-1");

        assertThat(item.getId()).startsWith(MenuItem.ID_PREFIX);
        assertThat(item.getMenuId()).isEqualTo("menu::1");
        assertThat(item.getRestaurantId()).isEqualTo("restaurant-1");
        assertThat(item.getName()).isEqualTo("Tiramisu");
        assertThat(item.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(120));
        assertThat(item.getCurrency()).isEqualTo("TRY");
        assertThat(item.getPhotoUrls()).containsExactly("https://cdn.example.com/image-1.jpg");
        assertThat(item.getVideoUrls()).containsExactly("https://cdn.example.com/video-1.mp4");
        assertThat(item.getAllergens()).containsExactly("lactose", "egg");
        assertThat(item.getIngredients()).containsExactly("mascarpone", "coffee");
        assertThat(item.getStatus()).isEqualTo(MenuItemStatus.INACTIVE);
        assertThat(item.getCreatedAt()).isEqualTo(item.getUpdatedAt());
    }

    @Test
    void shouldDefaultMediaListsAndStatusWhenOmitted() {
        MenuItemCreateRequest request = MenuItemCreateRequest.builder()
                .name("Espresso")
                .price(BigDecimal.valueOf(60))
                .build();

        MenuItem item = request.toEntity("menu::1", "restaurant-1");

        assertThat(item.getPhotoUrls()).isEmpty();
        assertThat(item.getVideoUrls()).isEmpty();
        assertThat(item.getAllergens()).isEmpty();
        assertThat(item.getIngredients()).isEmpty();
        assertThat(item.getStatus()).isEqualTo(MenuItemStatus.ACTIVE);
    }

    @Test
    void shouldGenerateUniqueIdsForEachItem() {
        assertThat(MenuItem.newId()).isNotEqualTo(MenuItem.newId());
    }

    @Test
    void shouldApplyUpdateRequestOntoExistingEntity() {
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        MenuItem item = MenuItem.builder()
                .id("menu-item::1")
                .menuId("menu::1")
                .restaurantId("restaurant-1")
                .name("Old Name")
                .price(BigDecimal.valueOf(100))
                .photoUrls(List.of("https://cdn.example.com/old.jpg"))
                .status(MenuItemStatus.ACTIVE)
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .build();

        MenuItemUpdateRequest request = MenuItemUpdateRequest.builder()
                .name("New Name")
                .description("New description")
                .category("Dessert")
                .price(BigDecimal.valueOf(150))
                .currency("EUR")
                .photoUrls(List.of("https://cdn.example.com/new.jpg"))
                .videoUrls(List.of("https://cdn.example.com/new.mp4"))
                .allergens(List.of("nuts"))
                .ingredients(List.of("hazelnut"))
                .status(MenuItemStatus.INACTIVE)
                .build();

        request.updateEntity(item);

        assertThat(item.getId()).isEqualTo("menu-item::1");
        assertThat(item.getMenuId()).isEqualTo("menu::1");
        assertThat(item.getName()).isEqualTo("New Name");
        assertThat(item.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(150));
        assertThat(item.getCurrency()).isEqualTo("EUR");
        assertThat(item.getPhotoUrls()).containsExactly("https://cdn.example.com/new.jpg");
        assertThat(item.getVideoUrls()).containsExactly("https://cdn.example.com/new.mp4");
        assertThat(item.getAllergens()).containsExactly("nuts");
        assertThat(item.getIngredients()).containsExactly("hazelnut");
        assertThat(item.getStatus()).isEqualTo(MenuItemStatus.INACTIVE);
        assertThat(item.getCreatedAt()).isEqualTo(createdAt);
        assertThat(item.getUpdatedAt()).isAfter(createdAt);
    }

    @Test
    void shouldClearMediaListsWhenUpdateOmitsThem() {
        MenuItem item = MenuItem.builder()
                .id("menu-item::1")
                .menuId("menu::1")
                .photoUrls(List.of("https://cdn.example.com/old.jpg"))
                .videoUrls(List.of("https://cdn.example.com/old.mp4"))
                .allergens(List.of("gluten"))
                .ingredients(List.of("flour"))
                .status(MenuItemStatus.ACTIVE)
                .build();

        MenuItemUpdateRequest.builder()
                .name("New Name")
                .price(BigDecimal.valueOf(150))
                .status(MenuItemStatus.ACTIVE)
                .build()
                .updateEntity(item);

        assertThat(item.getPhotoUrls()).isEmpty();
        assertThat(item.getVideoUrls()).isEmpty();
        assertThat(item.getAllergens()).isEmpty();
        assertThat(item.getIngredients()).isEmpty();
    }
}
