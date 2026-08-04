package io.ozgesahinbas.restaurant.menu.service;

import io.ozgesahinbas.restaurant.menu.dto.MenuCreateRequest;
import io.ozgesahinbas.restaurant.menu.dto.MenuUpdateRequest;
import io.ozgesahinbas.restaurant.menu.entity.Menu;
import io.ozgesahinbas.restaurant.menu.entity.MenuItem;
import io.ozgesahinbas.restaurant.menu.enums.MenuStatus;
import io.ozgesahinbas.restaurant.menu.enums.MenuType;
import io.ozgesahinbas.restaurant.menu.exception.MenuNotFoundException;
import io.ozgesahinbas.restaurant.menu.repository.MenuItemRepository;
import io.ozgesahinbas.restaurant.menu.repository.MenuRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuServiceImplTest {

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private MenuItemRepository menuItemRepository;

    @InjectMocks
    private MenuServiceImpl menuService;

    @Test
    void shouldCreateMenuWithGeneratedIdAndTimestamps() {
        MenuCreateRequest request = MenuCreateRequest.builder()
                .restaurantId("restaurant-1")
                .name("Night Menu")
                .description("Served after 20:00")
                .menuType(MenuType.NIGHT)
                .status(MenuStatus.ACTIVE)
                .build();

        when(menuRepository.save(any(Menu.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Menu created = menuService.createMenu(request);

        ArgumentCaptor<Menu> captor = ArgumentCaptor.forClass(Menu.class);
        verify(menuRepository).save(captor.capture());

        Menu persisted = captor.getValue();
        assertThat(persisted.getId()).startsWith(Menu.ID_PREFIX);
        assertThat(persisted.getRestaurantId()).isEqualTo("restaurant-1");
        assertThat(persisted.getName()).isEqualTo("Night Menu");
        assertThat(persisted.getMenuType()).isEqualTo(MenuType.NIGHT);
        assertThat(persisted.getStatus()).isEqualTo(MenuStatus.ACTIVE);
        assertThat(persisted.getCreatedAt()).isNotNull();
        assertThat(persisted.getUpdatedAt()).isNotNull();
        assertThat(created).isSameAs(persisted);
    }

    @Test
    void shouldReturnAllMenus() {
        List<Menu> menus = List.of(menu("menu::1"), menu("menu::2"));
        when(menuRepository.findAll()).thenReturn(menus);

        assertThat(menuService.getAllMenus()).isEqualTo(menus);
    }

    @Test
    void shouldReturnMenuById() {
        Menu menu = menu("menu::1");
        when(menuRepository.findById("menu::1")).thenReturn(Optional.of(menu));

        assertThat(menuService.getMenuById("menu::1")).isSameAs(menu);
    }

    @Test
    void shouldThrowWhenMenuByIdDoesNotExist() {
        when(menuRepository.findById("menu::404")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> menuService.getMenuById("menu::404"))
                .isInstanceOf(MenuNotFoundException.class)
                .hasMessage("Menu not found with id: menu::404");
    }

    @Test
    void shouldReturnMenusOfRestaurant() {
        List<Menu> menus = List.of(menu("menu::1"));
        when(menuRepository.findByRestaurantId("restaurant-1")).thenReturn(menus);

        assertThat(menuService.getMenusByRestaurantId("restaurant-1")).isEqualTo(menus);
    }

    @Test
    void shouldUpdateMenuFieldsAndTouchUpdatedAt() {
        Menu menu = menu("menu::1");
        LocalDateTime originalUpdatedAt = menu.getUpdatedAt();

        MenuUpdateRequest request = MenuUpdateRequest.builder()
                .name("Updated Menu")
                .description("Updated description")
                .menuType(MenuType.DESSERT)
                .status(MenuStatus.INACTIVE)
                .build();

        when(menuRepository.findById("menu::1")).thenReturn(Optional.of(menu));
        when(menuRepository.save(menu)).thenReturn(menu);

        Menu updated = menuService.updateMenu("menu::1", request);

        assertThat(updated.getName()).isEqualTo("Updated Menu");
        assertThat(updated.getDescription()).isEqualTo("Updated description");
        assertThat(updated.getMenuType()).isEqualTo(MenuType.DESSERT);
        assertThat(updated.getStatus()).isEqualTo(MenuStatus.INACTIVE);
        assertThat(updated.getUpdatedAt()).isAfterOrEqualTo(originalUpdatedAt);
        verify(menuRepository).save(menu);
    }

    @Test
    void shouldThrowWhenUpdatingMissingMenu() {
        MenuUpdateRequest request = MenuUpdateRequest.builder()
                .name("Updated Menu")
                .menuType(MenuType.DAY)
                .status(MenuStatus.ACTIVE)
                .build();

        when(menuRepository.findById("menu::404")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> menuService.updateMenu("menu::404", request))
                .isInstanceOf(MenuNotFoundException.class);

        verify(menuRepository, never()).save(any(Menu.class));
    }

    @Test
    void shouldDeleteMenuTogetherWithItsItems() {
        Menu menu = menu("menu::1");
        List<MenuItem> items = List.of(
                MenuItem.builder().id("menu-item::1").menuId("menu::1").build());

        when(menuRepository.findById("menu::1")).thenReturn(Optional.of(menu));
        when(menuItemRepository.findByMenuId("menu::1")).thenReturn(items);

        menuService.deleteMenu("menu::1");

        verify(menuItemRepository).deleteAll(items);
        verify(menuRepository).delete(menu);
    }

    @Test
    void shouldThrowWhenDeletingMissingMenu() {
        when(menuRepository.findById("menu::404")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> menuService.deleteMenu("menu::404"))
                .isInstanceOf(MenuNotFoundException.class);

        verify(menuItemRepository, never()).deleteAll(any());
        verify(menuRepository, never()).delete(any(Menu.class));
    }

    private Menu menu(String id) {
        LocalDateTime now = LocalDateTime.now();

        return Menu.builder()
                .id(id)
                .restaurantId("restaurant-1")
                .name("Night Menu")
                .description("Served after 20:00")
                .menuType(MenuType.NIGHT)
                .status(MenuStatus.ACTIVE)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}
