package com.example.AuctionBoard.api.notice;

import com.example.AuctionBoard.Utils.ContextUtils;
import com.example.AuctionBoard.api.image.*;
import com.example.AuctionBoard.api.user.User;
import com.example.AuctionBoard.api.user.UserService;
import com.example.AuctionBoard.configs.SchedulingConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;

@Service
public class NoticeServiceImpl implements NoticeService {
    private final NoticeRepository noticeRepository;
    private final DBImageService imageService;
    private final UserService userService;

    @Autowired
    public NoticeServiceImpl(NoticeRepository noticeRepository,
                             DBImageService imageService,
                             UserService userService) {
        this.noticeRepository = noticeRepository;
        this.imageService = imageService;
        this.userService = userService;
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
        Notice oldNotice = getById(newNotice.getId());
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
}
