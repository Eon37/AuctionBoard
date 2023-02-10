package com.example.AuctionBoard.api.currentPrice;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CurrentPriceRepository extends CrudRepository<CurrentPrice, Long> {
    Optional<CurrentPrice> findByNoticeId(Long noticeId);
}
