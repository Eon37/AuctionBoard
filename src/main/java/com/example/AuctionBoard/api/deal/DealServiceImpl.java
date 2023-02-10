package com.example.AuctionBoard.api.deal;

import com.example.AuctionBoard.Utils.ContextUtils;
import com.example.AuctionBoard.api.currentPrice.CurrentPrice;
import com.example.AuctionBoard.api.currentPrice.CurrentPriceService;
import com.example.AuctionBoard.api.notice.Notice;
import com.example.AuctionBoard.api.notice.NoticeService;
import com.example.AuctionBoard.api.notification.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

public class DealServiceImpl implements DealService {
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
    public void bet(Long noticeId, Integer newPrice) { //todo multithreading
        Notice notice = noticeService.getById(noticeId);

        if (!notice.isActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Notice is already inactive");
        }

        String newUserEmail = ContextUtils.getSpringContextUserOrThrow().getUsername();

        if (newUserEmail.equals(notice.getUser().getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User cannot buy his own stuff");
        }

        Optional<CurrentPrice> currentPrice = currentPriceService.getByNoticeId(noticeId);

        if (currentPrice.isPresent() && newPrice <= currentPrice.get().getCurrentPrice()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot lower the price");
        }

        CurrentPrice updateCurrentPrice = new CurrentPrice(
                currentPrice.map(CurrentPrice::getId).orElse(null),
                notice,
                newPrice,
                newUserEmail);

        currentPriceService.save(updateCurrentPrice);

        if (currentPrice.isEmpty()) {
            taskScheduler.schedule(new EndDealRunnable(notice), notice.getDueTo());
        } else {
            String previousUserEmail = currentPrice.map(CurrentPrice::getCurrentEmail).orElse(notice.getUser().getEmail());
            notificationService.notifyPriceOutdated(previousUserEmail, notice);
        }
    }

    public class EndDealRunnable implements Runnable {
        private final Notice notice;

        public EndDealRunnable(Notice notice) {
            this.notice = notice;
        }

        @Override
        public void run() {
            //todo stop price change
            noticeService.deactivate(notice.getId()); //todo synch

            CurrentPrice currentPrice = currentPriceService.getByNoticeId(notice.getId())
                    .orElseThrow(() -> { throw new IllegalArgumentException(); });

            notificationService.notifySold(notice, currentPrice);
        }
    }
}
