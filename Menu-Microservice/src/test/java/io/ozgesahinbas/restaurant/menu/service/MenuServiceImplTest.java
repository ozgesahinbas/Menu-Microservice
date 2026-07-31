package io.ozgesahinbas.restaurant.menu.service;

import io.ozgesahinbas.restaurant.menu.dto.MenuCreateRequest;
 import io.ozgesahinbas.restaurant.menu.dto.MenuUpdateRequest;
import io.ozgesahinbas.restaurant.menu.exception.MenuItemNotFoundException;
import io.ozgesahinbas.restaurant.menu.exception.MenuNotFoundException;
import io.ozgesahinbas.restaurant.menu.entity.Menu;
import io.ozgesahinbas.restaurant.menu.entity.MenuItem;
import io.ozgesahinbas.restaurant.menu.enums.MenuStatus;
import io.ozgesahinbas.restaurant.menu.enums.MenuType;
import io.ozgesahinbas.restaurant.menu.repository.MenuRepository;
import io.ozgesahinbas.restaurant.menu.repository.MenuItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
class MenuServiceImplTest {

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private MenuItemRepository menuItemRepository;

    @InjectMocks
    private MenuServiceImpl menuService;

    @Test
    void shouldCreateMenuSuccessfully() {

        MenuCreateRequest request = MenuCreateRequest.builder()
                .restaurantId("restaurant-1")
                .name("Night Menu")
                .description("Night menu for the restaurant")
                .menuType(MenuType.NIGHT)
                .status(MenuStatus.ACTIVE)
                .build();

        menuService.createMenu(request);

        ArgumentCaptor<Menu> menuCaptor =
                ArgumentCaptor.forClass(Menu.class);

        verify(menuRepository).save(menuCaptor.capture());

        Menu savedMenu = menuCaptor.getValue();

        assertEquals("restaurant-1", savedMenu.getRestaurantId());
        assertEquals("Night Menu", savedMenu.getName());
        assertEquals("Night menu for the restaurant", savedMenu.getDescription());
        assertEquals(MenuType.NIGHT, savedMenu.getMenuType());
        assertEquals(MenuStatus.ACTIVE, savedMenu.getStatus());

        assertNotNull(savedMenu.getCreatedAt());
        assertNotNull(savedMenu.getUpdatedAt());
    }

