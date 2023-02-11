package com.example.AuctionBoard.api.user;

public interface UserService {
    User getById(Long id);
    User getByEmail(String email);
    User save(User user);
    void delete(Long id);
}
