package com.example.AuctionBoard.api.currentPrice;

import java.util.Collection;
import java.util.Optional;

public interface CurrentPriceService {
    Collection<CurrentPrice> getAll(int pageNo, int pageSize);
    Optional<CurrentPrice> getById(Long id);
    Optional<CurrentPrice> getByNoticeId(Long noticeId);
    CurrentPrice save(CurrentPrice currentPrice);
    CurrentPrice update(CurrentPrice currentPrice);
    void delete(Long id);
}
