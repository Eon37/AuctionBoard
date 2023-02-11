package com.example.AuctionBoard.api.notification;

import com.example.AuctionBoard.api.currentPrice.CurrentPrice;
import com.example.AuctionBoard.api.notice.Notice;
import com.example.AuctionBoard.api.user.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public void notifyPriceOutdated(String email, Notice notice) {
        //email to previous buyer
        logger.info("[{}]! Someone has just raised the stakes for [{}]", email, notice.getTitle());
    }

    @Override
    public void notifySold(Notice notice, CurrentPrice currentPrice) {
        //email to seller
        logger.info("[{}]! Your good [{}] has just been sold to [{}] for [{}]",
                notice.getUser().getEmail(), notice.getTitle(), currentPrice.getCurrentEmail(), notice.getCurrentPrice());

        //email to buyer
        logger.info("[{}]! Your just bought [{}] from [{}] for [{}]",
                currentPrice.getCurrentEmail(), notice.getTitle(), notice.getUser().getEmail(), notice.getCurrentPrice());
    }
}
