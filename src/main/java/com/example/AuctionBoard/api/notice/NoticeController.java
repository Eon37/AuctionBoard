package com.example.AuctionBoard.api.notice;

import com.example.AuctionBoard.Utils.JSONUtils;
import com.example.AuctionBoard.api.deal.DealService;
import com.example.AuctionBoard.configs.ServicePathConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    @Operation(summary = "Get notices",
            description = "Get list of notices of specified size on specified page",
            responses = {
            @ApiResponse(
                    description = "List of notices",
                    responseCode = "200",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Notice.class))))
    })
    @GetMapping(path = {"/", ServicePathConstants.NOTICE_SERVICE })
    public Collection<Notice> getAll(@Parameter(name = "pageNo",
                                                description = "The number of page to view")
                                     @RequestParam(defaultValue = "0") int pageNo,

                                     @Parameter(name = "pageSize",
                                                description = "The size of notices page")
                                     @RequestParam(defaultValue = "10") int pageSize) {
        return noticeService.getAllActive(pageNo, pageSize);
    }

    @Operation(summary = "Get the notice",
            description = "Get the notice by id",
            responses = {
                    @ApiResponse(
                            description = "Notice",
                            responseCode = "200",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Notice.class))),
                    @ApiResponse(
                            description = "Notice not found",
                            responseCode = "404")
            })
    @GetMapping(path = ServicePathConstants.NOTICE_SERVICE + "/{id}")
    public Notice get(@Parameter(name = "id",
                                description = "The id of the notice to get",
                                required = true)
                      @PathVariable Long id) {
        return noticeService.getById(id);
    }

    @Operation(summary = "Create or update the given notice",
            description = "Create or update the notice with the full notice body and an image",
            responses = {
                    @ApiResponse(
                            description = "Create/update successful",
                            responseCode = "200",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Notice.class))),
                    @ApiResponse(
                            description = "Unauthorized access",
                            responseCode = "401"),
                    @ApiResponse(
                            description = "User not found",
                            responseCode = "404"),
                    @ApiResponse(
                            description = "Forbidden to create/update notice if not authorized",
                            responseCode = "403"),
                    @ApiResponse(
                            description = "Notice not found while updating",
                            responseCode = "404")
            })
    @PostMapping(path = ServicePathConstants.NOTICE_SERVICE,
                 consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public Notice save(@Parameter(name = "notice",
                                  description = "The json string of the notice",
                                  required = true,
                                  content = @Content(mediaType = "multipart/form-data", schema = @Schema(implementation = Notice.class)))
                       @RequestPart("notice") String notice,

                       @Parameter(name = "image",
                                  description = "The image file to attach to the notice",
                                  required = true,
                                  content = @Content(mediaType = "multipart/form-data"))
                       @RequestPart("image") MultipartFile image) {
        return noticeService.save(JSONUtils.fromString(notice, Notice.class), image);
    }

    @Operation(summary = "Delete notice",
            description = "Delete or deactivate the notice",
            responses = {
                    @ApiResponse(
                            description = "Delete successful",
                            responseCode = "200"),
                    @ApiResponse(
                            description = "Notice not found while updating",
                            responseCode = "404"),
                    @ApiResponse(
                            description = "Unauthorized access",
                            responseCode = "401"),
                    @ApiResponse(
                            description = "Forbidden to delete notice current user did not create",
                            responseCode = "403")
            })
    @DeleteMapping(path = ServicePathConstants.NOTICE_SERVICE + "/{id}")
    public void delete(@Parameter(name = "id",
                                 description = "The id of the notice to delete",
                                 required = true)
                       @PathVariable Long id,

                       @Parameter(name = "wipe",
                               description = "Whether to remove entirely or not")
                       @RequestParam(defaultValue = "false") boolean wipe) {
        noticeService.delete(id, wipe);
    }

    @Operation(summary = "Make a bet",
            description = "Intent to buy by making a bet",
            responses = {
                    @ApiResponse(
                            description = "Bet successful",
                            responseCode = "200"),
                    @ApiResponse(
                            description = "Concurrent price update attempt",
                            responseCode = "500"),
                    @ApiResponse(
                            description = "Attempt to bet on inactive notice",
                            responseCode = "400"),
                    @ApiResponse(
                            description = "Attempt to bet on user's own notice",
                            responseCode = "400"),
                    @ApiResponse(
                            description = "Attempt to set price lower or equal than current",
                            responseCode = "400"),
                    @ApiResponse(
                            description = "Unauthorized access",
                            responseCode = "401")
            })
    @PostMapping(path = ServicePathConstants.NOTICE_SERVICE + "/{id}/bet")
    public void bet(@Parameter(name = "id",
                               description = "The id of the notice to bet on",
                               required = true)
                    @PathVariable Long id,

                    @Parameter(name = "newPrice",
                               description = "The new price to set",
                               required = true)
                    @RequestParam Integer newPrice) {
        dealService.bet(id, newPrice);
    }
}
