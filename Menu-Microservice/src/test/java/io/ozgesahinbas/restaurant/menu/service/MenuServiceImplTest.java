package io.ozgesahinbas.restaurant.menu.service;

import io.ozgesahinbas.restaurant.menu.dto.MenuCreateRequest;
import io.ozgesahinbas.restaurant.menu.dto.MenuUpdateRequest;
import io.ozgesahinbas.restaurant.menu.exception.MenuNotFoundException;
import io.ozgesahinbas.restaurant.menu.model.Menu;
import io.ozgesahinbas.restaurant.menu.model.MenuItem;
import io.ozgesahinbas.restaurant.menu.model.MenuStatus;
import io.ozgesahinbas.restaurant.menu.model.MenuType;
import io.ozgesahinbas.restaurant.menu.repository.MenuRepository;
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
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class MenuServiceImplTest {

    @Mock
    private MenuRepository menuRepository;

    @InjectMocks
    private MenuServiceImpl menuService;

    @Test
    void shouldCreateMenuSuccessfully() {

        MenuItem item = MenuItem.builder()
                .name("Cheeseburger")
                .description("Cheeseburger with fries")
                .price(BigDecimal.valueOf(250))
                .imageUrl("https://example.com/cheeseburger.jpg")
                .build();

        MenuCreateRequest request = MenuCreateRequest.builder()
                .restaurantId("restaurant-1")
                .name("Night Menu")
                .description("Night menu for the restaurant")
                .menuType(MenuType.NIGHT)
                .status(MenuStatus.ACTIVE)
                .items(List.of(item))
                .build();

        menuService.createMenu(request);

        ArgumentCaptor<Menu> menuCaptor =
                ArgumentCaptor.forClass(Menu.class);

        verify(menuRepository).save(menuCaptor.capture());
        Menu savedMenu = menuCaptor.getValue();

        assertEquals("restaurant-1", savedMenu.getRestaurantId());
        assertEquals("Night Menu", savedMenu.getName());
        assertEquals(
                "Night menu for the restaurant",
                savedMenu.getDescription()
        );
        assertEquals(MenuType.NIGHT, savedMenu.getMenuType());
        assertEquals(MenuStatus.ACTIVE, savedMenu.getStatus());

        assertEquals(1, savedMenu.getItems().size());
        assertEquals(
                "Cheeseburger",
                savedMenu.getItems().get(0).getName()
        );
        assertEquals(
                BigDecimal.valueOf(250),
                savedMenu.getItems().get(0).getPrice()
        );
        assertEquals(
                "https://example.com/cheeseburger.jpg",
                savedMenu.getItems().get(0).getImageUrl()
        );

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
                .thenAnswer(invocation -> invocation.getArgument(0));


        Menu updatedMenu = menuService.updateMenu("menu-1", request);

        assertEquals("Night Menu", updatedMenu.getName());
        assertEquals("Updated description", updatedMenu.getDescription());
        assertEquals(MenuType.NIGHT, updatedMenu.getMenuType());
        assertEquals(MenuStatus.INACTIVE, updatedMenu.getStatus());

        verify(menuRepository).findById("menu-1");
        verify(menuRepository).save(existingMenu);
    }
}