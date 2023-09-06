package com.example.sigurnostprojekat.controllers;

import com.example.sigurnostprojekat.base.CrudController;
import com.example.sigurnostprojekat.models.dto.User;
import com.example.sigurnostprojekat.models.request.UserRequest;
import com.example.sigurnostprojekat.services.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/users")
public class UserController extends CrudController<Integer, UserRequest, User> {
    private final UserService userService;
    public UserController(UserService userService) {
        super(User.class, userService);
        this.userService = userService;
    }

    @RequestMapping("/chat")
    public List<User> getOtherUsers(){
        return userService.getAllOtherUsers();
    }
}
