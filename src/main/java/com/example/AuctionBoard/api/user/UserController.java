package com.example.AuctionBoard.api.user;

import com.example.AuctionBoard.configs.ServicePathConstants;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(path = ServicePathConstants.REGISTER_SERVICE)
    public User register(@RequestBody User user) {
        return userService.create(user);
    }
}
