package io.ozgesahinbas.restaurant.menu.repository;
import io.ozgesahinbas.restaurant.menu.entity.MenuItem;
import org.springframework.data.couchbase.repository.CouchbaseRepository;

import java.util.List;

public interface MenuItemRepository
        extends CouchbaseRepository<MenuItem, String> {

    List<MenuItem> findByMenuId(String menuId);
}
