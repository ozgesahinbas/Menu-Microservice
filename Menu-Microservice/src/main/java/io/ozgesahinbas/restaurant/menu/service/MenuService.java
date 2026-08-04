package io.ozgesahinbas.restaurant.menu.service;

import io.ozgesahinbas.restaurant.menu.dto.MenuCreateRequest;
import io.ozgesahinbas.restaurant.menu.dto.MenuUpdateRequest;
import io.ozgesahinbas.restaurant.menu.entity.Menu;

import java.util.List;

/**
 * Menu operations. Lookups by id report a missing menu as not found, so callers
 * can rely on getting a menu back rather than checking for null.
 */
public interface MenuService {

    Menu createMenu(MenuCreateRequest request);

    List<Menu> getAllMenus();

    Menu getMenuById(String id);

    List<Menu> getMenusByRestaurantId(String restaurantId);

    Menu updateMenu(String id, MenuUpdateRequest request);

    void deleteMenu(String id);
}
