package com.viehai.identity_service.controller;

import com.viehai.identity_service.dto.request.UserCreateRequest;
import com.viehai.identity_service.dto.request.UserUpdateRequest;
import com.viehai.identity_service.entity.UserMongo;
import com.viehai.identity_service.service.UserMongoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mongo-users")
@Profile("mongodb")
public class UserMongoController {

    @Autowired
    private UserMongoService userMongoService;

    @PostMapping()
    UserMongo createUser(@RequestBody UserCreateRequest request){
        return userMongoService.createUser(request);
    }

    @GetMapping()
    List<UserMongo> getUsers(){
        return userMongoService.getUsers();
    }

    @GetMapping("/{userId}")
    UserMongo getUser(@PathVariable String userId){
        return userMongoService.getUser(userId);
    }

    @PutMapping("/{userId}")
    UserMongo updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request){
        return userMongoService.updateUser(userId, request);
    }

    @DeleteMapping("/{userId}")
    String deleteUser(@PathVariable String userId){
        userMongoService.deleteUser(userId);
        return "User has been deleted from MongoDB";
    }
}
