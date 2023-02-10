package com.example.AuctionBoard.api.image;

import jakarta.persistence.*;


@Entity(name = "images")
public class DBImage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String mimeType;
    @Lob
    private byte[] data;

    public DBImage(Long id, String mimeType, byte[] data) {
        this.id = id;
        this.mimeType = mimeType;
        this.data = data;
    }

    public DBImage() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
