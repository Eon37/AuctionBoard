package com.example.AuctionBoard.api.user;

import com.example.AuctionBoard.configs.ServicePathConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Register a user",
            description = "Register a given user",
            responses = {
                    @ApiResponse(
                            description = "Create successful",
                            responseCode = "200",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            })
    @PostMapping(path = ServicePathConstants.REGISTER_SERVICE)
    public User register(@Parameter(name = "user",
                                    description = "The user to register",
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = User.class)))
                         @RequestBody User user) {
        return userService.save(user);
    }
}
