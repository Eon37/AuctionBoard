package com.example.AuctionBoard.api.notice;

import com.example.AuctionBoard.Utils.JSONUtils;
import com.example.AuctionBoard.api.deal.DealService;
import com.example.AuctionBoard.configs.ServicePathConstants;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;

@RestController
public class NoticeController {
    private final NoticeService noticeService;
    private final DealService dealService;

    public NoticeController(NoticeService noticeService, DealService dealService) {
        this.noticeService = noticeService;
        this.dealService = dealService;
    }

    @GetMapping(path = {"/", ServicePathConstants.NOTICE_SERVICE })
    public Collection<Notice> getAll(@RequestParam(defaultValue = "0") int pageNo,
                                     @RequestParam(defaultValue = "10") int pageSize) {
        return noticeService.getAllActive(pageNo, pageSize);
    }

    @GetMapping(path = ServicePathConstants.NOTICE_SERVICE + "/{id}")
    public Notice get(@PathVariable Long id) {
        return noticeService.getById(id);
    }

    @PostMapping(path = ServicePathConstants.NOTICE_SERVICE,
                 consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public Notice upsert(@RequestPart("notice") String notice,
                            @RequestPart("image") MultipartFile image) {
        return noticeService.upsert(JSONUtils.fromString(notice, Notice.class), image);
    }

    @DeleteMapping(path = ServicePathConstants.NOTICE_SERVICE + "/{id}")
    public void delete(@PathVariable Long id, @RequestParam(defaultValue = "false") boolean wipe) {
        noticeService.delete(id);
    }

    @PostMapping(path = ServicePathConstants.NOTICE_SERVICE + "/{id}/bet")
    public void bet(@PathVariable Long id, @RequestParam Integer newPrice) {
        dealService.bet(id, newPrice);
    }
}
