package io.ozgesahinbas.restaurant.menu.service;

import io.ozgesahinbas.restaurant.menu.dto.MenuItemCreateRequest;
import io.ozgesahinbas.restaurant.menu.dto.MenuItemUpdateRequest;
import io.ozgesahinbas.restaurant.menu.entity.Menu;
import io.ozgesahinbas.restaurant.menu.entity.MenuItem;
import io.ozgesahinbas.restaurant.menu.enums.MenuItemStatus;
import io.ozgesahinbas.restaurant.menu.enums.MenuStatus;
import io.ozgesahinbas.restaurant.menu.enums.MenuType;
import io.ozgesahinbas.restaurant.menu.exception.MenuItemNotFoundException;
import io.ozgesahinbas.restaurant.menu.exception.MenuNotFoundException;
import io.ozgesahinbas.restaurant.menu.repository.MenuItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
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
class MenuItemServiceImplTest {

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private MenuService menuService;

    @InjectMocks
    private MenuItemServiceImpl menuItemService;

    @Test
    void shouldCreateItemInheritingRestaurantFromItsMenu() {
        MenuItemCreateRequest request = MenuItemCreateRequest.builder()
                .name("Margherita")
                .description("Tomato, mozzarella, basil")
                .category("Pizza")
                .price(BigDecimal.valueOf(250))
                .currency("TRY")
                .photoUrls(List.of("https://cdn.example.com/image-1.jpg"))
                .videoUrls(List.of("https://cdn.example.com/video-1.mp4"))
                .allergens(List.of("gluten", "lactose"))
                .ingredients(List.of("tomato", "mozzarella"))
                .build();

        when(menuService.getMenuById("menu::1")).thenReturn(menu());
        when(menuItemRepository.save(any(MenuItem.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        MenuItem created = menuItemService.createMenuItem("menu::1", request);

        ArgumentCaptor<MenuItem> captor = ArgumentCaptor.forClass(MenuItem.class);
        verify(menuItemRepository).save(captor.capture());

        MenuItem persisted = captor.getValue();
        assertThat(persisted.getId()).startsWith(MenuItem.ID_PREFIX);
        assertThat(persisted.getMenuId()).isEqualTo("menu::1");
        assertThat(persisted.getRestaurantId()).isEqualTo("restaurant-1");
        assertThat(persisted.getPhotoUrls()).containsExactly("https://cdn.example.com/image-1.jpg");
        assertThat(persisted.getVideoUrls()).containsExactly("https://cdn.example.com/video-1.mp4");
        assertThat(persisted.getAllergens()).containsExactly("gluten", "lactose");
        assertThat(persisted.getIngredients()).containsExactly("tomato", "mozzarella");
        assertThat(persisted.getStatus()).isEqualTo(MenuItemStatus.ACTIVE);
        assertThat(created).isSameAs(persisted);
    }

    @Test
    void shouldNotCreateItemWhenMenuIsMissing() {
        MenuItemCreateRequest request = MenuItemCreateRequest.builder()
                .name("Margherita")
                .price(BigDecimal.valueOf(250))
                .build();

        when(menuService.getMenuById("menu::404"))
                .thenThrow(new MenuNotFoundException("menu::404"));

        assertThatThrownBy(() -> menuItemService.createMenuItem("menu::404", request))
                .isInstanceOf(MenuNotFoundException.class);

        verify(menuItemRepository, never()).save(any(MenuItem.class));
    }

    @Test
    void shouldListItemsOfMenu() {
        List<MenuItem> items = List.of(menuItem("menu-item::1", "menu::1"));

        when(menuService.getMenuById("menu::1")).thenReturn(menu());
        when(menuItemRepository.findByMenuId("menu::1")).thenReturn(items);

        assertThat(menuItemService.getMenuItems("menu::1")).isEqualTo(items);
    }

    @Test
    void shouldNotListItemsWhenMenuIsMissing() {
        when(menuService.getMenuById("menu::404"))
                .thenThrow(new MenuNotFoundException("menu::404"));

        assertThatThrownBy(() -> menuItemService.getMenuItems("menu::404"))
                .isInstanceOf(MenuNotFoundException.class);

        verify(menuItemRepository, never()).findByMenuId(any());
    }

    @Test
    void shouldReturnItemById() {
        MenuItem item = menuItem("menu-item::1", "menu::1");

        when(menuService.getMenuById("menu::1")).thenReturn(menu());
        when(menuItemRepository.findById("menu-item::1")).thenReturn(Optional.of(item));

        assertThat(menuItemService.getMenuItemById("menu::1", "menu-item::1")).isSameAs(item);
    }

    @Test
    void shouldThrowWhenItemDoesNotExist() {
        when(menuService.getMenuById("menu::1")).thenReturn(menu());
        when(menuItemRepository.findById("menu-item::404")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> menuItemService.getMenuItemById("menu::1", "menu-item::404"))
                .isInstanceOf(MenuItemNotFoundException.class)
                .hasMessage("Menu item not found with id: menu-item::404");
    }

    @Test
    void shouldThrowWhenItemBelongsToAnotherMenu() {
        MenuItem foreignItem = menuItem("menu-item::1", "menu::2");

        when(menuService.getMenuById("menu::1")).thenReturn(menu());
        when(menuItemRepository.findById("menu-item::1")).thenReturn(Optional.of(foreignItem));

        assertThatThrownBy(() -> menuItemService.getMenuItemById("menu::1", "menu-item::1"))
                .isInstanceOf(MenuItemNotFoundException.class);
    }

    @Test
    void shouldUpdateItemFields() {
        MenuItem item = menuItem("menu-item::1", "menu::1");
        LocalDateTime originalUpdatedAt = item.getUpdatedAt();

        MenuItemUpdateRequest request = MenuItemUpdateRequest.builder()
                .name("Margherita XL")
                .description("Bigger version")
                .category("Pizza")
                .price(BigDecimal.valueOf(320))
                .currency("TRY")
                .photoUrls(List.of("https://cdn.example.com/image-2.jpg"))
                .videoUrls(List.of("https://cdn.example.com/video-2.mp4"))
                .allergens(List.of("gluten"))
                .ingredients(List.of("tomato"))
                .status(MenuItemStatus.INACTIVE)
                .build();

        when(menuService.getMenuById("menu::1")).thenReturn(menu());
        when(menuItemRepository.findById("menu-item::1")).thenReturn(Optional.of(item));
        when(menuItemRepository.save(item)).thenReturn(item);

        MenuItem updated = menuItemService.updateMenuItem("menu::1", "menu-item::1", request);

        assertThat(updated.getName()).isEqualTo("Margherita XL");
        assertThat(updated.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(320));
        assertThat(updated.getPhotoUrls()).containsExactly("https://cdn.example.com/image-2.jpg");
        assertThat(updated.getStatus()).isEqualTo(MenuItemStatus.INACTIVE);
        assertThat(updated.getUpdatedAt()).isAfterOrEqualTo(originalUpdatedAt);
        verify(menuItemRepository).save(item);
    }

    @Test
    void shouldNotUpdateMissingItem() {
        MenuItemUpdateRequest request = MenuItemUpdateRequest.builder()
                .name("Margherita XL")
                .price(BigDecimal.valueOf(320))
                .status(MenuItemStatus.ACTIVE)
                .build();

        when(menuService.getMenuById("menu::1")).thenReturn(menu());
        when(menuItemRepository.findById("menu-item::404")).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                menuItemService.updateMenuItem("menu::1", "menu-item::404", request))
                .isInstanceOf(MenuItemNotFoundException.class);

        verify(menuItemRepository, never()).save(any(MenuItem.class));
    }

    @Test
    void shouldDeleteItem() {
        MenuItem item = menuItem("menu-item::1", "menu::1");

        when(menuService.getMenuById("menu::1")).thenReturn(menu());
        when(menuItemRepository.findById("menu-item::1")).thenReturn(Optional.of(item));

        menuItemService.deleteMenuItem("menu::1", "menu-item::1");

        verify(menuItemRepository).delete(item);
    }

    @Test
    void shouldNotDeleteMissingItem() {
        when(menuService.getMenuById("menu::1")).thenReturn(menu());
        when(menuItemRepository.findById("menu-item::404")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> menuItemService.deleteMenuItem("menu::1", "menu-item::404"))
                .isInstanceOf(MenuItemNotFoundException.class);

        verify(menuItemRepository, never()).delete(any(MenuItem.class));
    }

    private Menu menu() {
        return Menu.builder()
                .id("menu::1")
                .restaurantId("restaurant-1")
                .name("Night Menu")
                .menuType(MenuType.NIGHT)
                .status(MenuStatus.ACTIVE)
                .build();
    }

    private MenuItem menuItem(String id, String menuId) {
        LocalDateTime now = LocalDateTime.now();

        return MenuItem.builder()
                .id(id)
                .menuId(menuId)
                .restaurantId("restaurant-1")
                .name("Margherita")
                .category("Pizza")
                .price(BigDecimal.valueOf(250))
                .currency("TRY")
                .photoUrls(List.of())
                .videoUrls(List.of())
                .allergens(List.of())
                .ingredients(List.of())
                .status(MenuItemStatus.ACTIVE)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}
