package io.ozgesahinbas.restaurant.menu.service;

import io.ozgesahinbas.restaurant.menu.dto.MenuCreateRequest;
import io.ozgesahinbas.restaurant.menu.dto.MenuUpdateRequest;
import io.ozgesahinbas.restaurant.menu.entity.Menu;

import java.util.List;

/**
 * Menu lifecycle operations. Menu items are handled by {@link MenuItemService}.
 */
public interface MenuService {

    Menu createMenu(MenuCreateRequest request);

    List<Menu> getAllMenus();

    Menu getMenuById(String id);

    List<Menu> getMenusByRestaurantId(String restaurantId);

    Menu updateMenu(String id, MenuUpdateRequest request);

    /**
     * Deletes the menu together with every item that belongs to it.
     */
    void deleteMenu(String id);
}
