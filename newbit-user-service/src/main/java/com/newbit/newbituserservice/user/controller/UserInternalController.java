package com.newbit.newbituserservice.user.controller;

import com.newbit.newbituserservice.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/user")
public class UserInternalController {

    private final UserService userService;

    @PostMapping("/{userId}/diamond/use")
    public ResponseEntity<Integer> useDiamond(@PathVariable Long userId, @RequestParam int amount) {
        return ResponseEntity.ok(userService.useDiamond(userId, amount));
    }

    @PostMapping("/{userId}/diamond/add")
    public ResponseEntity<Integer> addDiamond(@PathVariable Long userId, @RequestParam int amount) {
        return ResponseEntity.ok(userService.addDiamond(userId, amount));
    }

    @PostMapping("/{userId}/point/use")
    public ResponseEntity<Integer> usePoint(@PathVariable Long userId, @RequestParam int amount) {
        return ResponseEntity.ok(userService.usePoint(userId, amount));
    }

    @PostMapping("/{userId}/point/add")
    public ResponseEntity<Integer> addPoint(@PathVariable Long userId, @RequestParam int amount) {
        return ResponseEntity.ok(userService.addPoint(userId, amount));
    }
}