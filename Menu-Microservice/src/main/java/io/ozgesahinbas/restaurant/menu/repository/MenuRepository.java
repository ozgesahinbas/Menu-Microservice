package io.ozgesahinbas.restaurant.menu.repository;

import io.ozgesahinbas.restaurant.menu.entity.Menu;
import org.springframework.data.couchbase.repository.CouchbaseRepository;

public interface MenuRepository extends CouchbaseRepository<Menu, String> {
}