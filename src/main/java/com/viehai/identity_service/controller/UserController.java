package com.viehai.identity_service.controller;

import com.viehai.identity_service.dto.request.UserCreateRequest;
import com.viehai.identity_service.dto.request.UserUpdateRequest;
import com.viehai.identity_service.entity.User;
import com.viehai.identity_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@Profile({"mysql","postgres"})
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping()
    User createUser(@RequestBody UserCreateRequest request){
        return userService.createUser(request);
    }

    @GetMapping()
    List<User> getUsers(){
        return userService.getUsers();

    }

    @GetMapping("/{userId}")
    User getUser(@PathVariable String userId){
        return userService.getUser(userId);
    }

    @PutMapping("/{userId}")
    User updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request){
        return userService.updateUser(userId, request);
    }

    @DeleteMapping("/{userId}")
    String deleteUser(@PathVariable String userId){
        userService.deleteUser(userId);
        return "User has been deleted";
    }

}
