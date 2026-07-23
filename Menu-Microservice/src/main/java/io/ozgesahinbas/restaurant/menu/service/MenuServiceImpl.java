package io.ozgesahinbas.restaurant.menu.service;

import io.ozgesahinbas.restaurant.menu.dto.MenuCreateRequest;
import io.ozgesahinbas.restaurant.menu.exception.MenuNotFoundException;
import io.ozgesahinbas.restaurant.menu.model.Menu;
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
}