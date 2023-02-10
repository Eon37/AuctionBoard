package com.example.AuctionBoard.api.notification;

import com.example.AuctionBoard.api.currentPrice.CurrentPrice;
import com.example.AuctionBoard.api.notice.Notice;

public interface NotificationService {
    void notifyPriceOutdated(String email, Notice notice);
    void notifySold(Notice notice, CurrentPrice currentPrice);
}
