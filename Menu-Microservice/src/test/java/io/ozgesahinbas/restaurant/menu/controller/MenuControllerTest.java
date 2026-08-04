package io.ozgesahinbas.restaurant.menu.controller;

import io.ozgesahinbas.restaurant.menu.dto.MenuCreateRequest;
import io.ozgesahinbas.restaurant.menu.dto.MenuUpdateRequest;
import io.ozgesahinbas.restaurant.menu.entity.Menu;
import io.ozgesahinbas.restaurant.menu.enums.MenuStatus;
import io.ozgesahinbas.restaurant.menu.enums.MenuType;
import io.ozgesahinbas.restaurant.menu.exception.MenuNotFoundException;
import io.ozgesahinbas.restaurant.menu.service.MenuService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MenuControllerTest {

    private MockMvc mockMvc;
    private MenuService menuService;

    @BeforeEach
    void setUp() {
        menuService = org.mockito.Mockito.mock(MenuService.class);

        MenuController menuController = new MenuController(menuService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(menuController)
                .build();
    }

    @Test
    void shouldCreateMenuSuccessfully() throws Exception {
        Menu created = Menu.builder()
                .id("menu-1")
                .restaurantId("restaurant-1")
                .name("Night Menu")
                .menuType(MenuType.NIGHT)
                .status(MenuStatus.ACTIVE)
                .build();

        when(menuService.createMenu(any(MenuCreateRequest.class)))
                .thenReturn(created);

        String requestBody = """
                {
                    "restaurantId": "restaurant-1",
                    "name": "Night Menu",
                    "description": "Night menu for the restaurant",
                    "menuType": "NIGHT",
                    "status": "ACTIVE"
                }
                """;

        mockMvc.perform(post("/menus")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("menu-1"))
                .andExpect(jsonPath("$.name").value("Night Menu"));

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
        mockMvc.perform(post("/menus")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(menuService, never()).createMenu(any(MenuCreateRequest.class));
    }

    @Test
    void shouldGetAllMenusSuccessfully() throws Exception {
        when(menuService.getAllMenus()).thenReturn(List.of());

        mockMvc.perform(get("/menus"))
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

        mockMvc.perform(get("/menus/menu-1"))
                .andExpect(status().isOk());

        verify(menuService).getMenuById("menu-1");
    }

    @Test
    void shouldReturnNotFoundWhenMenuDoesNotExist() throws Exception {
        when(menuService.getMenuById("menu-999"))
                .thenThrow(new MenuNotFoundException("menu-999"));

        mockMvc.perform(get("/menus/menu-999"))
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

        mockMvc.perform(put("/menus/menu-1")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk());

        verify(menuService).updateMenu(eq("menu-1"), any(MenuUpdateRequest.class));
    }

    @Test
    void shouldDeleteMenuSuccessfully() throws Exception {
        mockMvc.perform(delete("/menus/menu-1"))
                .andExpect(status().isNoContent());

        verify(menuService).deleteMenu("menu-1");
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistingMenu() throws Exception {
        doThrow(new MenuNotFoundException("menu-999"))
                .when(menuService)
                .deleteMenu("menu-999");

        mockMvc.perform(delete("/menus/menu-999"))
                .andExpect(status().isNotFound());

        verify(menuService).deleteMenu("menu-999");
    }

    @Test
    void shouldGetMenusByRestaurantIdSuccessfully() throws Exception {

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

        when(menuService.getMenusByRestaurantId("restaurant-1"))
                .thenReturn(List.of(menu1, menu2));

        mockMvc.perform(get("/menus").param("restaurantId", "restaurant-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value("menu-1"))
                .andExpect(jsonPath("$[0].restaurantId").value("restaurant-1"))
                .andExpect(jsonPath("$[0].name").value("Day Menu"))
                .andExpect(jsonPath("$[1].name").value("Night Menu"));

        verify(menuService).getMenusByRestaurantId("restaurant-1");
        verify(menuService, never()).getAllMenus();
    }

}
