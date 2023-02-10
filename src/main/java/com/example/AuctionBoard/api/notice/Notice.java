package com.example.AuctionBoard.api.notice;

import com.example.AuctionBoard.api.currentPrice.CurrentPrice;
import com.example.AuctionBoard.api.image.DBImage;
import com.example.AuctionBoard.api.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.time.Instant;

@Entity(name = "notices")
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title;
    private String description;
    private Integer startingPrice;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean active;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "DEFAULT_TIMEZONE")
    private Instant dueTo;
    //todo fix n+1
    @ManyToOne //todo cascade
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id")
    private DBImage image;
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "notice")
    @JsonBackReference
    private CurrentPrice currentPrice;

    public Notice(String title, String description, Integer startingPrice) {
        this.title = title;
        this.description = description;
        this.startingPrice = startingPrice;
    }

    private Notice() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStartingPrice() {
        return startingPrice;
    }

    public void setStartingPrice(Integer startingPrice) {
        this.startingPrice = startingPrice;
    }

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean isActive) {
        this.active = isActive;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public DBImage getImage() {
        return image;
    }

    public void setImage(DBImage image) {
        this.image = image;
    }

    public Instant getDueTo() {
        return dueTo;
    }

    public void setDueTo(Instant dueTo) {
        this.dueTo = dueTo;
    }

    public CurrentPrice getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(CurrentPrice currentPrice) {
        this.currentPrice = currentPrice;
    }
}
