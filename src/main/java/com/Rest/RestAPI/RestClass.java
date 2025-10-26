package com.Rest.RestAPI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/users")
public class RestClass {

    private final UserRepository userRepository;

    public RestClass(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @PostMapping("/")
    public User create(@RequestBody User user) {
        return userRepository.save(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        Optional<User> found = userRepository.findById(id);
        if (found.isPresent()) {
            return ResponseEntity.ok(found.get());
        }
        return ResponseEntity.status(404).body(Map.of("error", "User not found"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody User user) {
        Optional<User> found = userRepository.findById(id);
        if (found.isPresent()) {
            User existing = found.get();
            existing.setName(user.getName());
            existing.setAge(user.getAge());
            User saved = userRepository.save(existing);
            return ResponseEntity.ok(saved);
        }
        return ResponseEntity.status(404).body(Map.of("error", "User not found"));
    }

    @DeleteMapping("/{id}")
    public Map<String, String> delete(@PathVariable String id) {
        boolean exists = userRepository.existsById(id);
        if (exists) {
            userRepository.deleteById(id);
            return Map.of("status", "Deleted");
        }
        return Map.of("status", "User not found");
    }
}
