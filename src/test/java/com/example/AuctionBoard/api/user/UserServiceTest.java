package com.example.AuctionBoard.api.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    public static final String TEST_EMAIL = "test@email.com";
    public static final String TEST_PASS = "password";

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUserWithExistingEmailException() {
        //Given
        Mockito.when(userRepository.existsByEmail(TEST_EMAIL)).thenReturn(true);

        //When
        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> userService.create(new User(TEST_EMAIL, TEST_PASS)));

        //Then
        Assertions.assertEquals("User with the given email already exists", exception.getReason());
    }

    @Test
    void createUserVerifyPasswordEncoderCalled() {
        //Given
        User user = new User(TEST_EMAIL, TEST_PASS);
        Mockito.when(userRepository.existsByEmail(TEST_EMAIL)).thenReturn(false);
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        //When
        userService.create(user);

        //Then
        Mockito.verify(passwordEncoder, Mockito.times(1)).encode(TEST_PASS);
    }

    @Nested
    class AuthorizedRequestsTest {
        @Test
        void getByIdNotFoundException() {
            //Given
            Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

            //When
            ResponseStatusException exception =
                    assertThrows(ResponseStatusException.class, () -> userService.getById(1L));

            //Then
            Assertions.assertEquals("User not found by id", exception.getReason());
        }

        @Test
        void getByEmailNotFoundException() {
            //Given
            String userEmail = "test_email@email.com";
            Mockito.when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

            //When
            ResponseStatusException exception =
                    assertThrows(ResponseStatusException.class, () -> userService.getByEmail(userEmail));

            //Then
            Assertions.assertEquals("User not found by email", exception.getReason());
        }

        @Test
        void updateUserPasswordVerifyPasswordEncoderCalled() {
            //Given
            User user = new User(TEST_EMAIL, TEST_PASS);
            Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

            //When
            userService.update(1L, user);

            //Then
            Mockito.verify(passwordEncoder, Mockito.times(1)).encode(TEST_PASS);
        }

        @Test
        void updateUserWithoutPasswordVerifyPasswordEncoderNotCalled() {
            //Given
            User user = new User(TEST_EMAIL, TEST_PASS);
            Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

            //When
            userService.update(1L, new User(TEST_EMAIL, null));

            //Then
            Mockito.verify(passwordEncoder, Mockito.times(0)).encode(TEST_PASS);
        }
    }
}
