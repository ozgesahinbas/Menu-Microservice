package io.ozgesahinbas.restaurant.menu.service;

import io.ozgesahinbas.restaurant.menu.dto.MenuCreateRequest;
import io.ozgesahinbas.restaurant.menu.model.MenuStatus;
import io.ozgesahinbas.restaurant.menu.model.MenuType;
import io.ozgesahinbas.restaurant.menu.repository.MenuRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import io.ozgesahinbas.restaurant.menu.model.Menu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MenuServiceImplTest {

    @Mock
    private MenuRepository menuRepository;

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

        ArgumentCaptor<Menu> menuCaptor = ArgumentCaptor.forClass(Menu.class);

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

}