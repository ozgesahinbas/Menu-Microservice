package com.example.menumicroservice.repository;

import com.example.menumicroservice.model.Menu;
import org.springframework.data.couchbase.repository.CouchbaseRepository;

public interface MenuRepository extends CouchbaseRepository<Menu, String> {
}