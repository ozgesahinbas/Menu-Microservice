package io.ozgesahinbas.restaurant.menu.service;

import io.ozgesahinbas.restaurant.menu.dto.MenuItemCreateRequest;
import io.ozgesahinbas.restaurant.menu.dto.MenuItemUpdateRequest;
import io.ozgesahinbas.restaurant.menu.entity.Menu;
import io.ozgesahinbas.restaurant.menu.entity.MenuItem;
import io.ozgesahinbas.restaurant.menu.enums.MenuItemStatus;
import io.ozgesahinbas.restaurant.menu.exception.MenuItemNotFoundException;
import io.ozgesahinbas.restaurant.menu.exception.MenuNotFoundException;
import io.ozgesahinbas.restaurant.menu.repository.MenuItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class MenuItemServiceImplTest {

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private MenuService menuService;

    @InjectMocks
    private MenuItemServiceImpl menuItemService;

    @Test
    void shouldCreateMenuItemSuccessfully() {

        Menu menu = Menu.builder()
                .id("menu-1")
                .restaurantId("restaurant-1")
                .build();

        MenuItemCreateRequest request = MenuItemCreateRequest.builder()
                .name("Pizza")
                .description("Pepperoni Pizza")
                .price(BigDecimal.valueOf(250))
                .category("Main")
                .currency("TRY")
                .build();

        when(menuService.getMenuById("menu-1")).thenReturn(menu);
        when(menuItemRepository.save(any(MenuItem.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        MenuItem result = menuItemService.createMenuItem("menu-1", request);

        assertNotNull(result.getId());
        assertTrue(result.getId().startsWith(MenuItem.ID_PREFIX));
        assertEquals("menu-1", result.getMenuId());
        assertEquals("restaurant-1", result.getRestaurantId());
        assertEquals("Pizza", result.getName());
        assertEquals(BigDecimal.valueOf(250), result.getPrice());
        assertEquals(MenuItemStatus.ACTIVE, result.getStatus());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());

        verify(menuService).getMenuById("menu-1");
        verify(menuItemRepository).save(any(MenuItem.class));
    }

    @Test
    void shouldThrowExceptionWhenCreatingItemForMissingMenu() {

        MenuItemCreateRequest request = MenuItemCreateRequest.builder()
                .name("Pizza")
                .price(BigDecimal.valueOf(250))
                .build();

        when(menuService.getMenuById("menu-999"))
                .thenThrow(new MenuNotFoundException("menu-999"));

        assertThrows(
                MenuNotFoundException.class,
                () -> menuItemService.createMenuItem("menu-999", request)
        );

        verify(menuItemRepository, never()).save(any(MenuItem.class));
    }

    @Test
    void shouldGetMenuItemsSuccessfully() {

        Menu menu = Menu.builder()
                .id("menu-1")
                .restaurantId("restaurant-1")
                .build();

        List<MenuItem> items = List.of(
                MenuItem.builder()
                        .name("Pizza")
                        .description("Pepperoni Pizza")
                        .price(BigDecimal.valueOf(250))
                        .build(),

                MenuItem.builder()
                        .name("Burger")
                        .description("Cheeseburger")
                        .price(BigDecimal.valueOf(180))
                        .build()
        );

        when(menuService.getMenuById("menu-1")).thenReturn(menu);
        when(menuItemRepository.findByMenuId("menu-1")).thenReturn(items);

        List<MenuItem> result = menuItemService.getMenuItems("menu-1");

        assertEquals(2, result.size());
        assertEquals("Pizza", result.get(0).getName());

        verify(menuService).getMenuById("menu-1");
        verify(menuItemRepository).findByMenuId("menu-1");
    }

    @Test
    void shouldGetMenuItemByIdSuccessfully() {

        Menu menu = Menu.builder()
                .id("menu-1")
                .restaurantId("restaurant-1")
                .build();

        MenuItem menuItem = MenuItem.builder()
                .id("item-1")
                .menuId("menu-1")
                .restaurantId("restaurant-1")
                .name("Pizza")
                .price(BigDecimal.valueOf(250))
                .build();

        when(menuService.getMenuById("menu-1")).thenReturn(menu);
        when(menuItemRepository.findById("item-1"))
                .thenReturn(Optional.of(menuItem));

        MenuItem result = menuItemService.getMenuItemById("menu-1", "item-1");

        assertEquals("Pizza", result.getName());

        verify(menuService).getMenuById("menu-1");
        verify(menuItemRepository).findById("item-1");
    }

    @Test
    void shouldThrowExceptionWhenMenuItemNotFoundById() {

        Menu menu = Menu.builder().id("menu-1").build();

        when(menuService.getMenuById("menu-1")).thenReturn(menu);
        when(menuItemRepository.findById("item-999"))
                .thenReturn(Optional.empty());

        assertThrows(
                MenuItemNotFoundException.class,
                () -> menuItemService.getMenuItemById("menu-1", "item-999")
        );

        verify(menuItemRepository).findById("item-999");
    }

    @Test
    void shouldThrowExceptionWhenMenuItemBelongsToAnotherMenu() {

        Menu menu = Menu.builder().id("menu-1").build();

        MenuItem menuItem = MenuItem.builder()
                .id("item-1")
                .menuId("menu-2")
                .name("Pizza")
                .build();

        when(menuService.getMenuById("menu-1")).thenReturn(menu);
        when(menuItemRepository.findById("item-1"))
                .thenReturn(Optional.of(menuItem));

        assertThrows(
                MenuItemNotFoundException.class,
                () -> menuItemService.getMenuItemById("menu-1", "item-1")
        );
    }

    @Test
    void shouldUpdateMenuItemSuccessfully() {

        Menu menu = Menu.builder().id("menu-1").build();

        MenuItem existingItem = MenuItem.builder()
                .id("item-1")
                .menuId("menu-1")
                .restaurantId("restaurant-1")
                .name("Pizza")
                .description("Old description")
                .price(BigDecimal.valueOf(250))
                .status(MenuItemStatus.ACTIVE)
                .build();

        MenuItemUpdateRequest request = MenuItemUpdateRequest.builder()
                .name("Margherita Pizza")
                .description("Updated description")
                .price(BigDecimal.valueOf(300))
                .currency("TRY")
                .status(MenuItemStatus.INACTIVE)
                .build();

        when(menuService.getMenuById("menu-1")).thenReturn(menu);
        when(menuItemRepository.findById("item-1"))
                .thenReturn(Optional.of(existingItem));
        when(menuItemRepository.save(any(MenuItem.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        MenuItem result = menuItemService.updateMenuItem("menu-1", "item-1", request);

        assertEquals("Margherita Pizza", result.getName());
        assertEquals("Updated description", result.getDescription());
        assertEquals(BigDecimal.valueOf(300), result.getPrice());
        assertEquals(MenuItemStatus.INACTIVE, result.getStatus());
        assertNotNull(result.getUpdatedAt());

        // Identity stays with the server, not the request body.
        assertEquals("item-1", result.getId());
        assertEquals("menu-1", result.getMenuId());
        assertEquals("restaurant-1", result.getRestaurantId());

        verify(menuItemRepository).save(existingItem);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingItemOfAnotherMenu() {

        Menu menu = Menu.builder().id("menu-1").build();

        MenuItem menuItem = MenuItem.builder()
                .id("item-1")
                .menuId("menu-2")
                .build();

        MenuItemUpdateRequest request = MenuItemUpdateRequest.builder()
                .name("Pizza")
                .price(BigDecimal.valueOf(250))
                .build();

        when(menuService.getMenuById("menu-1")).thenReturn(menu);
        when(menuItemRepository.findById("item-1"))
                .thenReturn(Optional.of(menuItem));

        assertThrows(
                MenuItemNotFoundException.class,
                () -> menuItemService.updateMenuItem("menu-1", "item-1", request)
        );

        verify(menuItemRepository, never()).save(any(MenuItem.class));
    }

    @Test
    void shouldDeleteMenuItemSuccessfully() {

        Menu menu = Menu.builder().id("menu-1").build();

        MenuItem menuItem = MenuItem.builder()
                .id("item-1")
                .menuId("menu-1")
                .build();

        when(menuService.getMenuById("menu-1")).thenReturn(menu);
        when(menuItemRepository.findById("item-1"))
                .thenReturn(Optional.of(menuItem));

        menuItemService.deleteMenuItem("menu-1", "item-1");

        verify(menuItemRepository).delete(menuItem);
    }

    @Test
    void shouldThrowExceptionWhenDeletingMissingMenuItem() {

        Menu menu = Menu.builder().id("menu-1").build();

        when(menuService.getMenuById("menu-1")).thenReturn(menu);
        when(menuItemRepository.findById("item-999"))
                .thenReturn(Optional.empty());

        assertThrows(
                MenuItemNotFoundException.class,
                () -> menuItemService.deleteMenuItem("menu-1", "item-999")
        );

        verify(menuItemRepository, never()).delete(any(MenuItem.class));
    }

}
