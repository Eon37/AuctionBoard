package com.example.AuctionBoard.api.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Collection<User> getAll() {
        return (Collection<User>) userRepository.findAll();
    }

    @Override
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    String message = "User not found by id";
                    logger.error(message);
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, message);
                });
    }

    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    String message = "User not found by email";
                    logger.error(message);
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, message);
                });
    }

    @Override
    public User create(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            String message = "User with the given email already exists";
            logger.error(message);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User update(Long id, User newUser) {
        User oldUser = getById(id);

        newUser.setId(id);
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
