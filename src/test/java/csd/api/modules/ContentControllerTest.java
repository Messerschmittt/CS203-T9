// package csd.api.modules;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.junit.jupiter.api.Assertions.assertNull;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.when;

// import java.util.ArrayList;
// import java.util.List;
// import java.util.Optional;

// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.security.authentication.TestingAuthenticationToken;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// import csd.api.tables.*;
// import csd.api.tables.templates.AccountRecord;
// import csd.api.modules.user.*;
// import csd.api.modules.account.*;
// import csd.api.modules.content.*;

// @ExtendWith (MockitoExtension.class)
// public class ContentControllerTest {
//     @Mock
//     private ContentRepository contents;

//     @InjectMocks
//     private ContentController contentController;


//     @Test
//     void getContents_Approved() {
//         Authentication newAuth = new TestingAuthenticationToken(null, null, new String[]{"ROLE_USER"});
//         Content newContent = new Content();
//         ArrayList<Content> list = new ArrayList<>();
//         newContent.setApproved(true);
//         list.add(newContent);

//         when(contents.findAll()).thenReturn(list);

//         ArrayList<Content> result = new ArrayList<>(contentController.getContents(newAuth));

//         assertEquals(result.size(), 1);
//     }

//     @Test
//     void getContents_NonApproved_User() {
//         Authentication newAuth = new TestingAuthenticationToken(null, null, new String[]{"ROLE_USER"});
//         Content newContent = new Content();
//         ArrayList<Content> list = new ArrayList<>();
//         newContent.setApproved(false);
//         list.add(newContent);

//         when(contents.findAll()).thenReturn(list);

//         ArrayList<Content> result = new ArrayList<>(contentController.getContents(newAuth));

//         assertEquals(result.size(), 0);
//     }

//     @Test
//     void getContents_NonApproved_Manager() {
//         Authentication newAuth = new TestingAuthenticationToken(null, null, new String[]{"ROLE_MANAGER"});
//         Content newContent = new Content();
//         ArrayList<Content> list = new ArrayList<>();
//         newContent.setApproved(false);
//         list.add(newContent);

//         when(contents.findAll()).thenReturn(list);

//         ArrayList<Content> result = new ArrayList<>(contentController.getContents(newAuth));

//         assertEquals(result.size(), 1);
//     }

// }
