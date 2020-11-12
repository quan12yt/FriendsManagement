package unittest;

import com.example.demo.FriendManagementApplication;
import com.example.demo.payload.AddAndGetCommonRequest;
import com.example.demo.payload.EmailRequest;
import com.example.demo.payload.RetrieveRequest;
import com.example.demo.payload.SubscribeAndBlockRequest;
import com.example.demo.repository.EmailRepository;
import com.example.demo.repository.FriendRelationshipRepository;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = FriendManagementApplication.class)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class EmailControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmailRepository emailRepository;
    @MockBean
    private FriendRelationshipRepository friendRelationshipRepository;
    @MockBean
    private EmailService emailService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private EmailRequest emailRequest;
    private AddAndGetCommonRequest addCommonRequest;
    private SubscribeAndBlockRequest subBlockRequest;
    private RetrieveRequest retrieveRequest;
    List<String> listEmail;

    @Before
    public void setUp() {
        listEmail = new ArrayList<>();
        listEmail.add("quan@gmail.com");
        listEmail.add("hoang@gmail.com");
        retrieveRequest = new RetrieveRequest();
        emailRequest = new EmailRequest();
        addCommonRequest = new AddAndGetCommonRequest();
        subBlockRequest = new SubscribeAndBlockRequest();
    }

    @After
    public void clear() {
        emailRequest = new EmailRequest();
        addCommonRequest = new AddAndGetCommonRequest();
        subBlockRequest = new SubscribeAndBlockRequest();
    }

    @Test
    public void getFriendsSuccessTest() throws Exception {
        Map<String, Object> body = new HashMap<>();

        //Test with valid email
        emailRequest = new EmailRequest("hau@gmail.com");
        body.put("success", "true");
        body.put("friends", listEmail);
        body.put("count", listEmail.size());
        ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(body, HttpStatus.OK);

        when(emailService.getFriendList(any(EmailRequest.class))).thenReturn(responseEntity);
        String json = objectMapper.writeValueAsString(emailRequest);

        mockMvc.perform(post("/emails/friends")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath
                        ("$.success", CoreMatchers.is("true")))
                .andExpect(MockMvcResultMatchers.jsonPath
                        ("$.count", CoreMatchers.is(listEmail.size())))
                .andExpect(MockMvcResultMatchers.jsonPath
                        ("$.friends[0]", CoreMatchers.is(listEmail.get(0))));

    }

    @Test
    public void getFriendsInvalidEmailRequestTest() throws Exception {
        Map<String, Object> body = new HashMap<>();

        //Test with invalid email
        emailRequest = new EmailRequest("");
        body.put("error", "Email mustn't be empty or null");
        ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);

        when(emailService.getFriendList(emailRequest)).thenReturn(responseEntity);
        String json = objectMapper.writeValueAsString(emailRequest);

        mockMvc.perform(post("/emails/friends")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath
                        ("$.error[0]", CoreMatchers.is("Email mustn't be empty or null")));

    }

    @Test
    public void addFriendsSuccessTest() throws Exception {
        Map<String, Object> body = new HashMap<>();

        //Test with valid AddFriend request
        addCommonRequest.setFriends(listEmail);
        body.put("success", "true");
        ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(body, HttpStatus.CREATED);

        when(emailService.addFriend(any(AddAndGetCommonRequest.class))).thenReturn(responseEntity);
        String json = objectMapper.writeValueAsString(addCommonRequest);

        mockMvc.perform(post("/emails/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath
                        ("$.success", CoreMatchers.is("true")));
    }

    @Test
    public void addFriendsInvalidEmailRequestTest() throws Exception {
        Map<String, Object> body = new HashMap<>();

        //Test with invalid AddFriend request
        addCommonRequest.setFriends(null);
        body.put("Error", "List email must not be null or empty");
        ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);

        when(emailService.addFriend(addCommonRequest)).thenReturn(responseEntity);
        String json = objectMapper.writeValueAsString(addCommonRequest);

        mockMvc.perform(post("/emails/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath
                        ("$.error[0]", CoreMatchers.is("List email must not be null or empty")));

    }


    @Test
    public void getCommonFriendsSuccessTest() throws Exception {
        Map<String, Object> body = new HashMap<>();

        //Test with valid input
        addCommonRequest.setFriends(listEmail);
        body.put("success", "true");
        body.put("friends", listEmail);
        body.put("count", listEmail.size());
        ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(body, HttpStatus.OK);

        when(emailService.getCommonFriends(any(AddAndGetCommonRequest.class))).thenReturn(responseEntity);
        String json = objectMapper.writeValueAsString(addCommonRequest);

        mockMvc.perform(post("/emails/common")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath
                        ("$.success", CoreMatchers.is("true")))
                .andExpect(MockMvcResultMatchers.jsonPath
                        ("$.count", CoreMatchers.is(listEmail.size())))
                .andExpect(MockMvcResultMatchers.jsonPath
                        ("$.friends[0]", CoreMatchers.is(listEmail.get(0))));
    }

    @Test
    public void getCommonFriendsInvalidInputTest() throws Exception {
        Map<String, Object> body = new HashMap<>();

        //Test with invalid input
        addCommonRequest.setFriends(null);
        body.put("error", "Email mustn't be empty or null");
        ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);

        when(emailService.getCommonFriends(any(AddAndGetCommonRequest.class))).thenReturn(responseEntity);
        String json = objectMapper.writeValueAsString(addCommonRequest);

        mockMvc.perform(post("/emails/common")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath
                        ("$.error[0]", CoreMatchers.is("List email must not be null or empty")));

    }

    @Test
    public void subscribeToSuccessTest() throws Exception {
        Map<String, Object> body = new HashMap<>();

        //Test with valid input
        subBlockRequest = new SubscribeAndBlockRequest("requester@gmail.com"
                , "target@gamil.com");
        body.put("success", "true");
        ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(body
                , HttpStatus.CREATED);

        when(emailService.subscribeTo(any(SubscribeAndBlockRequest.class))).thenReturn(responseEntity);
        String json = objectMapper.writeValueAsString(subBlockRequest);

        mockMvc.perform(post("/emails/subscribe")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath
                        ("$.success", CoreMatchers.is("true")));
    }

    @Test
    public void subscribeToInvalidInputTest() throws Exception {
        Map<String, Object> body = new HashMap<>();

        //Test with invalid input
        subBlockRequest.setRequester(null);
        subBlockRequest.setTarget("target@gmail.com");

        ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);

        when(emailService.subscribeTo(any(SubscribeAndBlockRequest.class))).thenReturn(responseEntity);
        String json = objectMapper.writeValueAsString(subBlockRequest);

        mockMvc.perform(post("/emails/subscribe")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath
                        ("$.error[0]", CoreMatchers.is("Requester email must not be empty or null")));


    }

    @Test
    public void blockEmailSuccessTest() throws Exception {
        Map<String, Object> body = new HashMap<>();

        //Test with invalid input
        subBlockRequest.setRequester("requester@gmail.com");
        subBlockRequest.setTarget("target@gmail.com");

        body.put("success", "true");
        ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(body, HttpStatus.CREATED);
        when(emailService.blockEmail(any(SubscribeAndBlockRequest.class))).thenReturn(responseEntity);
        String json = objectMapper.writeValueAsString(subBlockRequest);
        mockMvc.perform(post("/emails/block")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath
                        ("$.success", CoreMatchers.is("true")));
    }

    @Test
    public void blockEmailInvalidInputTest() throws Exception {
        Map<String, Object> body = new HashMap<>();

        //Test with invalid input
        subBlockRequest.setRequester(null);
        subBlockRequest.setTarget("target@gmail.com");

        body.put("success", "true");
        ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        when(emailService.blockEmail(any(SubscribeAndBlockRequest.class))).thenReturn(responseEntity);
        String json = objectMapper.writeValueAsString(subBlockRequest);
        mockMvc.perform(post("/emails/block")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath
                        ("$.error[0]", CoreMatchers.is("Requester email must not be empty or null")));

    }

    @Test
    public void retrieveEmailSuccessTest() throws Exception {
        Map<String, Object> body = new HashMap<>();

        //Test with valid RetrieveRequest
        retrieveRequest = new RetrieveRequest("hau@gmail.com", "Hello haong@gmail.com");

        body.put("success", "true");
        body.put("recipients", listEmail);
        ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(body, HttpStatus.OK);

        when(emailService.retrieveEmails(any(RetrieveRequest.class))).thenReturn(responseEntity);
        String json = objectMapper.writeValueAsString(retrieveRequest);

        mockMvc.perform(post("/emails/retrieve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath
                        ("$.success", CoreMatchers.is("true")))
                .andExpect(MockMvcResultMatchers.jsonPath
                        ("$.recipients[0]", CoreMatchers.is(listEmail.get(0))));
    }

    @Test
    public void retrieveEmailInvalidTest() throws Exception {
        Map<String, Object> body = new HashMap<>();

        //Test with invalid RetrieveRequest
        retrieveRequest = new RetrieveRequest(null, "Hello haong@gmail.com");

        body.put("success", "true");
        body.put("recipients", listEmail);
        ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(body, HttpStatus.OK);

        when(emailService.retrieveEmails(any(RetrieveRequest.class))).thenReturn(responseEntity);
        String json = objectMapper.writeValueAsString(retrieveRequest);

        mockMvc.perform(post("/emails/retrieve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath
                        ("$.error[0]", CoreMatchers.is("Sender email must not be null or empty")));

    }


}
