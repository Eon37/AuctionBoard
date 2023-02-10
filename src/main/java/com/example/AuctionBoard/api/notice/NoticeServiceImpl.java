package com.example.AuctionBoard.api.notice;

import com.example.AuctionBoard.Utils.ContextUtils;
import com.example.AuctionBoard.api.currentPrice.CurrentPrice;
import com.example.AuctionBoard.api.currentPrice.CurrentPriceService;
import com.example.AuctionBoard.api.image.*;
import com.example.AuctionBoard.api.notification.NotificationService;
import com.example.AuctionBoard.api.user.User;
import com.example.AuctionBoard.api.user.UserService;
import com.example.AuctionBoard.configs.SchedulingConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Optional;

@Service
public class NoticeServiceImpl implements NoticeService {
    private final NoticeRepository noticeRepository;
    private final DBImageService imageService; //todo facade?
    private final UserService userService;
    private final CurrentPriceService currentPriceService;
    private final TaskScheduler taskScheduler;
    private final NotificationService notificationService;

    @Autowired
    public NoticeServiceImpl(NoticeRepository noticeRepository,
                             DBImageService imageService,
                             UserService userService,
                             CurrentPriceService currentPriceService,
                             TaskScheduler taskScheduler,
                             NotificationService notificationService) {
        this.noticeRepository = noticeRepository;
        this.imageService = imageService;
        this.userService = userService;
        this.currentPriceService = currentPriceService;
        this.taskScheduler = taskScheduler;
        this.notificationService = notificationService;
    }

    public Collection<Notice> getAllActive(int pageNo, int pageSize) {
        return noticeRepository.findByActiveIsTrue(PageRequest.of(pageNo, pageSize)).getContent();
    }

    @Override
    public Notice getById(Long id) {
        return noticeRepository.findById(id)
                .orElseThrow(() -> { throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Notice not found"); });
    }

    @Override
    public Notice upsert(Notice notice, MultipartFile image) {
        return notice.getId() == null ? create(notice, image) : update(notice, image);
    }

    private Notice create(Notice notice, MultipartFile image) {
        DBImage newImage = imageService.saveFromMultipart(image);

        String userEmail = ContextUtils.getSpringContextUserOrThrow().getUsername();
        User user = userService.getByEmail(userEmail);

        notice.setImage(newImage);
        notice.setUser(user);
        notice.setActive(true);
        notice.setDueTo(notice.getDueTo() == null
                ? Instant.now().plus(SchedulingConfig.NOTIFY_AFTER_DAYS, ChronoUnit.DAYS)
                : notice.getDueTo());

        return noticeRepository.save(notice);
    }

    private Notice update(Notice newNotice, MultipartFile image) {
        Notice oldNotice = noticeRepository.findById(newNotice.getId()).get(); //todo getById()
//        Notice updatedNotice = new Notice(notice); todo
//        CurrentPrice currentPrice = currentPriceService.getByNoticeId(notice.getId()).orElse(notice.getStartingPrice());
        return noticeRepository.save(oldNotice);
    }

    @Override
    public void delete(Long id) {
        noticeRepository.deleteById(id);
    }

    @Override
    public void deactivate(Long id) {
        Notice notice = getById(id);
        notice.setActive(false);

        noticeRepository.save(notice); //todo bd side? multithreading
    }

    @Override
    public void bet(Long noticeId, Integer newPrice) { //todo multithreading
        Notice notice = getById(noticeId);

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
            deactivate(notice.getId()); //todo synch

            CurrentPrice currentPrice = currentPriceService.getByNoticeId(notice.getId())
                    .orElseThrow(() -> { throw new IllegalArgumentException(); });

            notificationService.notifySold(notice, currentPrice);
        }
    }
}
