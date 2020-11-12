package unittest;

import com.example.demo.FriendManagementApplication;
import com.example.demo.model.Email;
import com.example.demo.model.FriendRelationship;
import com.example.demo.payload.AddAndGetCommonRequest;
import com.example.demo.payload.EmailRequest;
import com.example.demo.repository.EmailRepository;
import com.example.demo.repository.FriendRelationshipRepository;
import com.example.demo.service.EmailService;
import com.example.demo.service.serviceimp.EmailServiceImp;
import com.example.demo.utils.FriendStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.AssertTrue;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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

    @InjectMocks
    private EmailRequest emailRequest;
    @InjectMocks
    private AddAndGetCommonRequest addCommonRequest;

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
    private FriendRelationship blockRelationship1;
    private FriendRelationship subscribeRelationship1;
    private Map<String, Object> body;
    private ResponseEntity<Map<String, Object>> responseEntity;

    @Before
    public void setUp() {
        emailRequest = new EmailRequest();
        addCommonRequest = new AddAndGetCommonRequest();
        emailTest1 = new Email(1L, "hau@gmail.com");
        emailTest2 = new Email(2L, "hoang@gmail.com");
        emailTest3 = new Email(3L, "quan@gmail.com");
        emailTest4 = new Email(4L, "binh@gmail.com");
        emailTest5 = new Email(5L, "binh@gmail.com");
        emailTest5 = new Email(5L, "khang@gmail.com");
        friendRelationship1 = new FriendRelationship(1L, 1L, 2L, String.valueOf(FriendStatus.FRIEND), emailTest1, emailTest2);
        friendRelationship2 = new FriendRelationship(1L, 1L, 3L, String.valueOf(FriendStatus.FRIEND), emailTest1, emailTest2);
        blockRelationship1 = new FriendRelationship(1L, 4L, String.valueOf(FriendStatus.BLOCK));
        subscribeRelationship1 = new FriendRelationship(1L, 5L, String.valueOf(FriendStatus.SUBSCRIBE));
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
        when(emailRepository.findById(emailTest2.getEmailId())).thenReturn(Optional.of(emailTest2));
        when(emailRepository.findById(emailTest3.getEmailId())).thenReturn(Optional.of(emailTest3));
        listEmail = listRelationships.stream()
                .map(i -> i.getFriendEmail().getEmail())
                .collect(Collectors.toList());
        emailRequest = new EmailRequest(emailTest1.getEmail());
        responseEntity = emailService.getFriendList(emailRequest);

        List<String> emails = (List<String>) responseEntity.getBody().get("friends");
        assertTrue(responseEntity.getBody().get("success") == "true");
        assertTrue(responseEntity.getBody().get("count") == (Integer) listRelationships.size());
        assertTrue(emails.contains(emailTest2.getEmail()));
        assertTrue(emails.contains(emailTest3.getEmail()));
    }

    @Test
    public void testAddFriendsSuccess() {
        addCommonRequest = new AddAndGetCommonRequest
                (Arrays.asList(emailTest1.getEmail(),emailTest2.getEmail()));
        when(emailRepository.findByEmail(emailTest1.getEmail())).thenReturn(Optional.of(emailTest1));
        when(emailRepository.findByEmail(emailTest2.getEmail())).thenReturn(Optional.of(emailTest2));
        when(relationshipRepository.findByEmailIdAndFriendId
                (emailTest1.getEmailId(), emailTest2.getEmailId()))
                .thenReturn(Optional.empty());
        when(relationshipRepository.save(any())).thenReturn(friendRelationship1);
        when(relationshipRepository.save(any())).thenReturn(friendRelationship1);
        responseEntity = emailService.addFriend(addCommonRequest);

        List<String> emails = (List<String>) responseEntity.getBody().get("friends");
        assertTrue(responseEntity.getBody().get("success") == "true");

    }
}
