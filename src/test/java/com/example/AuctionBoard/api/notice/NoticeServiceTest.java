package com.example.AuctionBoard.api.notice;

import com.example.AuctionBoard.TestUserDetails;
import com.example.AuctionBoard.api.currentPrice.CurrentPrice;
import com.example.AuctionBoard.api.currentPrice.CurrentPriceService;
import com.example.AuctionBoard.api.image.DBImage;
import com.example.AuctionBoard.api.image.DBImageService;
import com.example.AuctionBoard.api.notification.NotificationService;
import com.example.AuctionBoard.api.user.User;
import com.example.AuctionBoard.api.user.UserService;
import com.example.AuctionBoard.configs.SecurityConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;

import static org.junit.jupiter.api.Assertions.assertThrows;


@Import(SecurityConfig.class)
@ExtendWith(MockitoExtension.class)
public class NoticeServiceTest {
    public static final String TEST_EMAIL = "test@email.com";
    public static final String TEST_PASS = "password";
    public static final User CONTEXT_USER = new User(TEST_EMAIL, TEST_PASS);

    public static final String TEST_EMAIL1 = "test1@email.com";
    public static final String TEST_PASS1 = "password1";
    public static final User TEST_USER1 = new User(TEST_EMAIL1, TEST_PASS1);

    private static final DBImage EMPTY_IMAGE = new DBImage(1L, "", new byte[0]);
    @Mock
    private NoticeRepository noticeRepository;
    @Mock
    private DBImageService dbImageService;
    @Mock
    private UserService userService;
    @Mock
    private CurrentPriceService currentPriceService;
    @Mock
    private TaskScheduler taskScheduler;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NoticeServiceImpl noticeService;

    private final MockMultipartFile mockMultipartFile = new MockMultipartFile("file", new byte[]{});

    @Test
    void getAllNotices() {
        //Given
        Page<Notice> noticesPage = Mockito.mock(Page.class);
        PageRequest pageRequest = PageRequest.of(0, 3);
        List<Notice> notices = List.of(
                new Notice("test", "test description", 100),
                new Notice("test1", "test1 description", 1000),
                new Notice("test2", "test2 description", 10000));

        Mockito.when(noticeRepository.findByActiveIsTrue(pageRequest)).thenReturn(noticesPage);
        Mockito.when(noticesPage.getContent()).thenReturn(notices);

        //When
        Collection<Notice> returned = noticeService.getAllActive(0, notices.size());

        //Then
        Assertions.assertEquals(notices.size(), returned.size());
    }


    @Nested
    class AuthorizedRequestTests {

        @BeforeEach
        void beforeEach() {
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    new TestUserDetails(),
                    null,
                    Collections.emptyList());

            SecurityContext securityContext = Mockito.mock(SecurityContext.class);
            Mockito.lenient().when(securityContext.getAuthentication()).thenReturn(auth);

            SecurityContextHolder.setContext(securityContext);
        }

        @Test
        void createNoticeWithoutTimeSuccess() {
            //Given
            Notice newNotice = new Notice("test", "test description", 100);

            Mockito.when(dbImageService.saveFromMultipart(mockMultipartFile)).thenReturn(EMPTY_IMAGE);
            Mockito.when(userService.getByEmail(TEST_EMAIL)).thenReturn(CONTEXT_USER);
            Mockito.when(noticeRepository.save(newNotice)).thenReturn(newNotice);

            //When
            Notice notice = noticeService.upsert(newNotice, mockMultipartFile);

            //Then
            Assertions.assertTrue(notice.isActive());
            Assertions.assertNull(notice.getCurrentPrice());
        }

        @Test
        void authorBetException() {
            //Given
            Notice existedNotice = new Notice("test", "test description", 100);
            existedNotice.setId(1L);
            existedNotice.setActive(true);
            existedNotice.setUser(CONTEXT_USER); //author

            Mockito.when(noticeRepository.findById(1L)).thenReturn(Optional.of(existedNotice));

            //When
            ResponseStatusException exception =
                    assertThrows(ResponseStatusException.class, () -> noticeService.bet(1L, 105));

            //Then
            Assertions.assertEquals("User cannot buy his own stuff", exception.getReason());
        }

        @Test
        void inactiveBetException() {
            //Given
            Notice existedNotice = new Notice("test", "test description", 100);
            existedNotice.setId(1L);
            existedNotice.setActive(false);
            existedNotice.setUser(TEST_USER1); //author

            Mockito.when(noticeRepository.findById(1L)).thenReturn(Optional.of(existedNotice));

            //When
            ResponseStatusException exception =
                    assertThrows(ResponseStatusException.class, () -> noticeService.bet(1L, 105));

            //Then
            Assertions.assertEquals("Notice is already inactive", exception.getReason());
        }

