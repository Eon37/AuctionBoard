package com.example.AuctionBoard.api.user;

public interface UserService {
    User getById(Long id);
    User getByEmail(String email);
    User create(User user);
    User update(User user);
    void delete(Long id);
}
