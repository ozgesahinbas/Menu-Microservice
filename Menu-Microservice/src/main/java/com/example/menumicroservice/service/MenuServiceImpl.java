package com.example.menumicroservice.service;

import com.example.menumicroservice.dto.MenuCreateRequest;
import com.example.menumicroservice.model.Menu;
import com.example.menumicroservice.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl {

    private final MenuRepository menuRepository;

    public void createMenu(MenuCreateRequest request) {
        Menu menu = Menu.builder()
                .restaurantId(request.getRestaurantId())
                .name(request.getName())
                .description(request.getDescription())
                .menuType(request.getMenuType())
                .status(request.getStatus())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        menuRepository.save(menu);

    }
}