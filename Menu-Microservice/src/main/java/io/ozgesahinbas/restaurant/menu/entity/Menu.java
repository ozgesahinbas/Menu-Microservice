package io.ozgesahinbas.restaurant.menu.entity;

import io.ozgesahinbas.restaurant.menu.enums.MenuStatus;
import io.ozgesahinbas.restaurant.menu.enums.MenuType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Menu {
    @Id
    private String id;
    private String restaurantId;
    private String name;
    private String description;
    private MenuType menuType;
    private MenuStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
