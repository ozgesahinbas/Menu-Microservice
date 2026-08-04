package io.ozgesahinbas.restaurant.menu.controller;

import io.ozgesahinbas.restaurant.menu.entity.Menu;
import io.ozgesahinbas.restaurant.menu.enums.MenuStatus;
import io.ozgesahinbas.restaurant.menu.enums.MenuType;
import io.ozgesahinbas.restaurant.menu.exception.GlobalExceptionHandler;
import io.ozgesahinbas.restaurant.menu.service.MenuService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RestaurantMenuControllerTest {

    @Mock
    private MenuService menuService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new RestaurantMenuController(menuService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldReturnMenusOfRestaurant() throws Exception {
        when(menuService.getMenusByRestaurantId("restaurant-1"))
                .thenReturn(List.of(menu("menu::1", "Day Menu", MenuType.DAY),
                                    menu("menu::2", "Night Menu", MenuType.NIGHT)));

        mockMvc.perform(get("/restaurants/restaurant-1/menus"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Day Menu"))
                .andExpect(jsonPath("$[1].name").value("Night Menu"));

        verify(menuService).getMenusByRestaurantId("restaurant-1");
    }

    @Test
    void shouldReturnEmptyListForRestaurantWithoutMenus() throws Exception {
        when(menuService.getMenusByRestaurantId("restaurant-9")).thenReturn(List.of());

        mockMvc.perform(get("/restaurants/restaurant-9/menus"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    private Menu menu(String id, String name, MenuType menuType) {
        return Menu.builder()
                .id(id)
                .restaurantId("restaurant-1")
                .name(name)
                .menuType(menuType)
                .status(MenuStatus.ACTIVE)
                .build();
    }
}
