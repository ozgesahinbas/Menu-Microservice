package io.ozgesahinbas.restaurant.menu.service;

import io.ozgesahinbas.restaurant.menu.dto.MenuCreateRequest;
import io.ozgesahinbas.restaurant.menu.dto.MenuItemCreateRequest;
import io.ozgesahinbas.restaurant.menu.dto.MenuUpdateRequest;
import io.ozgesahinbas.restaurant.menu.entity.Menu;
import io.ozgesahinbas.restaurant.menu.entity.MenuItem;
import io.ozgesahinbas.restaurant.menu.exception.MenuItemNotFoundException;
import io.ozgesahinbas.restaurant.menu.exception.MenuNotFoundException;
import io.ozgesahinbas.restaurant.menu.repository.MenuItemRepository;
import io.ozgesahinbas.restaurant.menu.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl {

    private final MenuRepository menuRepository;
    private final MenuItemRepository menuItemRepository;

    public void createMenu(MenuCreateRequest request) {
        menuRepository.save(request.toEntity());
    }

    public List<Menu> getAllMenus() {
        return menuRepository.findAll();
    }

    public Menu getMenuById(String id) {
        return menuRepository.findById(id)
                .orElseThrow(() -> new MenuNotFoundException(id));
    }

    public Menu updateMenu(String id, MenuUpdateRequest request) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new MenuNotFoundException(id));

        request.updateEntity(menu);
        return menuRepository.save(menu);
    }

    public void deleteMenu(String id) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new MenuNotFoundException(id));

        menuRepository.delete(menu);
    }

    public List<MenuItem> getMenuItems(String menuId) {
        menuRepository.findById(menuId)
                .orElseThrow(() -> new MenuNotFoundException(menuId));

        return menuItemRepository.findByMenuId(menuId);
    }
    public MenuItem createMenuItem(String menuId, MenuItemCreateRequest request) {

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new MenuNotFoundException(menuId));

        MenuItem menuItem = request.toEntity(menuId, menu.getRestaurantId());
        return menuItemRepository.save(menuItem);
    }
    public MenuItem getMenuItemById(String menuId, String itemId) {

        menuRepository.findById(menuId)
                .orElseThrow(() -> new MenuNotFoundException(menuId));

        MenuItem menuItem = menuItemRepository.findById(itemId)
                .orElseThrow(() -> new MenuItemNotFoundException(itemId));
        if (!menuItem.getMenuId().equals(menuId)) {
            throw new RuntimeException("Menu item does not belong to this menu");
        }
        return menuItem;
    }
    public List<Menu> getMenusByRestaurantId(String restaurantId) {
        return menuRepository.findByRestaurantId(restaurantId);
    }
}