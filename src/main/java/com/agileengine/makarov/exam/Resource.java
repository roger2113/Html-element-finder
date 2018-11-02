package com.agileengine.makarov.exam;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Resource {

    @GetMapping("/items")
    public ResponseEntity getItems() {
        return ResponseEntity.ok("Hi there!");
    }
}
