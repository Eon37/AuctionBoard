package com.example.AuctionBoard.api.image;

public class DefaultImages {
    private static final DBImage DB_IMAGE = new DBImage(null, "", new byte[0]);

    public static DBImage dbImage() {
        return DB_IMAGE;
    }
}
