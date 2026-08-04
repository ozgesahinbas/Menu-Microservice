package io.ozgesahinbas.restaurant.menu.controller;

import io.ozgesahinbas.restaurant.menu.entity.Menu;
import io.ozgesahinbas.restaurant.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Restaurant-scoped view of menus. Kept apart from {@link MenuController}
 * because it hangs off the /restaurants resource tree.
 */
@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
public class RestaurantMenuController {

    private final MenuService menuService;

    @GetMapping("/{restaurantId}/menus")
    public List<Menu> getMenusByRestaurantId(@PathVariable String restaurantId) {
        return menuService.getMenusByRestaurantId(restaurantId);
    }
}