    @Test
    void shouldGetAllMenusSuccessfully() {

        Menu menu1 = Menu.builder()
                .id("menu-1")
                .restaurantId("restaurant-1")
                .name("Night Menu")
                .menuType(MenuType.NIGHT)
                .status(MenuStatus.ACTIVE)
                .build();

        Menu menu2 = Menu.builder()
                .id("menu-2")
                .restaurantId("restaurant-1")
                .name("Dessert Menu")
                .menuType(MenuType.DESSERT)
                .status(MenuStatus.ACTIVE)
                .build();

        List<Menu> menus = List.of(menu1, menu2);
        when(menuRepository.findAll()).thenReturn(menus);
        List<Menu> result = menuService.getAllMenus();

        assertEquals(2, result.size());
        assertEquals("Night Menu", result.get(0).getName());
        assertEquals("Dessert Menu", result.get(1).getName());

        verify(menuRepository).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoMenusExist() {

        when(menuRepository.findAll()).thenReturn(List.of());
        List<Menu> result = menuService.getAllMenus();
        assertTrue(result.isEmpty());
        verify(menuRepository).findAll();
    }
    @Test
    void shouldGetMenuByIdSuccessfully() {
        Menu menu = Menu.builder()
                .id("menu-1")
                .restaurantId("restaurant-1")
                .name("Night Menu")
                .menuType(MenuType.NIGHT)
                .status(MenuStatus.ACTIVE)
                .build();

        when(menuRepository.findById("menu-1"))
                .thenReturn(Optional.of(menu));

        Menu result = menuService.getMenuById("menu-1");

        assertEquals("menu-1", result.getId());
        assertEquals("Night Menu", result.getName());

        verify(menuRepository).findById("menu-1");
    }
    @Test
    void shouldThrowExceptionWhenMenuNotFoundById(){
        when(menuRepository.findById("menu-999"))
                .thenReturn(Optional.empty());

        assertThrows(
                MenuNotFoundException.class,
                ()-> menuService.getMenuById("menu-999")
        );
        verify(menuRepository).findById("menu-999");
    }
     @Test
     void shouldUpdateMenuSuccessfully() {
         Menu existingMenu = Menu.builder()
               .id("menu-1")
               .restaurantId("restaurant-1")
               .name("Day Menu")
               .description("Old description")
               .menuType(MenuType.DAY)
               .status(MenuStatus.ACTIVE)
               .build();

       MenuUpdateRequest request = MenuUpdateRequest.builder()
               .name("Night Menu")
               .description("Updated description")
               .menuType(MenuType.NIGHT)
               .status(MenuStatus.INACTIVE)
               .build();
         when(menuRepository.findById("menu-1"))
                 .thenReturn(Optional.of(existingMenu));
         when(menuRepository.save(any(Menu.class)))
                 .thenAnswer(invocation
                         -> invocation.getArgument(0));
         Menu updatedMenu = menuService
                 .updateMenu("menu-1", request);
         assertEquals("Night Menu", updatedMenu.getName());
         assertEquals("Updated description", updatedMenu.getDescription());
         assertEquals(MenuType.NIGHT, updatedMenu.getMenuType());
         assertEquals(MenuStatus.INACTIVE, updatedMenu.getStatus());
         verify(menuRepository).findById("menu-1");
         verify(menuRepository).save(existingMenu);
     }

    @Test
    void shouldDeleteMenuSuccessfully() {
        Menu menu = Menu.builder()
                .id("menu-1")
                .restaurantId("restaurant-1")
                .name("Night Menu")
                .build();

        when(menuRepository.findById("menu-1"))
                .thenReturn(Optional.of(menu));

        menuService.deleteMenu("menu-1");

        verify(menuRepository).delete(menu);
        }

    @Test
    void shouldThrowExceptionWhenMenuNotFoundForDelete() {
        when(menuRepository.findById("menu-999"))
                .thenReturn(Optional.empty());

        assertThrows(MenuNotFoundException.class,
                () -> menuService.deleteMenu("menu-999"));

        verify(menuRepository, never()).delete(any(Menu.class));
    }
    @Test
    void shouldGetMenuItemsSuccessfully() {

        List<MenuItem> items = List.of(
                MenuItem.builder()
                        .name("Pizza")
                        .description("Pepperoni Pizza")
                        .price(BigDecimal.valueOf(250))
                        .photoUrls(List.of("https://cdn.example.com/image-1.jpg"))
                        .videoUrls(List.of("https://cdn.example.com/video-1.mp4"))
                        .build(),

                MenuItem.builder()
                        .name("Burger")
                        .description("Cheeseburger")
                        .price(BigDecimal.valueOf(180))
                        .photoUrls(List.of("https://cdn.example.com/image-2.jpg"))
                        .videoUrls(List.of("https://cdn.example.com/video-2.mp4"))
                        .build()
        );

        Menu menu = Menu.builder()
                .id("menu-1")
                .restaurantId("restaurant-1")
                .build();

        when(menuRepository.findById("menu-1"))
                .thenReturn(Optional.of(menu));

        when(menuItemRepository.findByMenuId("menu-1"))
                .thenReturn(items);

        List<MenuItem> result = menuService.getMenuItems("menu-1");

        assertEquals(2, result.size());
        assertEquals("Pizza", result.get(0).getName());

        verify(menuRepository).findById("menu-1");
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
                .description("Pepperoni Pizza")
                .price(BigDecimal.valueOf(250))
                .photoUrls(List.of("https://cdn.example.com/image-1.jpg"))
                .videoUrls(List.of("https://cdn.example.com/video-1.mp4"))
                .build();

        when(menuRepository.findById("menu-1"))
                .thenReturn(Optional.of(menu));

        when(menuItemRepository.findById("item-1"))
                .thenReturn(Optional.of(menuItem));

        MenuItem result = menuService.getMenuItemById("menu-1", "item-1");

        assertEquals("Pizza", result.getName());

        verify(menuRepository).findById("menu-1");
        verify(menuItemRepository).findById("item-1");
    }
    @Test
    void shouldThrowExceptionWhenMenuItemNotFoundById() {

        Menu menu = Menu.builder()
                .id("menu-1")
                .build();

        when(menuRepository.findById("menu-1"))
                .thenReturn(Optional.of(menu));

        when(menuItemRepository.findById("item-999"))
                .thenReturn(Optional.empty());

        assertThrows(
                MenuItemNotFoundException.class,
                () -> menuService.getMenuItemById("menu-1", "item-999")
        );

        verify(menuRepository).findById("menu-1");
        verify(menuItemRepository).findById("item-999");
    }
    @Test
    void shouldGetMenusByRestaurantIdSuccessfully() {

        Menu menu1 = Menu.builder()
                .id("menu-1")
                .restaurantId("restaurant-1")
                .name("Day Menu")
                .build();

        Menu menu2 = Menu.builder()
                .id("menu-2")
                .restaurantId("restaurant-1")
                .name("Night Menu")
                .build();

        when(menuRepository.findByRestaurantId("restaurant-1"))
                .thenReturn(List.of(menu1, menu2));

        List<Menu> result = menuService.getMenusByRestaurantId("restaurant-1");

        assertEquals(2, result.size());
        assertEquals("Day Menu", result.get(0).getName());
        assertEquals("Night Menu", result.get(1).getName());

        verify(menuRepository).findByRestaurantId("restaurant-1");
    }

}