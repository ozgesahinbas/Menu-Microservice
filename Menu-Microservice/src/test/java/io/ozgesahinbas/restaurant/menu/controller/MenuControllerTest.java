package io.ozgesahinbas.restaurant.menu.controller;

import io.ozgesahinbas.restaurant.menu.dto.MenuCreateRequest;
import io.ozgesahinbas.restaurant.menu.dto.MenuUpdateRequest;
import io.ozgesahinbas.restaurant.menu.entity.Menu;
import io.ozgesahinbas.restaurant.menu.enums.MenuStatus;
import io.ozgesahinbas.restaurant.menu.enums.MenuType;
import io.ozgesahinbas.restaurant.menu.exception.GlobalExceptionHandler;
import io.ozgesahinbas.restaurant.menu.exception.MenuNotFoundException;
import io.ozgesahinbas.restaurant.menu.service.MenuService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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
class MenuControllerTest {

    @Mock
    private MenuService menuService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new MenuController(menuService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldCreateMenu() throws Exception {
        when(menuService.createMenu(any(MenuCreateRequest.class))).thenReturn(menu());

        mockMvc.perform(post("/menus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "restaurantId": "restaurant-1",
                                  "name": "Night Menu",
                                  "description": "Served after 20:00",
                                  "menuType": "NIGHT",
                                  "status": "ACTIVE"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("menu::1"))
                .andExpect(jsonPath("$.name").value("Night Menu"))
                .andExpect(jsonPath("$.menuType").value("NIGHT"));

        verify(menuService).createMenu(any(MenuCreateRequest.class));
    }

    @Test
    void shouldReportEveryValidationErrorOfAnInvalidMenu() throws Exception {
        mockMvc.perform(post("/menus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.validationErrors.name").value("Menu name cannot be blank"))
                .andExpect(jsonPath("$.validationErrors.restaurantId")
                        .value("Restaurant id cannot be blank"))
                .andExpect(jsonPath("$.validationErrors.menuType")
                        .value("Menu type cannot be null"));
    }

    @Test
    void shouldRejectUnknownMenuType() throws Exception {
        mockMvc.perform(post("/menus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "restaurantId": "restaurant-1",
                                  "name": "Night Menu",
                                  "menuType": "BRUNCH"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Malformed request body"));
    }

    @Test
    void shouldReturnAllMenus() throws Exception {
        when(menuService.getAllMenus()).thenReturn(List.of(menu()));

        mockMvc.perform(get("/menus"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value("menu::1"));

        verify(menuService).getAllMenus();
    }

    @Test
    void shouldReturnMenuById() throws Exception {
        when(menuService.getMenuById("menu::1")).thenReturn(menu());

        mockMvc.perform(get("/menus/menu::1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Night Menu"));
    }

    @Test
    void shouldReturnNotFoundForMissingMenu() throws Exception {
        when(menuService.getMenuById("menu::404"))
                .thenThrow(new MenuNotFoundException("menu::404"));

        mockMvc.perform(get("/menus/menu::404"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Menu not found with id: menu::404"))
                .andExpect(jsonPath("$.path").value("/menus/menu::404"));
    }

    @Test
    void shouldUpdateMenu() throws Exception {
        Menu updated = menu();
        updated.setName("Updated Menu");
        updated.setStatus(MenuStatus.INACTIVE);

        when(menuService.updateMenu(eq("menu::1"), any(MenuUpdateRequest.class)))
                .thenReturn(updated);

        mockMvc.perform(put("/menus/menu::1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Updated Menu",
                                  "description": "Updated description",
                                  "menuType": "NIGHT",
                                  "status": "INACTIVE"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Menu"))
                .andExpect(jsonPath("$.status").value("INACTIVE"));

        verify(menuService).updateMenu(eq("menu::1"), any(MenuUpdateRequest.class));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingMissingMenu() throws Exception {
        when(menuService.updateMenu(eq("menu::404"), any(MenuUpdateRequest.class)))
                .thenThrow(new MenuNotFoundException("menu::404"));

        mockMvc.perform(put("/menus/menu::404")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Updated Menu",
                                  "menuType": "NIGHT",
                                  "status": "ACTIVE"
                                }
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteMenu() throws Exception {
        mockMvc.perform(delete("/menus/menu::1"))
                .andExpect(status().isNoContent());

        verify(menuService).deleteMenu("menu::1");
    }

    @Test
    void shouldReturnNotFoundWhenDeletingMissingMenu() throws Exception {
        doThrow(new MenuNotFoundException("menu::404"))
                .when(menuService).deleteMenu("menu::404");

        mockMvc.perform(delete("/menus/menu::404"))
                .andExpect(status().isNotFound());
    }

    private Menu menu() {
        LocalDateTime now = LocalDateTime.now();

        return Menu.builder()
                .id("menu::1")
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
