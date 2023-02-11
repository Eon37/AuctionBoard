package com.example.AuctionBoard.api.currentPrice;

import java.util.Optional;

public interface CurrentPriceService {
    Optional<CurrentPrice> getByNoticeId(Long noticeId);
    CurrentPrice save(CurrentPrice currentPrice);
    void delete(Long id);
}
