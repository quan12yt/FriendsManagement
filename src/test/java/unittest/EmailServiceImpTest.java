package unittest;

import com.example.demo.FriendManagementApplication;
import com.example.demo.model.Email;
import com.example.demo.model.FriendRelationship;
import com.example.demo.payload.AddAndGetCommonRequest;
import com.example.demo.payload.EmailRequest;
import com.example.demo.payload.RetrieveRequest;
import com.example.demo.payload.SubscribeAndBlockRequest;
import com.example.demo.repository.EmailRepository;
import com.example.demo.repository.FriendRelationshipRepository;
import com.example.demo.service.serviceimp.EmailServiceImp;
import com.example.demo.utils.FriendStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = FriendManagementApplication.class)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@Transactional
public class EmailServiceImpTest {

    @InjectMocks
    private EmailServiceImp emailService;
    @Mock
    private EmailRepository emailRepository;
    @Mock
    private FriendRelationshipRepository relationshipRepository;

    private EmailRequest emailRequest;
    private AddAndGetCommonRequest addCommonRequest;
    private SubscribeAndBlockRequest subBlockRequest;
    private RetrieveRequest retrieveRequest;

    private List<FriendRelationship> listRelationships;
    private List<String> listEmail;

    private Email emailTest1;
    private Email emailTest2;
    private Email emailTest3;
    private Email emailTest4;
    private Email emailTest5;
    private Email emailTest6;

    private FriendRelationship friendRelationship1;
    private FriendRelationship friendRelationship2;
    private FriendRelationship friendRelationship3;
    private FriendRelationship friendRelationship4;
    private FriendRelationship blockRelationship1;
    private FriendRelationship subscribeRelationship1;
    private Map<String, Object> body;
    private ResponseEntity<Map<String, Object>> responseEntity;

    @Before
    public void setUp() {
        emailRequest = new EmailRequest();
        addCommonRequest = new AddAndGetCommonRequest();
        subBlockRequest = new SubscribeAndBlockRequest();
        retrieveRequest = new RetrieveRequest();

        emailTest1 = new Email(1L, "hau@gmail.com");
        emailTest2 = new Email(2L, "hoang@gmail.com");
        emailTest3 = new Email(3L, "quan@gmail.com");
        emailTest4 = new Email(4L, "binh@gmail.com");
        emailTest5 = new Email(5L, "lan@gmail.com");
        emailTest6 = new Email(5L, "khang@gmail.com");
        friendRelationship1 = new FriendRelationship(1L, 1L, 2L, String.valueOf(FriendStatus.FRIEND), emailTest1, emailTest2);
        friendRelationship2 = new FriendRelationship(2L, 1L, 3L, String.valueOf(FriendStatus.FRIEND), emailTest1, emailTest3);
        friendRelationship3 = new FriendRelationship(3L, 2L, 1L, String.valueOf(FriendStatus.FRIEND), emailTest2, emailTest1);
        friendRelationship4 = new FriendRelationship(4L, 3L, 1L, String.valueOf(FriendStatus.FRIEND), emailTest3, emailTest1);
        blockRelationship1 = new FriendRelationship(5L, 5L, 4L, String.valueOf(FriendStatus.BLOCK), emailTest5, emailTest4);
        subscribeRelationship1 = new FriendRelationship(6L, 6L, 5L, String.valueOf(FriendStatus.SUBSCRIBE), emailTest6, emailTest5);

        listRelationships = new ArrayList<>();
        listEmail = new ArrayList<>();
        body = new HashMap<>();
    }

    @Test
    public void testGetFriendsSuccess() {
        when(emailRepository.findByEmail(emailTest1.getEmail())).thenReturn(Optional.of(emailTest1));
        listRelationships = Arrays.asList(friendRelationship1, friendRelationship2);

        when(relationshipRepository.findByEmailIdAndStatus
                (emailTest1.getEmailId(), String.valueOf(FriendStatus.FRIEND)))
                .thenReturn(listRelationships);
        when(emailRepository.findById(emailTest2.getEmailId()))
                .thenReturn(Optional.of(emailTest2));
        when(emailRepository.findById(emailTest3.getEmailId()))
                .thenReturn(Optional.of(emailTest3));

        listEmail = listRelationships.stream()
                .map(i -> i.getFriendEmail().getEmail())
                .collect(Collectors.toList());
        emailRequest = new EmailRequest(emailTest1.getEmail());
        responseEntity = emailService.getFriendList(emailRequest);

        List<String> emails = (List<String>) responseEntity.getBody().get("friends");
        assertSame("true", responseEntity.getBody().get("success"));
        assertSame(responseEntity.getBody().get("count"), listRelationships.size());
        assertTrue(emails.contains(emailTest2.getEmail()));
        assertTrue(emails.contains(emailTest3.getEmail()));
    }

