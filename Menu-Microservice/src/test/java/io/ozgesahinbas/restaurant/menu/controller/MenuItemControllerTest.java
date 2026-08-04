package io.ozgesahinbas.restaurant.menu.controller;

import io.ozgesahinbas.restaurant.menu.dto.MenuItemCreateRequest;
import io.ozgesahinbas.restaurant.menu.dto.MenuItemUpdateRequest;
import io.ozgesahinbas.restaurant.menu.entity.MenuItem;
import io.ozgesahinbas.restaurant.menu.enums.MenuItemStatus;
import io.ozgesahinbas.restaurant.menu.exception.MenuItemNotFoundException;
import io.ozgesahinbas.restaurant.menu.exception.MenuNotFoundException;
import io.ozgesahinbas.restaurant.menu.service.MenuItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MenuItemControllerTest {

    private MockMvc mockMvc;
    private MenuItemService menuItemService;

    @BeforeEach
    void setUp() {
        menuItemService = org.mockito.Mockito.mock(MenuItemService.class);

        MenuItemController menuItemController = new MenuItemController(menuItemService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(menuItemController)
                .build();
    }

    @Test
    void shouldCreateMenuItemSuccessfully() throws Exception {

        MenuItem menuItem = MenuItem.builder()
                .id("item-1")
                .menuId("menu-1")
                .restaurantId("restaurant-1")
                .name("Pizza")
                .description("Pepperoni Pizza")
                .price(BigDecimal.valueOf(250))
                .status(MenuItemStatus.ACTIVE)
                .build();

        when(menuItemService.createMenuItem(eq("menu-1"), any(MenuItemCreateRequest.class)))
                .thenReturn(menuItem);

        String requestBody = """
            {
              "name": "Pizza",
              "description": "Pepperoni Pizza",
              "price": 250,
              "currency": "TRY"
            }
            """;

        mockMvc.perform(post("/menus/menu-1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("item-1"))
                .andExpect(jsonPath("$.menuId").value("menu-1"))
                .andExpect(jsonPath("$.name").value("Pizza"));

        verify(menuItemService).createMenuItem(eq("menu-1"), any(MenuItemCreateRequest.class));
    }

    @Test
    void shouldReturnBadRequestWhenMenuItemNameIsBlank() throws Exception {

        String requestBody = """
            {
              "name": "",
              "description": "Pepperoni Pizza",
              "price": 250
            }
            """;

        mockMvc.perform(post("/menus/menu-1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(menuItemService, never())
                .createMenuItem(any(), any(MenuItemCreateRequest.class));
    }

    @Test
    void shouldReturnBadRequestWhenMenuItemPriceIsNotPositive() throws Exception {

        String requestBody = """
            {
              "name": "Pizza",
              "price": 0
            }
            """;

        mockMvc.perform(post("/menus/menu-1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(menuItemService, never())
                .createMenuItem(any(), any(MenuItemCreateRequest.class));
    }

    @Test
    void shouldReturnNotFoundWhenCreatingItemForMissingMenu() throws Exception {

        when(menuItemService.createMenuItem(eq("menu-999"), any(MenuItemCreateRequest.class)))
                .thenThrow(new MenuNotFoundException("menu-999"));

        String requestBody = """
            {
              "name": "Pizza",
              "price": 250
            }
            """;

        mockMvc.perform(post("/menus/menu-999/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetMenuItemsSuccessfully() throws Exception {

        List<MenuItem> items = List.of(
                MenuItem.builder()
                        .id("item-1")
                        .menuId("menu-1")
                        .name("Pizza")
                        .price(BigDecimal.valueOf(250))
                        .photoUrls(List.of("https://cdn.example.com/image-1.jpg"))
                        .build(),

                MenuItem.builder()
                        .id("item-2")
                        .menuId("menu-1")
                        .name("Burger")
                        .price(BigDecimal.valueOf(180))
                        .photoUrls(List.of("https://cdn.example.com/image-2.jpg"))
                        .build()
        );

        when(menuItemService.getMenuItems("menu-1")).thenReturn(items);

        mockMvc.perform(get("/menus/menu-1/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Pizza"))
                .andExpect(jsonPath("$[1].name").value("Burger"));

        verify(menuItemService).getMenuItems("menu-1");
    }

    @Test
    void shouldGetMenuItemByIdSuccessfully() throws Exception {

        MenuItem menuItem = MenuItem.builder()
                .id("item-1")
                .menuId("menu-1")
                .name("Pizza")
                .description("Pepperoni Pizza")
                .price(BigDecimal.valueOf(250))
                .build();

        when(menuItemService.getMenuItemById("menu-1", "item-1"))
                .thenReturn(menuItem);

        mockMvc.perform(get("/menus/menu-1/items/item-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Pizza"));

        verify(menuItemService).getMenuItemById("menu-1", "item-1");
    }

    @Test
    void shouldReturnNotFoundWhenMenuItemDoesNotExist() throws Exception {

        when(menuItemService.getMenuItemById("menu-1", "item-999"))
                .thenThrow(new MenuItemNotFoundException("item-999"));

        mockMvc.perform(get("/menus/menu-1/items/item-999"))
                .andExpect(status().isNotFound());

        verify(menuItemService).getMenuItemById("menu-1", "item-999");
    }

    @Test
    void shouldUpdateMenuItemSuccessfully() throws Exception {

        MenuItem updated = MenuItem.builder()
                .id("item-1")
                .menuId("menu-1")
                .name("Margherita Pizza")
                .description("Updated description")
                .price(BigDecimal.valueOf(300))
                .status(MenuItemStatus.INACTIVE)
                .build();

        when(menuItemService.updateMenuItem(
                eq("menu-1"), eq("item-1"), any(MenuItemUpdateRequest.class)))
                .thenReturn(updated);

        String requestBody = """
            {
              "name": "Margherita Pizza",
              "description": "Updated description",
              "price": 300,
              "status": "INACTIVE"
            }
            """;

        mockMvc.perform(put("/menus/menu-1/items/item-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Margherita Pizza"))
                .andExpect(jsonPath("$.status").value("INACTIVE"));

        verify(menuItemService).updateMenuItem(
                eq("menu-1"), eq("item-1"), any(MenuItemUpdateRequest.class));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingMissingMenuItem() throws Exception {

        when(menuItemService.updateMenuItem(
                eq("menu-1"), eq("item-999"), any(MenuItemUpdateRequest.class)))
                .thenThrow(new MenuItemNotFoundException("item-999"));

        String requestBody = """
            {
              "name": "Pizza",
              "price": 250
            }
            """;

        mockMvc.perform(put("/menus/menu-1/items/item-999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteMenuItemSuccessfully() throws Exception {

        mockMvc.perform(delete("/menus/menu-1/items/item-1"))
                .andExpect(status().isNoContent());

        verify(menuItemService).deleteMenuItem("menu-1", "item-1");
    }

    @Test
    void shouldReturnNotFoundWhenDeletingMissingMenuItem() throws Exception {

        doThrow(new MenuItemNotFoundException("item-999"))
                .when(menuItemService)
                .deleteMenuItem("menu-1", "item-999");

        mockMvc.perform(delete("/menus/menu-1/items/item-999"))
                .andExpect(status().isNotFound());

        verify(menuItemService).deleteMenuItem("menu-1", "item-999");
    }

}
