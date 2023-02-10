package com.example.AuctionBoard;

import com.example.AuctionBoard.api.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

import static com.example.AuctionBoard.api.notice.NoticeServiceTest.TEST_EMAIL;
import static com.example.AuctionBoard.api.notice.NoticeServiceTest.TEST_PASS;

public class TestUserDetails implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return TEST_PASS;
    }

    @Override
    public String getUsername() {
        return TEST_EMAIL;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
