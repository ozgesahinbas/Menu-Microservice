package io.ozgesahinbas.restaurant.menu.service;

import io.ozgesahinbas.restaurant.menu.dto.MenuCreateRequest;
import io.ozgesahinbas.restaurant.menu.dto.MenuUpdateRequest;
import io.ozgesahinbas.restaurant.menu.entity.Menu;
import io.ozgesahinbas.restaurant.menu.exception.MenuNotFoundException;
import io.ozgesahinbas.restaurant.menu.repository.MenuItemRepository;
import io.ozgesahinbas.restaurant.menu.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;
    private final MenuItemRepository menuItemRepository;

    @Override
    public Menu createMenu(MenuCreateRequest request) {
        return menuRepository.save(request.toEntity());
    }

    @Override
    public List<Menu> getAllMenus() {
        return menuRepository.findAll();
    }

    @Override
    public Menu getMenuById(String id) {
        return menuRepository.findById(id)
                .orElseThrow(() -> new MenuNotFoundException(id));
    }

    @Override
    public List<Menu> getMenusByRestaurantId(String restaurantId) {
        return menuRepository.findByRestaurantId(restaurantId);
    }

    @Override
    public Menu updateMenu(String id, MenuUpdateRequest request) {
        Menu menu = getMenuById(id);
        request.updateEntity(menu);

        return menuRepository.save(menu);
    }

    @Override
    public void deleteMenu(String id) {
        Menu menu = getMenuById(id);

        // Items live as separate documents, so they have to be removed
        // explicitly - Couchbase has no cascading delete.
        menuItemRepository.deleteAll(menuItemRepository.findByMenuId(id));
        menuRepository.delete(menu);
    }
}