        @Test
        void firstBetSuccess() {
            //Given
            Notice existedNotice = new Notice("test", "test description", 100);
            existedNotice.setId(1L);
            existedNotice.setActive(true);
            existedNotice.setDueTo(Instant.now().plus(1, ChronoUnit.DAYS));
            existedNotice.setUser(TEST_USER1); //author

            Mockito.when(noticeRepository.findById(1L)).thenReturn(Optional.of(existedNotice));
            Mockito.when(currentPriceService.getByNoticeId(1L)).thenReturn(Optional.empty());

            ArgumentCaptor<CurrentPrice> newCurrentPrice = ArgumentCaptor.forClass(CurrentPrice.class);
            Mockito.when(currentPriceService.save(newCurrentPrice.capture()))
                    .thenReturn(new CurrentPrice(null, null, null, null)); //does nothing

            Mockito.when(taskScheduler.schedule(Mockito.any(NoticeServiceImpl.EndDealRunnable.class), Mockito.any(Instant.class)))
                    .thenReturn(Mockito.mock(ScheduledFuture.class)); //does nothing

            //When
            noticeService.bet(1L, 105);

            //Then
            Assertions.assertEquals(1L, newCurrentPrice.getValue().getNotice().getId());
            Assertions.assertNull(newCurrentPrice.getValue().getId());
            Assertions.assertEquals(CONTEXT_USER.getEmail(), newCurrentPrice.getValue().getCurrentEmail());
            Assertions.assertEquals(105, newCurrentPrice.getValue().getCurrentPrice());
        }

        @Test
        void updateBetSuccess() {
            //Given
            Notice existedNotice = new Notice("test", "test description", 100);
            existedNotice.setId(1L);
            existedNotice.setActive(true);
            existedNotice.setUser(TEST_USER1); //author

            CurrentPrice existedCurrentPrice = new CurrentPrice(
                    1L,
                    existedNotice,
                    105,
                    CONTEXT_USER.getEmail());

            Mockito.when(noticeRepository.findById(1L)).thenReturn(Optional.of(existedNotice));
            Mockito.when(currentPriceService.getByNoticeId(1L)).thenReturn(Optional.of(existedCurrentPrice));

            ArgumentCaptor<CurrentPrice> newCurrentPrice = ArgumentCaptor.forClass(CurrentPrice.class);
            Mockito.when(currentPriceService.save(newCurrentPrice.capture())).thenReturn(existedCurrentPrice); //does nothing

            Mockito.doNothing().when(notificationService).notifyPriceOutdated(CONTEXT_USER.getEmail(), existedNotice);

            //When
            noticeService.bet(1L, 110);

            //Then
            Assertions.assertEquals(1L, newCurrentPrice.getValue().getNotice().getId());
            Assertions.assertEquals(1L, newCurrentPrice.getValue().getId());
            Assertions.assertEquals(CONTEXT_USER.getEmail(), newCurrentPrice.getValue().getCurrentEmail());
            Assertions.assertEquals(110, newCurrentPrice.getValue().getCurrentPrice());
        }

        @Test
        void updateWithEqualBetException() {
            //Given
            Notice existedNotice = new Notice("test", "test description", 100);
            existedNotice.setId(1L);
            existedNotice.setActive(true);
            existedNotice.setUser(TEST_USER1); //author

            CurrentPrice existedCurrentPrice = new CurrentPrice(
                    1L,
                    existedNotice,
                    105,
                    CONTEXT_USER.getEmail());

            Mockito.when(noticeRepository.findById(1L)).thenReturn(Optional.of(existedNotice));
            Mockito.when(currentPriceService.getByNoticeId(1L)).thenReturn(Optional.of(existedCurrentPrice));

            //When
            ResponseStatusException exception =
                    assertThrows(ResponseStatusException.class, () -> noticeService.bet(1L, 105));

            //Then
            Assertions.assertEquals("Cannot lower the price", exception.getReason());
        }

        @Test
        void updateWithLessBetException() {
            //Given
            Notice existedNotice = new Notice("test", "test description", 100);
            existedNotice.setId(1L);
            existedNotice.setActive(true);
            existedNotice.setUser(TEST_USER1); //author

            CurrentPrice existedCurrentPrice = new CurrentPrice(
                    1L,
                    existedNotice,
                    105,
                    CONTEXT_USER.getEmail());

            Mockito.when(noticeRepository.findById(1L)).thenReturn(Optional.of(existedNotice));
            Mockito.when(currentPriceService.getByNoticeId(1L)).thenReturn(Optional.of(existedCurrentPrice));

            //When
            ResponseStatusException exception =
                    assertThrows(ResponseStatusException.class, () -> noticeService.bet(1L, 90));

            //Then
            Assertions.assertEquals("Cannot lower the price", exception.getReason());
        }
    }
}