    @Test
    public void testAddFriendsSuccess() {
        addCommonRequest = new AddAndGetCommonRequest
                (Arrays.asList(emailTest1.getEmail(), emailTest2.getEmail()));
        when(emailRepository.findByEmail(emailTest1.getEmail()))
                .thenReturn(Optional.of(emailTest1));
        when(emailRepository.findByEmail(emailTest2.getEmail()))
                .thenReturn(Optional.of(emailTest2));
        when(relationshipRepository.findByEmailIdAndFriendId
                (emailTest1.getEmailId(), emailTest2.getEmailId()))
                .thenReturn(Optional.empty());
        responseEntity = emailService.addFriend(addCommonRequest);

        assertSame("true", responseEntity.getBody().get("success"));

    }

    @Test
    public void testGetCommonSuccess() {
        addCommonRequest = new AddAndGetCommonRequest
                (Arrays.asList(emailTest2.getEmail(), emailTest3.getEmail()));
        listEmail = Collections.singletonList(emailTest1.getEmail());

        when(emailRepository.findByEmail(emailTest2.getEmail()))
                .thenReturn(Optional.of(emailTest2));
        when(emailRepository.findByEmail(emailTest3.getEmail()))
                .thenReturn(Optional.of(emailTest3));
        when(relationshipRepository.findByEmailIdAndStatus
                (emailTest2.getEmailId(), String.valueOf(FriendStatus.FRIEND)))
                .thenReturn(Collections.singletonList(friendRelationship3));
        when(relationshipRepository.findByEmailIdAndStatus
                (emailTest3.getEmailId(), String.valueOf(FriendStatus.FRIEND))).
                thenReturn(Collections.singletonList(friendRelationship4));
        when(emailRepository.findById(emailTest1.getEmailId()))
                .thenReturn(Optional.of(emailTest1));

        responseEntity = emailService.getCommonFriends(addCommonRequest);
        List<String> emails = (List<String>) responseEntity.getBody().get("friends");

        assertSame("true", responseEntity.getBody().get("success"));
        assertSame(responseEntity.getBody().get("count"), listEmail.size());
        assertTrue(emails.contains(emailTest1.getEmail()));

    }

    @Test
    public void testSubscribeSuccess() {
        subBlockRequest = new SubscribeAndBlockRequest(emailTest6.getEmail(), emailTest5.getEmail());

        when(emailRepository.findByEmail(emailTest6.getEmail())).thenReturn(Optional.of(emailTest6));
        when(emailRepository.findByEmail(emailTest5.getEmail())).thenReturn(Optional.of(emailTest5));
        when(relationshipRepository.findByEmailIdAndFriendId(emailTest6.getEmailId(), emailTest5.getEmailId()))
                .thenReturn(Optional.empty());

        responseEntity = emailService.subscribeTo(subBlockRequest);
        assertSame("true", responseEntity.getBody().get("success"));
    }

    @Test
    public void testBlockEmailSuccess() {
        subBlockRequest = new SubscribeAndBlockRequest(emailTest5.getEmail(), emailTest4.getEmail());

        when(emailRepository.findByEmail(emailTest5.getEmail())).thenReturn(Optional.of(emailTest5));
        when(emailRepository.findByEmail(emailTest4.getEmail())).thenReturn(Optional.of(emailTest4));
        when(relationshipRepository.findByEmailIdAndFriendId(emailTest5.getEmailId(), emailTest4.getEmailId()))
                .thenReturn(Optional.empty());

        responseEntity = emailService.blockEmail(subBlockRequest);
        assertSame("true", responseEntity.getBody().get("success"));
    }

    @Test
    public void testRetrieveEmailSuccess() {
        retrieveRequest = new RetrieveRequest(emailTest1.getEmail(), "Hello " + emailTest5.getEmail());
        listEmail = Arrays.asList(emailTest2.getEmail(), emailTest3.getEmail(), emailTest5.getEmail());

        when(emailRepository.findByEmail(emailTest1.getEmail())).thenReturn(Optional.of(emailTest1));
        when(emailRepository.findByEmail(emailTest5.getEmail())).thenReturn(Optional.of(emailTest5));
        when(relationshipRepository.findByEmailId(emailTest1.getEmailId())).thenReturn(Arrays.asList(friendRelationship1, friendRelationship2));
        when(emailRepository.findById(emailTest2.getEmailId())).thenReturn(Optional.of(emailTest2));
        when(emailRepository.findById(emailTest3.getEmailId())).thenReturn(Optional.of(emailTest3));

        responseEntity = emailService.retrieveEmails(retrieveRequest);
        Set<String> emails = (Set<String>) responseEntity.getBody().get("recipients");
        assertSame("true", responseEntity.getBody().get("success"));
        assertTrue(emails.contains(listEmail.get(0)));
        assertTrue(emails.contains(listEmail.get(1)));
        assertTrue(emails.contains(listEmail.get(2)));

    }
}
