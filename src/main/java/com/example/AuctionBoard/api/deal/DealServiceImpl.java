package com.example.AuctionBoard.api.deal;

import com.example.AuctionBoard.Utils.IdConcurrentLock;
import com.example.AuctionBoard.Utils.ContextUtils;
import com.example.AuctionBoard.api.currentPrice.CurrentPrice;
import com.example.AuctionBoard.api.currentPrice.CurrentPriceService;
import com.example.AuctionBoard.api.notice.Notice;
import com.example.AuctionBoard.api.notice.NoticeService;
import com.example.AuctionBoard.api.notification.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class DealServiceImpl implements DealService {
    private static final Logger logger = LoggerFactory.getLogger(DealServiceImpl.class);

    private final NoticeService noticeService;
    private final CurrentPriceService currentPriceService;
    private final TaskScheduler taskScheduler;
    private final NotificationService notificationService;

    public DealServiceImpl(NoticeService noticeService,
                           CurrentPriceService currentPriceService,
                           TaskScheduler taskScheduler,
                           NotificationService notificationService) {
        this.noticeService = noticeService;
        this.currentPriceService = currentPriceService;
        this.taskScheduler = taskScheduler;
        this.notificationService = notificationService;
    }

    @Override
    public void bet(Long noticeId, Integer newPrice) {
        try {
            if (!IdConcurrentLock.tryLock(IdConcurrentLock.BET_LOCK + noticeId)) {
                String message = "Someone is already making a bet. Try again later";
                logger.error(message);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message);
            }

            Notice notice = noticeService.getById(noticeId);

            if (!notice.isActive()) {
                String message = "Notice is already inactive";
                logger.error(message);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
            }

            String newUserEmail = ContextUtils.getSpringContextUserOrThrow().getUsername();

            if (newUserEmail.equals(notice.getUser().getEmail())) {
                String message = "User cannot buy his own stuff";
                logger.error(message);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
            }

            Optional<CurrentPrice> currentPrice = currentPriceService.getByNoticeId(noticeId);

            if (currentPrice.isPresent() && newPrice <= currentPrice.get().getCurrentPrice()) {
                String message = "Cannot lower the price";
                logger.error(message);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
            }

            CurrentPrice updateCurrentPrice = new CurrentPrice(
                    currentPrice.map(CurrentPrice::getId).orElse(null),
                    notice,
                    newPrice,
                    newUserEmail);

            currentPriceService.save(updateCurrentPrice);

            if (currentPrice.isEmpty()) {
                logger.info("Notice [{}] is scheduled to deactivate due to [{}]", noticeId, notice.getDueTo());
                taskScheduler.schedule(new EndDealRunnable(notice), notice.getDueTo());
            } else {
                String previousUserEmail = currentPrice.map(CurrentPrice::getCurrentEmail)
                        .orElse(notice.getUser().getEmail());
                notificationService.notifyPriceOutdated(previousUserEmail, notice);
            }
        } finally {
            IdConcurrentLock.unlock(IdConcurrentLock.BET_LOCK + noticeId);
        }
    }

    public class EndDealRunnable implements Runnable {
        private final Notice notice;

        public EndDealRunnable(Notice notice) {
            this.notice = notice;
        }

        @Override
        public void run() {
            noticeService.deactivate(notice.getId());

            CurrentPrice currentPrice = currentPriceService.getByNoticeId(notice.getId())
                    .orElseThrow(() -> {
                        String message = "Current price should exist";
                        logger.error(message);
                        throw new IllegalArgumentException(message);
                    });

            notificationService.notifySold(notice, currentPrice);
        }
    }
}
