package com.example.AuctionBoard.api.user;

import com.example.AuctionBoard.configs.ServicePathConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

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
                    @ApiResponse(
                            description = "User with the given email already registered",
                            responseCode = "400")
            })
    @PostMapping(path = ServicePathConstants.REGISTER_SERVICE)
    public User register(@Parameter(name = "user",
                                    description = "The user to register",
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = User.class)))
                         @RequestBody User user) {
        return userService.create(user);
    }

    @Operation(summary = "Get users",
            description = "Get list of users",
            responses = {
                    @ApiResponse(
                            description = "List of users",
                            responseCode = "200",
                            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = User.class)))),
                    @ApiResponse(
                            description = "Unauthorized access",
                            responseCode = "401")
            })
    @GetMapping(path = ServicePathConstants.USER_SERVICE)
    public Collection<User> getAll() {
        return userService.getAll();
    }

    @Operation(summary = "Get the user",
            description = "Get the user by id",
            responses = {
                    @ApiResponse(
                            description = "User",
                            responseCode = "200",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
                    @ApiResponse(
                            description = "Notice not found",
                            responseCode = "404"),
                    @ApiResponse(
                            description = "Unauthorized access",
                            responseCode = "401")
            })
    @GetMapping(path = ServicePathConstants.USER_SERVICE + "/{id}")
    public User getById(@Parameter(name = "id",
                                description = "The id of the notice to get",
                                required = true)
                        @PathVariable Long id) {
        return userService.getById(id);
    }

    @Operation(summary = "Update a user",
            description = "Update a user",
            responses = {
                    @ApiResponse(
                            description = "Update successful",
                            responseCode = "200",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
                    @ApiResponse(
                            description = "Unauthorized access",
                            responseCode = "401")
            })
    @PostMapping(path = ServicePathConstants.USER_SERVICE + "/{id}")
    public User update(@Parameter(name = "id",
                                description = "The id of the user to update",
                                required = true,
                                content = @Content(schema = @Schema(implementation = User.class)))
                       @PathVariable Long id,

                       @Parameter(name = "user",
                                description = "The user update",
                                required = true,
                                content = @Content(schema = @Schema(implementation = User.class)))
                       @RequestBody User user) {
        return userService.update(id, user);
    }

    @Operation(summary = "Delete notice",
            description = "Delete the notice",
            responses = {
                    @ApiResponse(
                            description = "Delete successful",
                            responseCode = "200"),
                    @ApiResponse(
                            description = "Unauthorized access",
                            responseCode = "401")
            })
    @DeleteMapping(path = ServicePathConstants.USER_SERVICE + "/{id}")
    public void delete(@Parameter(name = "id",
                                description = "The id of the user to delete",
                                required = true)
                       @PathVariable Long id) {
        userService.delete(id);
    }
}
