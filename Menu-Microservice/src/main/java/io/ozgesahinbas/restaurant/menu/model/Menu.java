package io.ozgesahinbas.restaurant.menu.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

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
    private List<MenuItem> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
