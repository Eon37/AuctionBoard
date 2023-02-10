package com.example.AuctionBoard.api.user;

import java.util.Collection;

public interface UserService {
    User getById(Long id);
    User getByEmail(String email);
    User create(User user);
    User update(User user);
    void delete(Long id);
}
