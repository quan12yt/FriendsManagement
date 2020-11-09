package com.example.demo.service.serviceimp;

import com.example.demo.dto.EmailDTO;
import com.example.demo.model.Email;
import com.example.demo.model.FriendRelationship;
import com.example.demo.payload.AddFriendRequest;
import com.example.demo.payload.EmailRequest;
import com.example.demo.payload.SubscribeRequest;
import com.example.demo.repository.EmailRepository;
import com.example.demo.repository.FriendRelationshipRepository;
import com.example.demo.service.EmailService;
import com.example.demo.utils.ConverterUtils;
import com.example.demo.utils.EmailUtil;
import com.example.demo.utils.FriendStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmailServiceImp implements EmailService {

    @Autowired
    EmailRepository emailRepository;
    @Autowired
    FriendRelationshipRepository friendRelationshipRepository;
    private ConverterUtils converter = new ConverterUtils();
    private EmailUtil emailUtil = new EmailUtil();

    @Override
    public List<EmailDTO> getAllEmails() {
        return emailRepository.findAll().stream()
                .map(converter::convertToEmailDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ResponseEntity<Map<String, Object>> getFriendList(EmailRequest emailRequest) {
        Map<String, Object> body = new HashMap<>();
        if (emailRequest == null) {
            body.put("Error", "Invalid request");
            return new ResponseEntity<Map<String, Object>>(body, HttpStatus.BAD_REQUEST);
        }
        Optional<Email> emailOptional = emailRepository.findByEmail(emailRequest.getEmail());
        if (emailOptional.isEmpty()) {
            body.put("Error", "Invalid email");
            return new ResponseEntity<Map<String, Object>>(body, HttpStatus.BAD_REQUEST);
        }
        Email email = emailOptional.get();
        if (email.getFriends().isEmpty()) {
            body.put("Error", "Friends not found");
            return new ResponseEntity<Map<String, Object>>(body, HttpStatus.BAD_REQUEST);
        }
        List<String> list = email.getFriends().stream()
                .map(i -> i.getEmail())
                .collect(Collectors.toList());
        body.put("success", "true");
        body.put("friends", list);
        body.put("count", email.getFriends().size());
        return new ResponseEntity<Map<String, Object>>(body, HttpStatus.OK);
    }


    @Override
    public ResponseEntity<Map<String, Object>> addFriend(AddFriendRequest friendRequest) {
        Map<String, Object> body = new HashMap<>();
        if (friendRequest.getFriends().size() != 2) {
            body.put("Error", "Must have 2 emails");
            return new ResponseEntity<Map<String, Object>>(body, HttpStatus.BAD_REQUEST);
        }
        if (friendRequest.getFriends().get(0).equals(friendRequest.getFriends().get(1))) {
            body.put("Error", "Same email error ");
            return new ResponseEntity<Map<String, Object>>(body, HttpStatus.BAD_REQUEST);
        }
        Optional<Email> optionalEmail1 = emailRepository.
                findByEmail(friendRequest.getFriends().get(0));
        Optional<Email> optionalEmail2 = emailRepository
                .findByEmail(friendRequest.getFriends().get(1));
        if (optionalEmail1.isEmpty() || optionalEmail2.isEmpty()) {
            body.put("Error", "Invalid Email");
            return new ResponseEntity<Map<String, Object>>(body, HttpStatus.BAD_REQUEST);
        }
        Email email1 = optionalEmail1.get();
        Email email2 = optionalEmail2.get();
        if (email1.getEmailId() == null || email2.getEmailId() == null) {
            body.put("Error", "Both emails have to be in database");
            return new ResponseEntity<Map<String, Object>>(body, HttpStatus.BAD_REQUEST);
        }
        if(emailUtil.getRelationshipStatus(email1,email2) ==  FriendStatus.BLOCK) {
            body.put("Error", "This email has been blocked !!");
            return new ResponseEntity<Map<String, Object>>(body, HttpStatus.BAD_REQUEST);
        }
        if (emailUtil.isFriendOf(email1, email2)) {
            body.put("Error", "Two Email have already being friend");
            return new ResponseEntity<Map<String, Object>>(body, HttpStatus.BAD_REQUEST);
        }
        FriendRelationship relationship = new FriendRelationship
                (email1.getEmailId(), email2.getEmailId(), FriendStatus.FRIEND + "");
        FriendRelationship relationship1 = new FriendRelationship
                (email2.getEmailId(), email1.getEmailId(), FriendStatus.FRIEND + "");
        friendRelationshipRepository.save(relationship);
        friendRelationshipRepository.save(relationship1);
        body.put("success", "true");
        return new ResponseEntity<Map<String, Object>>(body, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Map<String, Object>> getCommonFriends(AddFriendRequest friendRequest) {
        Map<String, Object> body = new HashMap<>();
        if (friendRequest.getFriends().size() != 2) {
            body.put("Error", "Must contains 2 emails");
            return new ResponseEntity<Map<String, Object>>(body, HttpStatus.BAD_REQUEST);
        }
        if (friendRequest.getFriends().get(0).equals(friendRequest.getFriends().get(1))) {
            body.put("Error", "Same email error ");
            return new ResponseEntity<Map<String, Object>>(body, HttpStatus.BAD_REQUEST);
        }
        Optional<Email> optionalEmail1 = emailRepository.
                findByEmail(friendRequest.getFriends().get(0));
        Optional<Email> optionalEmail2 = emailRepository
                .findByEmail(friendRequest.getFriends().get(1));
        if (optionalEmail1.isEmpty() || optionalEmail2.isEmpty()) {
            body.put("Error", "Invalid Email");
            return new ResponseEntity<Map<String, Object>>(body, HttpStatus.BAD_REQUEST);
        }
        Email email1 = optionalEmail1.get();
        Email email2 = optionalEmail2.get();
        if (email1.getEmailId() == null || email2.getEmailId() == null) {
            body.put("Error", "Both emails have to be in database");
            return new ResponseEntity<Map<String, Object>>(body, HttpStatus.BAD_REQUEST);
        }
        List<String> emailList = new ArrayList<>();
        for (Email e : email2.getFriends()
        ) {
            if (email1.getFriends().contains(e)) {
                emailList.add(e.getEmail());
            }
        }
        body.put("success", "true");
        body.put("friends", emailList);
        body.put("count", emailList.size());
        return new ResponseEntity<Map<String, Object>>(body, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Map<String, Object>> subscribeTo(SubscribeRequest subscribeRequest) {
        Map<String, Object> body = new HashMap<>();
        if (subscribeRequest.getRequester() == null || subscribeRequest.getTarget() == null) {
            body.put("Error", "Invalid Request");
            return new ResponseEntity<Map<String, Object>>(body, HttpStatus.BAD_REQUEST);
        }
        if (subscribeRequest.getRequester().equals(subscribeRequest.getTarget())) {
            body.put("Error", "Same email error ");
            return new ResponseEntity<Map<String, Object>>(body, HttpStatus.BAD_REQUEST);
        }
        Optional<Email> optionalEmail1 = emailRepository.
                findByEmail(subscribeRequest.getRequester());
        Optional<Email> optionalEmail2 = emailRepository
                .findByEmail(subscribeRequest.getTarget());
        if (optionalEmail1.isEmpty() || optionalEmail2.isEmpty()) {
            body.put("Error", "Invalid Email");
            return new ResponseEntity<Map<String, Object>>(body, HttpStatus.BAD_REQUEST);
        }
        Email requestEmail = optionalEmail1.get();
        Email targetEmail = optionalEmail2.get();
        if (requestEmail.getEmailId() == null || targetEmail.getEmailId() == null) {
            body.put("Error", "Both emails have to be in database");
            return new ResponseEntity<Map<String, Object>>(body, HttpStatus.BAD_REQUEST);
        }
//        if(emailUtil.checkIfBlock(email1,email2)) {
//            body.put("Error", "This email has been blocked !!");
//            return new ResponseEntity<Map<String, Object>>(body, HttpStatus.BAD_REQUEST);
//        }
        if (emailUtil.isFriendOf(targetEmail, requestEmail)) {
            body.put("Error", "Two Email have already being friend");
            return new ResponseEntity<Map<String, Object>>(body, HttpStatus.BAD_REQUEST);
        }
        FriendRelationship relationship = new FriendRelationship
                (requestEmail.getEmailId(), targetEmail.getEmailId(), FriendStatus.FRIEND + "");
        FriendRelationship relationship1 = new FriendRelationship
                (relationship.getEmailId(), targetEmail.getEmailId(), FriendStatus.FRIEND + "");
        friendRelationshipRepository.save(relationship);
        friendRelationshipRepository.save(relationship1);
        body.put("success", "true");
        return new ResponseEntity<Map<String, Object>>(body, HttpStatus.CREATED);
    }
}
