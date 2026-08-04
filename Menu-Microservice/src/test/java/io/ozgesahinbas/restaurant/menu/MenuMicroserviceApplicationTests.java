package io.ozgesahinbas.restaurant.menu;

import io.ozgesahinbas.restaurant.menu.aspect.LoggingAspect;
import io.ozgesahinbas.restaurant.menu.controller.MenuController;
import io.ozgesahinbas.restaurant.menu.controller.MenuItemController;
import io.ozgesahinbas.restaurant.menu.controller.RestaurantMenuController;
import io.ozgesahinbas.restaurant.menu.exception.GlobalExceptionHandler;
import io.ozgesahinbas.restaurant.menu.repository.MenuItemRepository;
import io.ozgesahinbas.restaurant.menu.repository.MenuRepository;
import io.ozgesahinbas.restaurant.menu.service.MenuItemService;
import io.ozgesahinbas.restaurant.menu.service.MenuService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the wiring of the microservice. Couchbase itself is not started:
 * the repositories are replaced by mocks (see application-test.yml).
 */
@SpringBootTest
@ActiveProfiles("test")
class MenuMicroserviceApplicationTests {

    @MockitoBean
    private MenuRepository menuRepository;

    @MockitoBean
    private MenuItemRepository menuItemRepository;

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void shouldLoadEveryLayerOfTheService() {
        assertThat(applicationContext.getBean(MenuController.class)).isNotNull();
        assertThat(applicationContext.getBean(MenuItemController.class)).isNotNull();
        assertThat(applicationContext.getBean(RestaurantMenuController.class)).isNotNull();
        assertThat(applicationContext.getBean(MenuService.class)).isNotNull();
        assertThat(applicationContext.getBean(MenuItemService.class)).isNotNull();
        assertThat(applicationContext.getBean(GlobalExceptionHandler.class)).isNotNull();
        assertThat(applicationContext.getBean(LoggingAspect.class)).isNotNull();
    }
}
