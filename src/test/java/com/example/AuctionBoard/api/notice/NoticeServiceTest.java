package com.example.AuctionBoard.api.notice;

import com.example.AuctionBoard.TestUserDetails;
import com.example.AuctionBoard.api.image.DBImage;
import com.example.AuctionBoard.api.image.DBImageService;
import com.example.AuctionBoard.api.user.User;
import com.example.AuctionBoard.api.user.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class NoticeServiceTest {
    public static final String TEST_EMAIL = "test@email.com";
    public static final String TEST_PASS = "password";
    public static final User CONTEXT_USER = new User(TEST_EMAIL, TEST_PASS);

    private static final DBImage EMPTY_IMAGE = new DBImage(1L, "", new byte[0]);

    @Mock
    private NoticeRepository noticeRepository;
    @Mock
    private DBImageService dbImageService;
    @Mock
    private UserService userService;
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
    }
}
