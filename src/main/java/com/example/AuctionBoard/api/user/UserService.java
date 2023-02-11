package com.example.AuctionBoard.api.user;

import java.util.Collection;

public interface UserService {
    Collection<User> getAll();
    User getById(Long id);
    User getByEmail(String email);
    User create(User user);
    User update(Long id, User user);
    void delete(Long id);
}
