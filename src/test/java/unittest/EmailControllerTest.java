package unittest;

import com.example.demo.FriendManagementApplication;
import com.example.demo.exception.DataNotFoundException;
import com.example.demo.exception.WrongRequirementException;
import com.example.demo.dto.request.AddAndGetCommonRequest;
import com.example.demo.dto.request.EmailRequest;
import com.example.demo.dto.request.RetrieveRequest;
import com.example.demo.dto.request.SubscribeAndBlockRequest;
import com.example.demo.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = FriendManagementApplication.class)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@Transactional
public class EmailControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmailService emailService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private EmailRequest emailRequest;
    private AddAndGetCommonRequest addCommonRequest;
    private SubscribeAndBlockRequest subBlockRequest;
    private RetrieveRequest retrieveRequest;


    List<String> listEmail;
    Set<String> setEmails;

    @Before
    public void setUp() {
        listEmail = new ArrayList<>();
        listEmail.add("quan@gmail.com");
        listEmail.add("hoang@gmail.com");

        setEmails = new HashSet<>();
        setEmails.add("quan@gmail.com");
        setEmails.add("hoang@gmail.com");

        retrieveRequest = new RetrieveRequest();
        emailRequest = new EmailRequest();
        addCommonRequest = new AddAndGetCommonRequest();
        subBlockRequest = new SubscribeAndBlockRequest();


    }

    @After
    public void clear() {
        retrieveRequest = new RetrieveRequest();
        emailRequest = new EmailRequest();
        addCommonRequest = new AddAndGetCommonRequest();
        subBlockRequest = new SubscribeAndBlockRequest();
    }

    @Test
    @WithUserDetails("quan")
    public void getFriendsSuccessTest() throws Exception {
        //Test with valid email
        emailRequest = new EmailRequest("huynhquang@gmail.com");
        String json = objectMapper.writeValueAsString(emailRequest);

        when(emailService.getFriendList(any(EmailRequest.class))).thenReturn(listEmail);

        mockMvc.perform(post("/emails/friends")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath
                        ("$.success", CoreMatchers.is("true")))
                .andExpect(jsonPath
                        ("$.count", CoreMatchers.is(2)))
                .andExpect(jsonPath
                        ("$.friends[0]", CoreMatchers.is("quan@gmail.com")));

    }

    @Test
    @WithUserDetails("quan")
    public void getFriendsNoContent() throws Exception {
        //Test with valid email
        emailRequest = new EmailRequest("huynhquang@gmail.com");
        String json = objectMapper.writeValueAsString(emailRequest);
        listEmail = Collections.emptyList();
        when(emailService.getFriendList(any(EmailRequest.class))).thenReturn(listEmail);

        mockMvc.perform(post("/emails/friends")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isNoContent());


    }

    @Test
    @WithUserDetails("quan")
    public void getFriendsInvalidRequestTest() throws Exception {
        //Test with invalid request
        emailRequest = new EmailRequest("");
        String json = objectMapper.writeValueAsString(emailRequest);

        mockMvc.perform(post("/emails/friends")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath
                        ("$.error[0]", CoreMatchers.is("Email mustn't be empty or null")));

    }

    @Test
    @WithUserDetails("quan")
    public void getFriendsInvalidEmailRequestTest() throws Exception {
        //Test with invalid email
        emailRequest = new EmailRequest("invalid");
        String json = objectMapper.writeValueAsString(emailRequest);

        when(emailService.getFriendList(any(EmailRequest.class))).thenReturn(listEmail);

        mockMvc.perform(post("/emails/friends")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath
                        ("$.message", CoreMatchers.is("Invalid email")));
    }

    @Test
    @WithUserDetails("quan")
    public void getFriendsEmailNotExistTest() throws Exception {
        //Test with not existed email
        emailRequest = new EmailRequest("hau@gmail.com");
        String json = objectMapper.writeValueAsString(emailRequest);

        when(emailService.getFriendList(any(EmailRequest.class)))
                .thenThrow(new DataNotFoundException("Email not found in database"));

        mockMvc.perform(post("/emails/friends")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath
                        ("$.message", CoreMatchers.is("Email not found in database")));

    }


    @Test
    @WithUserDetails("quan")
    public void addFriendsSuccessTest() throws Exception {
        addCommonRequest.setFriends(Arrays.asList("alone@gmail.com", "famous@gmail.com"));
        //Test with valid AddFriend request
        String json = objectMapper.writeValueAsString(addCommonRequest);

        when(emailService.addFriend(any(AddAndGetCommonRequest.class))).thenReturn(true);

        mockMvc.perform(post("/emails/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath
                        ("$.success", CoreMatchers.is("true")));
    }

    @Test
    @WithUserDetails("quan")
    public void addFriendsInvalidRequestTest() throws Exception {
        //Test with invalid AddFriend request
        addCommonRequest.setFriends(null);

        when(emailService.addFriend(any(AddAndGetCommonRequest.class))).thenReturn(true);

        String json = objectMapper.writeValueAsString(addCommonRequest);
        mockMvc.perform(post("/emails/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath
                        ("$.error[0]", CoreMatchers.is("List email must not be null or empty")));

    }

    @Test
    @WithUserDetails("quan")
    public void addFriendsLackEmailRequestTest() throws Exception {
        //Test with invalid AddFriend request
        addCommonRequest.setFriends(Collections.singletonList("alone@gmail.com"));
        String json = objectMapper.writeValueAsString(addCommonRequest);

        when(emailService.addFriend(any(AddAndGetCommonRequest.class))).thenReturn(true);

        mockMvc.perform(post("/emails/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath
                        ("$.message", CoreMatchers.is("Must contains 2 emails")));

    }

    @Test
    @WithUserDetails("quan")
    public void addFriendsInvalidEmailTest() throws Exception {
        //Test with invalid AddFriend request
        addCommonRequest.setFriends(Arrays.asList("aloneEmail.com", "famous@gmail.com"));

        when(emailService.addFriend(any(AddAndGetCommonRequest.class))).thenReturn(true);

        String json = objectMapper.writeValueAsString(addCommonRequest);
        mockMvc.perform(post("/emails/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath
                        ("$.message", CoreMatchers.is("Invalid email")));
    }

    @Test
    @WithUserDetails("quan")
    public void addFriendsSameEmailTest() throws Exception {
        //Test with invalid AddFriend request
        addCommonRequest.setFriends(Arrays.asList("famous@gmail.com", "famous@gmail.com"));
        when(emailService.addFriend(any(AddAndGetCommonRequest.class))).thenReturn(true);

        String json = objectMapper.writeValueAsString(addCommonRequest);
        mockMvc.perform(post("/emails/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath
                        ("$.message", CoreMatchers.is("Same email error")));
    }

    @Test
    @WithUserDetails("quan")
    public void addFriendsBlockEmailTest() throws Exception {
        //Test with invalid AddFriend request
        addCommonRequest.setFriends(Arrays.asList("hoangtu1@gmail.com", "huynhquang@gmail.com"));
        when(emailService.addFriend(any(AddAndGetCommonRequest.class)))
                .thenThrow(new WrongRequirementException("This email has been blocked !!"));

        String json = objectMapper.writeValueAsString(addCommonRequest);
        mockMvc.perform(post("/emails/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath
                        ("$.message", CoreMatchers.is("This email has been blocked !!")));
    }

    @Test
    @WithUserDetails("quan")
    public void addFriendsAlreadyFriendTest() throws Exception {
        //Test with invalid AddFriend request
        addCommonRequest.setFriends(Arrays.asList("hoauanh@gmail.com", "huynhquang@gmail.com"));

        when(emailService.addFriend(any(AddAndGetCommonRequest.class)))
                .thenThrow(new WrongRequirementException("Two Email have already being friend"));

        String json = objectMapper.writeValueAsString(addCommonRequest);
        mockMvc.perform(post("/emails/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath
                        ("$.message", CoreMatchers.is("Two Email have already being friend")));
    }


    @Test
    @WithUserDetails("quan")
    public void getCommonFriendsSuccessTest() throws Exception {
        //Test with valid input
        addCommonRequest.setFriends(Arrays.asList("anhthus@gmail.com", "huynhquang@gmail.com"));

        when(emailService.getCommonFriends(any(AddAndGetCommonRequest.class))).thenReturn(listEmail);

        String json = objectMapper.writeValueAsString(addCommonRequest);
        mockMvc.perform(post("/emails/common")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath
                        ("$.success", CoreMatchers.is("true")))
                .andExpect(jsonPath
                        ("$.count", CoreMatchers.is(2)))
                .andExpect(jsonPath
                        ("$.friends[0]", CoreMatchers.is("quan@gmail.com")));
    }

    @Test
    @WithUserDetails("quan")
    public void getCommonFriendsNoContent() throws Exception {
        //Test with valid input
        addCommonRequest.setFriends(Arrays.asList("anhthus@gmail.com", "huynhquang@gmail.com"));
        listEmail = Collections.emptyList();
        when(emailService.getCommonFriends(any(AddAndGetCommonRequest.class))).thenReturn(listEmail);

        String json = objectMapper.writeValueAsString(addCommonRequest);
        mockMvc.perform(post("/emails/common")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @WithUserDetails("quan")
    public void getCommonFriendsInvalidInputTest() throws Exception {
        //Test with invalid input
        addCommonRequest.setFriends(null);
        String json = objectMapper.writeValueAsString(addCommonRequest);

        mockMvc.perform(post("/emails/common")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath
                        ("$.error[0]", CoreMatchers.is("List email must not be null or empty")));

    }

    @Test
    @WithUserDetails("quan")
    public void getCommonFriendsLackEmailRequestTest() throws Exception {
        //Test with invalid AddFriend request
        addCommonRequest.setFriends(Collections.singletonList("alone@gmail.com"));
        String json = objectMapper.writeValueAsString(addCommonRequest);
        mockMvc.perform(post("/emails/common")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",
                        CoreMatchers.is("Must contains 2 emails")));

    }

    @Test
    @WithUserDetails("quan")
    public void getCommonFriendsInvalidEmailTest() throws Exception {
        //Test with invalid AddFriend request
        addCommonRequest.setFriends(Arrays.asList("aloneEmail.com", "famous@gmail.com"));
        String json = objectMapper.writeValueAsString(addCommonRequest);
        mockMvc.perform(post("/emails/common")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath
                        ("$.message", CoreMatchers.is("Invalid email")));
    }

    @Test
    @WithUserDetails("quan")
    public void getCommonFriendsSameEmailTest() throws Exception {
        //Test with invalid AddFriend request
        addCommonRequest.setFriends(Arrays.asList("famous@gmail.com", "famous@gmail.com"));
        String json = objectMapper.writeValueAsString(addCommonRequest);
        mockMvc.perform(post("/emails/common")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath
                        ("$.message", CoreMatchers.is("Same email error")));
    }

    @Test
    @WithUserDetails("quan")
    public void getCommonFriendsNotExistEmailTest() throws Exception {
        //Test with invalid AddFriend request
        addCommonRequest.setFriends(Arrays.asList("mous@gmail.com", "famous@gmail.com"));

        when(emailService.getCommonFriends(any(AddAndGetCommonRequest.class)))
                .thenThrow(new DataNotFoundException("Email not exist"));

        String json = objectMapper.writeValueAsString(addCommonRequest);
        mockMvc.perform(post("/emails/common")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath
                        ("$.message", CoreMatchers.is("Email not exist")));
    }

    @Test
    @WithUserDetails("quan")
    public void subscribeToSuccessTest() throws Exception {
        //Test with valid input
        subBlockRequest = new SubscribeAndBlockRequest
                ("alone@gmail.com", "famous@gmail.com");

        when(emailService.subscribeTo(any(SubscribeAndBlockRequest.class))).thenReturn(true);

        String json = objectMapper.writeValueAsString(subBlockRequest);
        mockMvc.perform(put("/emails/subscribe")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath
                        ("$.success", CoreMatchers.is("true")));
    }

    @Test
    @WithUserDetails("quan")
    public void subscribeToInvalidInputTest() throws Exception {
        //Test with invalid input
        subBlockRequest.setRequester(null);
        subBlockRequest.setTarget("target@gmail.com");

        String json = objectMapper.writeValueAsString(subBlockRequest);

        mockMvc.perform(put("/emails/subscribe")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error[0]", CoreMatchers
                        .is("Requester email must not be empty or null")));

    }

    @Test
    @WithUserDetails("quan")
    public void subscribeToInvalidEmailTest() throws Exception {
        //Test with invalid input
        subBlockRequest.setRequester("targetGmail.com");
        subBlockRequest.setTarget("target@gmail.com");

        String json = objectMapper.writeValueAsString(subBlockRequest);

        mockMvc.perform(put("/emails/subscribe")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath
                        ("$.message", CoreMatchers
                                .is("Invalid requester or target email")));

    }

    @Test
    @WithUserDetails("quan")
    public void subscribeToSameEmailTest() throws Exception {
        //Test with invalid input
        subBlockRequest.setRequester("target@gmail.com");
        subBlockRequest.setTarget("target@gmail.com");

        String json = objectMapper.writeValueAsString(subBlockRequest);

        mockMvc.perform(put("/emails/subscribe")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath
                        ("$.message", CoreMatchers
                                .is("Same email error")));

    }

    @Test
    @WithUserDetails("quan")
    public void subscribeToNotExistEmailTest() throws Exception {
        //Test with invalid input
        subBlockRequest.setRequester("taret@gmail.com");
        subBlockRequest.setTarget("target@gmail.com");

        when(emailService.subscribeTo(any(SubscribeAndBlockRequest.class)))
                .thenThrow(new DataNotFoundException("Requester or target email not existed"));

        String json = objectMapper.writeValueAsString(subBlockRequest);

        mockMvc.perform(put("/emails/subscribe")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath
                        ("$.message", CoreMatchers
                                .is("Requester or target email not existed")));

    }

    @Test
    @WithUserDetails("quan")
    public void subscribeToBlockedExistEmailTest() throws Exception {
        //Test with invalid input
        subBlockRequest.setRequester("hoangtu1@gmail.com");
        subBlockRequest.setTarget("huynhquang@gmail.com");

        when(emailService.subscribeTo(any(SubscribeAndBlockRequest.class)))
                .thenThrow(new WrongRequirementException("This email has been blocked !!"));

        String json = objectMapper.writeValueAsString(subBlockRequest);

        mockMvc.perform(put("/emails/subscribe")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath
                        ("$.message", CoreMatchers
                                .is("This email has been blocked !!")));

    }

    @Test
    @WithUserDetails("quan")
    public void subscribeToFriendEmailTest() throws Exception {
        //Test with invalid input
        subBlockRequest.setRequester("anhthus@gmail.com");
        subBlockRequest.setTarget("huynhquang@gmail.com");

        when(emailService.subscribeTo(any(SubscribeAndBlockRequest.class)))
                .thenThrow(new WrongRequirementException("Already being friend of this target ,  !!"));

        String json = objectMapper.writeValueAsString(subBlockRequest);

        mockMvc.perform(put("/emails/subscribe")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath
                        ("$.message", CoreMatchers
                                .is("Already being friend of this target ,  !!")));

    }

    @Test
    @WithUserDetails("quan")
    public void subscribeToSubscribedEmailTest() throws Exception {
        //Test with invalid input
        subBlockRequest.setRequester("vuiquanghau@gmail.com");
        subBlockRequest.setTarget("hoangtu1@gmail.com");

        when(emailService.subscribeTo(any(SubscribeAndBlockRequest.class)))
                .thenThrow(new WrongRequirementException("Already subscribed to this target email !!"));

        String json = objectMapper.writeValueAsString(subBlockRequest);

        mockMvc.perform(put("/emails/subscribe")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath
                        ("$.message", CoreMatchers
                                .is("Already subscribed to this target email !!")));

    }

    @Test
    @WithUserDetails("quan")
    public void blockEmailSuccessTest() throws Exception {
        //Test with invalid input
        subBlockRequest.setRequester("alone@gmail.com");
        subBlockRequest.setTarget("famous@gmail.com");

        when(emailService.blockEmail(any(SubscribeAndBlockRequest.class))).thenReturn(true);

        String json = objectMapper.writeValueAsString(subBlockRequest);
        mockMvc.perform(put("/emails/block")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath
                        ("$.success", CoreMatchers.is("true")));
    }

    @Test
    @WithUserDetails("quan")
    public void blockEmailInvalidInputTest() throws Exception {
        //Test with invalid input
        subBlockRequest.setRequester(null);
        subBlockRequest.setTarget("target@gmail.com");

        String json = objectMapper.writeValueAsString(subBlockRequest);
        mockMvc.perform(put("/emails/block")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath
                        ("$.error[0]", CoreMatchers.is("Requester email must not be empty or null")));

    }

    @Test
    @WithUserDetails("quan")
    public void blockEmailInvalidEmailTest() throws Exception {
        //Test with invalid input
        subBlockRequest.setRequester("targetGmail.com");
        subBlockRequest.setTarget("target@gmail.com");

        String json = objectMapper.writeValueAsString(subBlockRequest);

        mockMvc.perform(put("/emails/block")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath
                        ("$.message", CoreMatchers
                                .is("Invalid requester or target email")));

    }

    @Test
    @WithUserDetails("quan")
    public void blockEmailSameEmailTest() throws Exception {
        //Test with invalid input
        subBlockRequest.setRequester("target@gmail.com");
        subBlockRequest.setTarget("target@gmail.com");

        String json = objectMapper.writeValueAsString(subBlockRequest);

        mockMvc.perform(put("/emails/block")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath
                        ("$.message", CoreMatchers
                                .is("Same email error")));

    }

    @Test
    @WithUserDetails("quan")
    public void blockEmailNotExistEmailTest() throws Exception {
        //Test with invalid input
        subBlockRequest.setRequester("taret@gmail.com");
        subBlockRequest.setTarget("target@gmail.com");

        when(emailService.blockEmail(any(SubscribeAndBlockRequest.class)))
                .thenThrow(new DataNotFoundException("Requester or target email not existed"));

        String json = objectMapper.writeValueAsString(subBlockRequest);

        mockMvc.perform(put("/emails/block")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath
                        ("$.message", CoreMatchers
                                .is("Requester or target email not existed")));
    }

    @Test
    @WithUserDetails("quan")
    public void blockEmailBlockedEmailTest() throws Exception {
        //Test with invalid input
        subBlockRequest.setRequester("hoangtu1@gmail.com");
        subBlockRequest.setTarget("huynhquang@gmail.com");

        when(emailService.blockEmail(any(SubscribeAndBlockRequest.class)))
                .thenThrow(new WrongRequirementException("This email has already being blocked !!"));

        String json = objectMapper.writeValueAsString(subBlockRequest);

        mockMvc.perform(put("/emails/block")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath
                        ("$.message", CoreMatchers
                                .is("This email has already being blocked !!")));

    }

    @Test
    @WithUserDetails("quan")
    public void retrieveEmailSuccessTest() throws Exception {
        //Test with valid RetrieveRequest
        retrieveRequest = new RetrieveRequest("vuiquanghau@gmail.com", "Hello hauhoang@gmail.com");

        when(emailService.retrieveEmails(any(RetrieveRequest.class)))
                .thenReturn(setEmails);

        String json = objectMapper.writeValueAsString(retrieveRequest);

        mockMvc.perform(post("/emails/retrieve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath
                        ("$.success", CoreMatchers.is("true")))
                .andExpect(jsonPath
                        ("$.recipients[0]", CoreMatchers.is("quan@gmail.com")));
    }

    @Test
    @WithUserDetails("quan")
    public void retrieveEmailInvalidTest() throws Exception {
        //Test with invalid RetrieveRequest
        retrieveRequest = new RetrieveRequest(null, "Hello haong@gmail.com");

        String json = objectMapper.writeValueAsString(retrieveRequest);

        mockMvc.perform(post("/emails/retrieve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath
                        ("$.error[0]", CoreMatchers.is("Sender email must not be null or empty")));
    }

    @Test
    @WithUserDetails("quan")
    public void retrieveNotExistEmailTest() throws Exception {
        //Test with invalid RetrieveRequest
        retrieveRequest = new RetrieveRequest("vuiquanghu@gmail.com", "Hello haong@gmail.com");

        when(emailService.retrieveEmails(any(RetrieveRequest.class)))
                .thenThrow(new DataNotFoundException("Email not existed"));

        String json = objectMapper.writeValueAsString(retrieveRequest);

        mockMvc.perform(post("/emails/retrieve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath
                        ("$.message", CoreMatchers.is("Email not existed")));
    }

    @Test
    @WithUserDetails("quan")
    public void retrieveNotFoundTest() throws Exception {
        //Test with invalid RetrieveRequest
        retrieveRequest = new RetrieveRequest("alone@gmail.com", "Hello hang@gmail.com");
        setEmails = Collections.emptySet();
        when(emailService.retrieveEmails(any(RetrieveRequest.class)))
                .thenReturn(setEmails);

        String json = objectMapper.writeValueAsString(retrieveRequest);

        mockMvc.perform(post("/emails/retrieve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

}
