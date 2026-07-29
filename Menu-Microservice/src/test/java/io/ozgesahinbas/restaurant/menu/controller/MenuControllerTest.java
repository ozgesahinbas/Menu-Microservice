package io.ozgesahinbas.restaurant.menu.controller;

import io.ozgesahinbas.restaurant.menu.dto.MenuCreateRequest;
import io.ozgesahinbas.restaurant.menu.dto.MenuUpdateRequest;
import io.ozgesahinbas.restaurant.menu.exception.MenuNotFoundException;
import io.ozgesahinbas.restaurant.menu.model.Menu;
import io.ozgesahinbas.restaurant.menu.model.MenuItem;
import io.ozgesahinbas.restaurant.menu.model.MenuStatus;
import io.ozgesahinbas.restaurant.menu.model.MenuType;
import io.ozgesahinbas.restaurant.menu.service.MenuServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MenuControllerTest {

    private MockMvc mockMvc;
    private MenuServiceImpl menuService;

    @BeforeEach
    void setUp() {
        menuService = org.mockito.Mockito.mock(MenuServiceImpl.class);

        MenuController menuController = new MenuController(menuService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(menuController)
                .build();
    }

    @Test
    void shouldCreateMenuSuccessfully() throws Exception {
        String requestBody = """
                {
                    "restaurantId": "restaurant-1",
                    "name": "Night Menu",
                    "description": "Night menu for the restaurant",
                    "menuType": "NIGHT",
                    "status": "ACTIVE"
                }
                """;

        mockMvc.perform(post("/menu")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isCreated());
        verify(menuService).createMenu(any(MenuCreateRequest.class));
    }
    @Test
    void shouldReturnBadRequestWhenMenuNameIsBlank() throws Exception{
        String requestBody = """
            {
                "restaurantId": "restaurant-1",
                "name": "",
                "description": "Night menu for the restaurant",
                "menuType": "NIGHT",
                "status": "ACTIVE"
            }
            """;
        mockMvc.perform(post("/menu")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }
    @Test
    void shouldGetAllMenusSuccessfully() throws Exception {
        when(menuService.getAllMenus()).thenReturn(List.of());

        mockMvc.perform(get("/menu"))
                .andExpect(status().isOk());

        verify(menuService).getAllMenus();
    }

    @Test
    void shouldGetMenuByIdSuccessfully() throws Exception {
        Menu menu = Menu.builder()
                .id("menu-1")
                .restaurantId("restaurant-1")
                .name("Night Menu")
                .menuType(MenuType.NIGHT)
                .status(MenuStatus.ACTIVE)
                .build();

        when(menuService.getMenuById("menu-1"))
                .thenReturn(menu);

        mockMvc.perform(get("/menu/menu-1"))
                .andExpect(status().isOk());

        verify(menuService).getMenuById("menu-1");
    }
    @Test
    void shouldReturnNotFoundWhenMenuDoesNotExist() throws Exception {
        when(menuService.getMenuById("menu-999"))
                .thenThrow(new MenuNotFoundException("menu-999"));

        mockMvc.perform(get("/menu/menu-999"))
                .andExpect(status().isNotFound());

        verify(menuService).getMenuById("menu-999");
    }
    @Test
    void shouldUpdateMenuSuccessfully() throws Exception {
        Menu menu = Menu.builder()
                .id("menu-1")
                .restaurantId("restaurant-1")
                .name("Night Menu")
                .description("Updated description")
                .menuType(MenuType.NIGHT)
                .status(MenuStatus.INACTIVE)
                .build();

        String requestBody = """
            {
                "name": "Night Menu",
                "description": "Updated description",
                "menuType": "NIGHT",
                "status": "INACTIVE"
            }
            """;

        when(menuService.updateMenu(eq("menu-1"), any(MenuUpdateRequest.class)))
                .thenReturn(menu);

        mockMvc.perform(put("/menu/menu-1")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk());

        verify(menuService).updateMenu(eq("menu-1"), any(MenuUpdateRequest.class));
    }
    @Test
    void shouldDeleteMenuSuccessfully() throws Exception {
        mockMvc.perform(delete("/menu/menu-1"))
                .andExpect(status().isNoContent());

        verify(menuService).deleteMenu("menu-1");
    }
    @Test
    void shouldReturnNotFoundWhenDeletingNonExistingMenu() throws Exception {
        doThrow(new MenuNotFoundException("menu-999"))
                .when(menuService)
                .deleteMenu("menu-999");

        mockMvc.perform(delete("/menu/menu-999"))
                .andExpect(status().isNotFound());

        verify(menuService).deleteMenu("menu-999");
    }
    @Test
    void shouldGetMenuItemsSuccessfully() throws Exception {

        List<MenuItem> items = List.of(
                MenuItem.builder()
                        .name("Pizza")
                        .description("Pepperoni Pizza")
                        .price(BigDecimal.valueOf(250))
                        .imageUrl("pizza.jpg")
                        .build(),
                MenuItem.builder()
                        .name("Burger")
                        .description("Cheeseburger")
                        .price(BigDecimal.valueOf(180))
                        .imageUrl("burger.jpg")
                        .build()
        );

        when(menuService.getMenuItems("menu-1"))
                .thenReturn(items);

        mockMvc.perform(get("/menu/menu-1/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Pizza"))
                .andExpect(jsonPath("$[1].name").value("Burger"));

        verify(menuService).getMenuItems("menu-1");
    }
}