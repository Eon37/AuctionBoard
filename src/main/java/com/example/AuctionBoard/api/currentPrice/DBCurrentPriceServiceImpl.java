package com.example.AuctionBoard.api.currentPrice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DBCurrentPriceServiceImpl implements CurrentPriceService {
    private final CurrentPriceRepository currentPriceRepository;

    @Autowired
    public DBCurrentPriceServiceImpl(CurrentPriceRepository currentPriceRepository) {
        this.currentPriceRepository = currentPriceRepository;
    }

    @Override
    public Optional<CurrentPrice> getByNoticeId(Long noticeId) {
        return currentPriceRepository.findByNoticeId(noticeId);
    }

    @Override
    public CurrentPrice save(CurrentPrice currentPrice) {
        return currentPriceRepository.save(currentPrice);
    }

    @Override
    public void delete(Long id) {
        currentPriceRepository.deleteById(id);
    }
}
