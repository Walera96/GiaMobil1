package com.spbutu.gia.auth.web;

import com.spbutu.gia.auth.domain.entity.AppUser;
import com.spbutu.gia.auth.domain.repository.AppUserRepository;
import com.spbutu.gia.auth.web.dto.CreateUserRequest;
import com.spbutu.gia.auth.web.dto.UpdateUserRequest;
import com.spbutu.gia.auth.web.dto.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(AppUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
            .map(this::toDto)
            .toList();
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable UUID id) {
        return userRepository.findById(id)
            .map(this::toDto)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody CreateUserRequest req) {
        if (userRepository.findByUsername(req.username()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }
        AppUser user = new AppUser();
        user.setUsername(req.username());
        user.setPassword(passwordEncoder.encode(req.password()));
        user.setFullName(req.fullName());
        user.setEmail(req.email());
        user.setRole(req.role());
        AppUser saved = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
    }

    @PutMapping("/{id}")
    public UserDto update(@PathVariable UUID id, @RequestBody UpdateUserRequest req) {
        AppUser user = userRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        user.setUsername(req.username());
        user.setFullName(req.fullName());
        user.setEmail(req.email());
        user.setRole(req.role());
        return toDto(userRepository.save(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private UserDto toDto(AppUser u) {
        return new UserDto(u.getId(), u.getUsername(), u.getFullName(), u.getEmail(), u.getRole(), u.getCreatedAt(), u.getUpdatedAt());
    }
}
