package io.ozgesahinbas.restaurant.menu.controller;

import io.ozgesahinbas.restaurant.menu.dto.MenuCreateRequest;
import io.ozgesahinbas.restaurant.menu.service.MenuServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

        mockMvc.perform(post("/menus")
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
        mockMvc.perform(post("/menus")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }
    @Test
    void shouldGetAllMenusSuccessfully() throws Exception{
        mockMvc.perform(get("/menus"))
                .andExpect(status().isOk());
    }
}