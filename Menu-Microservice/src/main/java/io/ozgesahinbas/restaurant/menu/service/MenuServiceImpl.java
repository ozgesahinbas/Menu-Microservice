package io.ozgesahinbas.restaurant.menu.service;

import io.ozgesahinbas.restaurant.menu.dto.MenuCreateRequest;
import io.ozgesahinbas.restaurant.menu.dto.MenuUpdateRequest;
import io.ozgesahinbas.restaurant.menu.exception.MenuNotFoundException;
import io.ozgesahinbas.restaurant.menu.model.Menu;
import io.ozgesahinbas.restaurant.menu.model.MenuItem;
import io.ozgesahinbas.restaurant.menu.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl {

    private final MenuRepository menuRepository;

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
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new MenuNotFoundException(menuId));

        return menu.getItems();
    }
}