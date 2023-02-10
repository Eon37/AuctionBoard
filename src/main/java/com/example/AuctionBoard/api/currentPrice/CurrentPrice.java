package com.example.AuctionBoard.api.currentPrice;

import com.example.AuctionBoard.api.notice.Notice;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

@Entity(name = "current_prices")
public class CurrentPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "notice_id", referencedColumnName = "id")
    @JsonManagedReference
    private Notice notice;
    private Integer currentPrice;
    private String currentEmail;

    public CurrentPrice(Long id, Notice notice, Integer currentPrice, String currentEmail) {
        this.id = id;
        this.notice = notice;
        this.currentPrice = currentPrice;
        this.currentEmail = currentEmail;
    }

    private CurrentPrice() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Notice getNotice() {
        return notice;
    }

    public void setNotice(Notice notice) {
        this.notice = notice;
    }

    public Integer getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(Integer currentPrice) {
        this.currentPrice = currentPrice;
    }

    public String getCurrentEmail() {
        return currentEmail;
    }

    public void setCurrentEmail(String currentEmail) {
        this.currentEmail = currentEmail;
    }
}
