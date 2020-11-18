package unittest;

import com.example.demo.FriendManagementApplication;
import com.example.demo.exception.WrongRequirementException;
import com.example.demo.model.Email;
import com.example.demo.model.FriendRelationship;
import com.example.demo.request.AddAndGetCommonRequest;
import com.example.demo.request.EmailRequest;
import com.example.demo.request.RetrieveRequest;
import com.example.demo.request.SubscribeAndBlockRequest;
import com.example.demo.repository.EmailRepository;
import com.example.demo.repository.FriendRelationshipRepository;
import com.example.demo.service.serviceimp.EmailServiceImp;
import com.example.demo.enums.FriendStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = FriendManagementApplication.class)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class EmailServiceImpTest {

    @Autowired
    private EmailServiceImp emailService;
    @MockBean
    private EmailRepository emailRepository;
    @MockBean
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

    @Before
    public void setUp() {
        emailRequest = new EmailRequest();
        addCommonRequest = new AddAndGetCommonRequest();
        subBlockRequest = new SubscribeAndBlockRequest();
        retrieveRequest = new RetrieveRequest();

        emailTest1 = Email.builder().emailId(1L).email("hau@gmail.com").build();
        emailTest2 = Email.builder().emailId(2L).email("hoang@gmail.com").build();
        emailTest3 = Email.builder().emailId(3L).email("quan@gmail.com").build();
        emailTest4 = Email.builder().emailId(4L).email("binh@gmail.com").build();
        emailTest5 = Email.builder().emailId(4L).email("lan@gmail.com").build();
        emailTest6 = Email.builder().emailId(5L).email("khang@gmail.com").build();

        friendRelationship1 = new FriendRelationship(1L, 1L, 2L, FriendStatus.FRIEND.name(), emailTest1, emailTest2);
        friendRelationship2 = new FriendRelationship(2L, 1L, 3L, FriendStatus.FRIEND.name(), emailTest1, emailTest3);
        friendRelationship3 = new FriendRelationship(3L, 2L, 1L, FriendStatus.FRIEND.name(), emailTest2, emailTest1);
        friendRelationship4 = new FriendRelationship(4L, 3L, 1L, FriendStatus.FRIEND.name(), emailTest3, emailTest1);
        blockRelationship1 = new FriendRelationship(5L, 5L, 4L, FriendStatus.BLOCK.name(), emailTest5, emailTest4);
        subscribeRelationship1 = new FriendRelationship(6L, 6L, 5L, FriendStatus.SUBSCRIBE.name(), emailTest6, emailTest5);

        listRelationships = new ArrayList<>();
        listEmail = new ArrayList<>();
    }

    @Test
    public void testGetFriendsSuccess() {
        when(emailRepository.findByEmail(emailTest1.getEmail())).thenReturn(Optional.of(emailTest1));
        listRelationships = Arrays.asList(friendRelationship1, friendRelationship2);

        when(relationshipRepository.findByEmailIdAndStatus
                (emailTest1.getEmailId(), FriendStatus.FRIEND.name()))
                .thenReturn(listRelationships);
        when(emailRepository.findById(emailTest2.getEmailId()))
                .thenReturn(Optional.of(emailTest2));
        when(emailRepository.findById(emailTest3.getEmailId()))
                .thenReturn(Optional.of(emailTest3));

        listEmail = listRelationships.stream()
                .map(i -> i.getFriendEmail().getEmail())
                .collect(Collectors.toList());
        emailRequest = new EmailRequest(emailTest1.getEmail());

        List<String> listResults = emailService.getFriendList(emailRequest);

        assertSame(listResults.size(), listRelationships.size());
        assertTrue(listResults.contains(emailTest2.getEmail()));
        assertTrue(listResults.contains(emailTest3.getEmail()));
    }

    @Test
    public void testGetFriendsNotExistEmail() {
        emailRequest = new EmailRequest(emailTest1.getEmail());
        when(emailRepository.findByEmail(emailTest1.getEmail())).thenReturn(Optional.empty());
        Throwable ex = assertThrows(WrongRequirementException.class, () -> emailService.getFriendList(emailRequest));
        assertSame("Email not found in database", ex.getMessage());
    }

    @Test
    public void testAddFriendSuccess() {
        addCommonRequest = new AddAndGetCommonRequest
                (Arrays.asList(emailTest1.getEmail(), emailTest2.getEmail()));
        when(emailRepository.findByEmail(emailTest1.getEmail()))
                .thenReturn(Optional.of(emailTest1));
        when(emailRepository.findByEmail(emailTest2.getEmail()))
                .thenReturn(Optional.of(emailTest2));
        when(relationshipRepository.findByEmailIdAndFriendId
                (emailTest1.getEmailId(), emailTest2.getEmailId()))
                .thenReturn(Optional.empty());
        Boolean result = emailService.addFriend(addCommonRequest);

        assertSame("true", result.toString());
    }

    @Test
    public void testAddFriendNotExistEmail() {
        addCommonRequest = new AddAndGetCommonRequest(Arrays.asList("q@gmail.com", "qw@gmail.com"));
        Throwable ex = assertThrows(WrongRequirementException.class, () -> emailService.addFriend(addCommonRequest));
        assertSame("Both emails have to be in database", ex.getMessage());
    }

    @Test
    public void testAddFriendBlockedEmail() {
        addCommonRequest = new AddAndGetCommonRequest(Arrays.asList(emailTest1.getEmail(), emailTest2.getEmail()));
        when(emailRepository.findByEmail(emailTest1.getEmail()))
                .thenReturn(Optional.of(emailTest1));
        when(emailRepository.findByEmail(emailTest2.getEmail()))
                .thenReturn(Optional.of(emailTest2));
        when(relationshipRepository.findByEmailIdAndFriendId
                (emailTest1.getEmailId(), emailTest2.getEmailId()))
                .thenReturn(Optional.ofNullable(blockRelationship1));
        Throwable ex = assertThrows(WrongRequirementException.class, () -> emailService.addFriend(addCommonRequest));
        assertSame("This email has been blocked !!", ex.getMessage());
    }

    @Test
    public void testAddFriendOldFriend() {
        addCommonRequest = new AddAndGetCommonRequest(Arrays.asList(emailTest1.getEmail(), emailTest2.getEmail()));
        when(emailRepository.findByEmail(emailTest1.getEmail()))
                .thenReturn(Optional.of(emailTest1));
        when(emailRepository.findByEmail(emailTest2.getEmail()))
                .thenReturn(Optional.of(emailTest2));
        when(relationshipRepository.findByEmailIdAndFriendId
                (emailTest1.getEmailId(), emailTest2.getEmailId()))
                .thenReturn(Optional.ofNullable(friendRelationship1));
        Throwable ex = assertThrows(WrongRequirementException.class, () -> emailService.addFriend(addCommonRequest));
        assertSame("Two Email have already being friend", ex.getMessage());
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
                (emailTest2.getEmailId(), FriendStatus.FRIEND.name()))
                .thenReturn(Collections.singletonList(friendRelationship3));
        when(relationshipRepository.findByEmailIdAndStatus
                (emailTest3.getEmailId(), FriendStatus.FRIEND.name())).
                thenReturn(Collections.singletonList(friendRelationship4));
        when(emailRepository.findById(emailTest1.getEmailId()))
                .thenReturn(Optional.of(emailTest1));

        List<String> list = emailService.getCommonFriends(addCommonRequest);
        assertSame(list.size(), listEmail.size());
        assertTrue(list.contains(emailTest1.getEmail()));

    }

    @Test
    public void testGetCommonNotExistEmail() {
        addCommonRequest = new AddAndGetCommonRequest(Arrays.asList("q@gmail.com", "qw@gmail.com"));
        Throwable ex = assertThrows(WrongRequirementException.class, () -> emailService.getCommonFriends(addCommonRequest));
        assertSame("Email not exist", ex.getMessage());
    }


    @Test
    public void testSubscribeSuccess() {
        subBlockRequest = new SubscribeAndBlockRequest(emailTest6.getEmail(), emailTest5.getEmail());

        when(emailRepository.findByEmail(emailTest6.getEmail())).thenReturn(Optional.of(emailTest6));
        when(emailRepository.findByEmail(emailTest5.getEmail())).thenReturn(Optional.of(emailTest5));
        when(relationshipRepository.findByEmailIdAndFriendId(emailTest6.getEmailId(), emailTest5.getEmailId()))
                .thenReturn(Optional.empty());
        assertSame("true", emailService.subscribeTo(subBlockRequest).toString());
    }


    @Test
    public void testSubscribeNotExistEmail() {
        subBlockRequest = new SubscribeAndBlockRequest(emailTest1.getEmail(), emailTest2.getEmail());
        when(emailRepository.findByEmail(emailTest1.getEmail())).thenReturn(Optional.empty());
        when(emailRepository.findByEmail(emailTest2.getEmail())).thenReturn(Optional.empty());

        Throwable ex = assertThrows(WrongRequirementException.class, () -> emailService.subscribeTo(subBlockRequest));
        assertSame("Requester or target email not existed", ex.getMessage());
    }

    @Test
    public void testSubscribeBlockedEmail() {
        subBlockRequest = new SubscribeAndBlockRequest(emailTest1.getEmail(), emailTest2.getEmail());
        when(emailRepository.findByEmail(emailTest1.getEmail())).thenReturn(Optional.ofNullable(emailTest1));
        when(emailRepository.findByEmail(emailTest2.getEmail())).thenReturn(Optional.ofNullable(emailTest2));
        when(relationshipRepository.findByEmailIdAndFriendId(emailTest1.getEmailId(), emailTest2.getEmailId()))
                .thenReturn(Optional.ofNullable(blockRelationship1));

        Throwable ex = assertThrows(WrongRequirementException.class, () -> emailService.subscribeTo(subBlockRequest));
        assertSame("Target email has been blocked !!", ex.getMessage());
    }

    @Test
    public void testSubscribedEmail() {
        subBlockRequest = new SubscribeAndBlockRequest(emailTest1.getEmail(), emailTest2.getEmail());
        when(emailRepository.findByEmail(emailTest1.getEmail())).thenReturn(Optional.ofNullable(emailTest1));
        when(emailRepository.findByEmail(emailTest2.getEmail())).thenReturn(Optional.ofNullable(emailTest2));
        when(relationshipRepository.findByEmailIdAndFriendId(emailTest1.getEmailId(), emailTest2.getEmailId()))
                .thenReturn(Optional.ofNullable(subscribeRelationship1));

        Throwable ex = assertThrows(WrongRequirementException.class, () -> emailService.subscribeTo(subBlockRequest));
        assertSame("Already subscribed to this target email !!", ex.getMessage());
    }

    @Test
    public void testSubscribeFriendsEmail() {
        subBlockRequest = new SubscribeAndBlockRequest(emailTest1.getEmail(), emailTest2.getEmail());
        when(emailRepository.findByEmail(emailTest1.getEmail())).thenReturn(Optional.ofNullable(emailTest1));
        when(emailRepository.findByEmail(emailTest2.getEmail())).thenReturn(Optional.ofNullable(emailTest2));
        when(relationshipRepository.findByEmailIdAndFriendId(emailTest1.getEmailId(), emailTest2.getEmailId()))
                .thenReturn(Optional.ofNullable(friendRelationship1));

        Throwable ex = assertThrows(WrongRequirementException.class, () -> emailService.subscribeTo(subBlockRequest));
        assertSame("Already being friend of this target ,no need to subscribe  !!", ex.getMessage());
    }

    @Test
    public void testBlockEmailSuccess() {
        subBlockRequest = new SubscribeAndBlockRequest(emailTest5.getEmail(), emailTest4.getEmail());

        when(emailRepository.findByEmail(emailTest5.getEmail())).thenReturn(Optional.of(emailTest5));
        when(emailRepository.findByEmail(emailTest4.getEmail())).thenReturn(Optional.of(emailTest4));
        when(relationshipRepository.findByEmailIdAndFriendId(emailTest5.getEmailId(), emailTest4.getEmailId()))
                .thenReturn(Optional.empty());

        assertSame("true", emailService.blockEmail(subBlockRequest).toString());
    }


    @Test
    public void testBlockNotExistEmail() {
        subBlockRequest = new SubscribeAndBlockRequest(emailTest1.getEmail(), emailTest2.getEmail());
        when(emailRepository.findByEmail(emailTest1.getEmail())).thenReturn(Optional.empty());
        when(emailRepository.findByEmail(emailTest2.getEmail())).thenReturn(Optional.empty());

        Throwable ex = assertThrows(WrongRequirementException.class, () -> emailService.blockEmail(subBlockRequest));
        assertSame("Requester or target email not existed", ex.getMessage());
    }

    @Test
    public void testBlockBlockedEmail() {
        subBlockRequest = new SubscribeAndBlockRequest(emailTest1.getEmail(), emailTest2.getEmail());
        when(emailRepository.findByEmail(emailTest1.getEmail())).thenReturn(Optional.ofNullable(emailTest1));
        when(emailRepository.findByEmail(emailTest2.getEmail())).thenReturn(Optional.ofNullable(emailTest2));
        when(relationshipRepository.findByEmailIdAndFriendId(emailTest1.getEmailId(), emailTest2.getEmailId()))
                .thenReturn(Optional.ofNullable(blockRelationship1));

        Throwable ex = assertThrows(WrongRequirementException.class, () -> emailService.blockEmail(subBlockRequest));
        assertSame("This email has already being blocked !!", ex.getMessage());
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

        Set<String> setEmails = emailService.retrieveEmails(retrieveRequest);
        assertTrue(setEmails.contains(listEmail.get(0)));
        assertTrue(setEmails.contains(listEmail.get(1)));
        assertTrue(setEmails.contains(listEmail.get(2)));
    }


    @Test
    public void testRetrieveEmptyEmail() {
        retrieveRequest = new RetrieveRequest(emailTest1.getEmail(), "Hello ");
        listEmail = Arrays.asList(emailTest2.getEmail(), emailTest3.getEmail(), emailTest5.getEmail());

        when(emailRepository.findByEmail(emailTest1.getEmail())).thenReturn(Optional.of(emailTest1));
        when(relationshipRepository.findByEmailId(emailTest1.getEmailId())).thenReturn(Collections.emptyList());

        Throwable ex = assertThrows(WrongRequirementException.class, () -> emailService.retrieveEmails(retrieveRequest));
        assertSame("No recipients found for the given email ", ex.getMessage());
    }

}
