package com.example.AuctionBoard.api.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> { throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found by id"); });
    }

    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> { throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found by email"); });
    }

    @Override
    public User save(User user) {
        return user.getId() == null ? create(user) : update(user);
    }

    private User create(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    private User update(User newUser) {
        User oldUser = getById(newUser.getId());

        if (!StringUtils.hasText(newUser.getEmail())) newUser.setEmail(oldUser.getEmail());
        newUser.setPassword(StringUtils.hasText(newUser.getPassword())
                ? passwordEncoder.encode(newUser.getPassword())
                : oldUser.getPassword());

        return userRepository.save(newUser);
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
