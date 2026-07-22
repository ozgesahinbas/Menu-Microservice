package io.ozgesahinbas.restaurant.menu.service;

import io.ozgesahinbas.restaurant.menu.dto.MenuCreateRequest;
import io.ozgesahinbas.restaurant.menu.model.Menu;
import io.ozgesahinbas.restaurant.menu.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl {

    private final MenuRepository menuRepository;

    public void createMenu(MenuCreateRequest request) {
        menuRepository.save(request.toEntity());
    }
}