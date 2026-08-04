package io.ozgesahinbas.restaurant.menu.dto;

import io.ozgesahinbas.restaurant.menu.entity.Menu;
import io.ozgesahinbas.restaurant.menu.enums.MenuStatus;
import io.ozgesahinbas.restaurant.menu.enums.MenuType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class MenuRequestMappingTest {

    @Test
    void shouldMapCreateRequestToEntity() {
        MenuCreateRequest request = MenuCreateRequest.builder()
                .restaurantId("restaurant-1")
                .name("Wine Menu")
                .description("Selected wines")
                .menuType(MenuType.WINE)
                .status(MenuStatus.INACTIVE)
                .build();

        Menu menu = request.toEntity();

        assertThat(menu.getId()).startsWith(Menu.ID_PREFIX);
        assertThat(menu.getRestaurantId()).isEqualTo("restaurant-1");
        assertThat(menu.getName()).isEqualTo("Wine Menu");
        assertThat(menu.getDescription()).isEqualTo("Selected wines");
        assertThat(menu.getMenuType()).isEqualTo(MenuType.WINE);
        assertThat(menu.getStatus()).isEqualTo(MenuStatus.INACTIVE);
        assertThat(menu.getCreatedAt()).isEqualTo(menu.getUpdatedAt());
    }

    @Test
    void shouldDefaultStatusToActiveWhenOmitted() {
        MenuCreateRequest request = MenuCreateRequest.builder()
                .restaurantId("restaurant-1")
                .name("Day Menu")
                .menuType(MenuType.DAY)
                .build();

        assertThat(request.toEntity().getStatus()).isEqualTo(MenuStatus.ACTIVE);
    }

    @Test
    void shouldGenerateUniqueIdsForEachMenu() {
        assertThat(Menu.newId()).isNotEqualTo(Menu.newId());
    }

    @Test
    void shouldApplyUpdateRequestOntoExistingEntity() {
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        Menu menu = Menu.builder()
                .id("menu::1")
                .restaurantId("restaurant-1")
                .name("Old Name")
                .description("Old description")
                .menuType(MenuType.FOOD)
                .status(MenuStatus.ACTIVE)
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .build();

        MenuUpdateRequest request = MenuUpdateRequest.builder()
                .name("New Name")
                .description("New description")
                .menuType(MenuType.BEVERAGE)
                .status(MenuStatus.INACTIVE)
                .build();

        request.updateEntity(menu);

        assertThat(menu.getId()).isEqualTo("menu::1");
        assertThat(menu.getRestaurantId()).isEqualTo("restaurant-1");
        assertThat(menu.getName()).isEqualTo("New Name");
        assertThat(menu.getDescription()).isEqualTo("New description");
        assertThat(menu.getMenuType()).isEqualTo(MenuType.BEVERAGE);
        assertThat(menu.getStatus()).isEqualTo(MenuStatus.INACTIVE);
        assertThat(menu.getCreatedAt()).isEqualTo(createdAt);
        assertThat(menu.getUpdatedAt()).isAfter(createdAt);
    }
}
