package io.ozgesahinbas.restaurant.menu.controller;

import io.ozgesahinbas.restaurant.menu.dto.MenuItemCreateRequest;
import io.ozgesahinbas.restaurant.menu.dto.MenuItemUpdateRequest;
import io.ozgesahinbas.restaurant.menu.entity.MenuItem;
import io.ozgesahinbas.restaurant.menu.enums.MenuItemStatus;
import io.ozgesahinbas.restaurant.menu.exception.GlobalExceptionHandler;
import io.ozgesahinbas.restaurant.menu.exception.MenuItemNotFoundException;
import io.ozgesahinbas.restaurant.menu.exception.MenuNotFoundException;
import io.ozgesahinbas.restaurant.menu.service.MenuItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MenuItemControllerTest {

    @Mock
    private MenuItemService menuItemService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new MenuItemController(menuItemService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldCreateMenuItemWithPhotoAndVideoUrls() throws Exception {
        when(menuItemService.createMenuItem(eq("menu::1"), any(MenuItemCreateRequest.class)))
                .thenReturn(menuItem());

        mockMvc.perform(post("/menus/menu::1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Margherita",
                                  "description": "Tomato, mozzarella, basil",
                                  "category": "Pizza",
                                  "price": 250,
                                  "currency": "TRY",
                                  "photoUrls": ["https://cdn.example.com/image-1.jpg"],
                                  "videoUrls": ["https://cdn.example.com/video-1.mp4"],
                                  "allergens": ["gluten"],
                                  "ingredients": ["tomato", "mozzarella"]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("menu-item::1"))
                .andExpect(jsonPath("$.photoUrls[0]").value("https://cdn.example.com/image-1.jpg"))
                .andExpect(jsonPath("$.videoUrls[0]").value("https://cdn.example.com/video-1.mp4"));

        verify(menuItemService).createMenuItem(eq("menu::1"), any(MenuItemCreateRequest.class));
    }

    @Test
    void shouldRejectItemWithBlankNameAndMissingPrice() throws Exception {
        mockMvc.perform(post("/menus/menu::1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "",
                                  "category": "Pizza"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.name")
                        .value("Menu item name cannot be blank"))
                .andExpect(jsonPath("$.validationErrors.price").value("Price cannot be null"));
    }

    @Test
    void shouldReturnNotFoundWhenCreatingItemForMissingMenu() throws Exception {
        when(menuItemService.createMenuItem(eq("menu::404"), any(MenuItemCreateRequest.class)))
                .thenThrow(new MenuNotFoundException("menu::404"));

        mockMvc.perform(post("/menus/menu::404/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Margherita",
                                  "price": 250
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Menu not found with id: menu::404"));
    }

    @Test
    void shouldListMenuItems() throws Exception {
        when(menuItemService.getMenuItems("menu::1")).thenReturn(List.of(menuItem()));

        mockMvc.perform(get("/menus/menu::1/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Margherita"));
    }

    @Test
    void shouldReturnMenuItemById() throws Exception {
        when(menuItemService.getMenuItemById("menu::1", "menu-item::1")).thenReturn(menuItem());

        mockMvc.perform(get("/menus/menu::1/items/menu-item::1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Margherita"))
                .andExpect(jsonPath("$.allergens[0]").value("gluten"));
    }

    @Test
    void shouldReturnNotFoundForMissingMenuItem() throws Exception {
        when(menuItemService.getMenuItemById("menu::1", "menu-item::404"))
                .thenThrow(new MenuItemNotFoundException("menu-item::404"));

        mockMvc.perform(get("/menus/menu::1/items/menu-item::404"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("Menu item not found with id: menu-item::404"));
    }

    @Test
    void shouldUpdateMenuItem() throws Exception {
        MenuItem updated = menuItem();
        updated.setName("Margherita XL");
        updated.setStatus(MenuItemStatus.INACTIVE);

        when(menuItemService.updateMenuItem(
                eq("menu::1"), eq("menu-item::1"), any(MenuItemUpdateRequest.class)))
                .thenReturn(updated);

        mockMvc.perform(put("/menus/menu::1/items/menu-item::1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Margherita XL",
                                  "description": "Bigger version",
                                  "category": "Pizza",
                                  "price": 320,
                                  "currency": "TRY",
                                  "photoUrls": ["https://cdn.example.com/image-2.jpg"],
                                  "videoUrls": ["https://cdn.example.com/video-2.mp4"],
                                  "allergens": ["gluten"],
                                  "ingredients": ["tomato"],
                                  "status": "INACTIVE"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Margherita XL"))
                .andExpect(jsonPath("$.status").value("INACTIVE"));

        verify(menuItemService).updateMenuItem(
                eq("menu::1"), eq("menu-item::1"), any(MenuItemUpdateRequest.class));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingMissingMenuItem() throws Exception {
        when(menuItemService.updateMenuItem(
                eq("menu::1"), eq("menu-item::404"), any(MenuItemUpdateRequest.class)))
                .thenThrow(new MenuItemNotFoundException("menu-item::404"));

        mockMvc.perform(put("/menus/menu::1/items/menu-item::404")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Margherita XL",
                                  "price": 320,
                                  "status": "ACTIVE"
                                }
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteMenuItem() throws Exception {
        mockMvc.perform(delete("/menus/menu::1/items/menu-item::1"))
                .andExpect(status().isNoContent());

        verify(menuItemService).deleteMenuItem("menu::1", "menu-item::1");
    }

    @Test
    void shouldReturnNotFoundWhenDeletingMissingMenuItem() throws Exception {
        doThrow(new MenuItemNotFoundException("menu-item::404"))
                .when(menuItemService).deleteMenuItem("menu::1", "menu-item::404");

        mockMvc.perform(delete("/menus/menu::1/items/menu-item::404"))
                .andExpect(status().isNotFound());
    }

    private MenuItem menuItem() {
        LocalDateTime now = LocalDateTime.now();

        return MenuItem.builder()
                .id("menu-item::1")
                .menuId("menu::1")
                .restaurantId("restaurant-1")
                .name("Margherita")
                .description("Tomato, mozzarella, basil")
                .category("Pizza")
                .price(BigDecimal.valueOf(250))
                .currency("TRY")
                .photoUrls(List.of("https://cdn.example.com/image-1.jpg"))
                .videoUrls(List.of("https://cdn.example.com/video-1.mp4"))
                .allergens(List.of("gluten"))
                .ingredients(List.of("tomato", "mozzarella"))
                .status(MenuItemStatus.ACTIVE)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}
