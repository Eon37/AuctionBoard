package com.example.AuctionBoard.api.currentPrice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class DBCurrentPriceServiceImpl implements CurrentPriceService {
    private final CurrentPriceRepository currentPriceRepository;

    @Autowired
    public DBCurrentPriceServiceImpl(CurrentPriceRepository currentPriceRepository) {
        this.currentPriceRepository = currentPriceRepository;
    }

    @Override
    public Collection<CurrentPrice> getAll(int pageNo, int pageSize) {
        return null; //todo do i even need this?
    }

    @Override
    public Optional<CurrentPrice> getById(Long id) {
        return currentPriceRepository.findById(id);
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
    public CurrentPrice update(CurrentPrice currentPrice) {
        //todo update
        return currentPriceRepository.save(currentPrice);
    }

    @Override
    public void delete(Long id) {
        currentPriceRepository.deleteById(id);
    }
}
