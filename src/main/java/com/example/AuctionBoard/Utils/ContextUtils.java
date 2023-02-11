package com.example.AuctionBoard.Utils;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

public class ContextUtils {

    public static Optional<UserDetails> getSpringContextUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return auth.getPrincipal() instanceof UserDetails
                ? Optional.of((UserDetails) auth.getPrincipal())
                : Optional.empty();
    }

    public static UserDetails getSpringContextUserOrThrow() {
        return getSpringContextUser()
                .orElseThrow(() -> { throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden action"); });
    }
}
