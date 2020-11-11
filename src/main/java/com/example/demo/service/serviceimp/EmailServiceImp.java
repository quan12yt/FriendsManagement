package com.example.demo.service.serviceimp;

import com.example.demo.model.Email;
import com.example.demo.model.FriendRelationship;
import com.example.demo.payload.AddAndGetCommonRequest;
import com.example.demo.payload.EmailRequest;
import com.example.demo.payload.RetrieveRequest;
import com.example.demo.payload.SubscribeAndBlockRequest;
import com.example.demo.repository.EmailRepository;
import com.example.demo.repository.FriendRelationshipRepository;
import com.example.demo.service.EmailService;
import com.example.demo.utils.EmailUtils;
import com.example.demo.utils.EmailValidation;
import com.example.demo.utils.FriendStatus;
import com.example.demo.utils.RequestValidation;
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
    FriendRelationshipRepository relationshipRepository;
    @Autowired
    FriendRelationshipRepository friendRelationshipRepository;
    private final EmailUtils emailUtil = new EmailUtils();


    @Override
    public ResponseEntity<Map<String, Object>> getFriendList(EmailRequest emailRequest) {
        Map<String, Object> body = new HashMap<>();
        if (emailRequest == null) {
            body.put("Error", "Invalid request");
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
        if (!EmailValidation.isEmail(emailRequest.getEmail())) {
            body.put("Error", "Invalid email");
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
        Optional<Email> emailOptional = emailRepository.findByEmail(emailRequest.getEmail());
        if (emailOptional.isEmpty()) {
            body.put("Error", "Email not found in database");
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
        Email email = emailOptional.get();
        if (email.getFriends().isEmpty()) {
            body.put("Error", "Friends not found");
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
        List<String> list = relationshipRepository
                .findByEmailIdAndStatus(email.getEmailId()
                        , String.valueOf(FriendStatus.FRIEND))
                .stream().map(i -> emailRepository.findById(i.getFriendId()).get().getEmail())
                .collect(Collectors.toList());
        body.put("success", "true");
        body.put("friends", list);
        body.put("count", list.size());
        return new ResponseEntity<>(body, HttpStatus.OK);
    }


    @Override
    public ResponseEntity<Map<String, Object>> addFriend(AddAndGetCommonRequest friendRequest) {
        Map<String, Object> body = new HashMap<>();
        String error = RequestValidation.checkAddAndSubscribeRequest(friendRequest);
        if(!(error == "")){
            body.put("Error",error);
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
        Optional<Email> optionalEmail1 = emailRepository.
                findByEmail(friendRequest.getFriends().get(0));
        Optional<Email> optionalEmail2 = emailRepository
                .findByEmail(friendRequest.getFriends().get(1));
        if (optionalEmail1.isEmpty() || optionalEmail2.isEmpty()) {
            body.put("Error", "Invalid Email");
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
        Email email1 = optionalEmail1.get();
        Email email2 = optionalEmail2.get();
        if (email1.getEmailId() == null || email2.getEmailId() == null) {
            body.put("Error", "Both emails have to be in database");
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
        Optional<FriendRelationship> friendRelationship = relationshipRepository
                .findByEmailIdAndFriendId(email1.getEmailId(), email2.getEmailId());
        if (friendRelationship.isPresent()) {
            if (friendRelationship.get().getStatus().contains(String.valueOf(FriendStatus.BLOCK))) {
                body.put("Error", "This email has been blocked !!");
                return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
            }
            if (friendRelationship.get().getStatus().contains(String.valueOf(FriendStatus.FRIEND))) {
                body.put("Error", "Two Email have already being friend");
                return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
            }
        }
        FriendRelationship relationship = new FriendRelationship
                (email1.getEmailId(), email2.getEmailId(), FriendStatus.FRIEND + "");
        FriendRelationship inverseRelationship = new FriendRelationship
                (email2.getEmailId(), email1.getEmailId(), FriendStatus.FRIEND + "");
        friendRelationshipRepository.save(relationship);
        friendRelationshipRepository.save(inverseRelationship);
        body.put("success", "true");
        return new ResponseEntity<>(body, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Map<String, Object>> getCommonFriends(AddAndGetCommonRequest friendRequest) {
        Map<String, Object> body = new HashMap<>();
        String error = RequestValidation.checkAddAndSubscribeRequest(friendRequest);
        if(!(error == "")){
            body.put("Error",error);
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
        Optional<Email> optionalEmail1 = emailRepository.
                findByEmail(friendRequest.getFriends().get(0));
        Optional<Email> optionalEmail2 = emailRepository
                .findByEmail(friendRequest.getFriends().get(1));
        if (optionalEmail1.isEmpty() || optionalEmail2.isEmpty()) {
            body.put("Error", "Email not exist");
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
        List<String> friendsOfEmail1 = this.getFriendsOfEmail(optionalEmail1.get());
        List<String> friendsOfEmail2 = this.getFriendsOfEmail(optionalEmail2.get());
        List<String> emailList = new ArrayList<>();
        for (String f : friendsOfEmail1
        ) {
            if (friendsOfEmail2.contains(f)) {
                emailList.add(f);
            }
        }
        body.put("success", "true");
        body.put("friends", emailList);
        body.put("count", emailList.size());
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Map<String, Object>> subscribeTo(SubscribeAndBlockRequest subscribeRequest) {
        Map<String, Object> body = new HashMap<>();
        String error = RequestValidation.checkSubscribeAndBlockRequest(subscribeRequest);
        if(!(error == "")){
            body.put("Error",error);
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
        Optional<Email> optionalEmail1 = emailRepository.
                findByEmail(subscribeRequest.getRequester());
        Optional<Email> optionalEmail2 = emailRepository
                .findByEmail(subscribeRequest.getTarget());
        if (optionalEmail1.isEmpty() || optionalEmail2.isEmpty()) {
            body.put("Error", "Email not exist");
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
        Email requestEmail = optionalEmail1.get();
        Email targetEmail = optionalEmail2.get();
        Optional<FriendRelationship> friendRelationship = relationshipRepository
                .findByEmailIdAndFriendId(requestEmail.getEmailId(), targetEmail.getEmailId());
        if (friendRelationship.isPresent()) {
            if (friendRelationship.get().getStatus().contains(String.valueOf(FriendStatus.BLOCK))) {
                body.put("Error", "This email has been blocked !!");
                return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
            }
            if (friendRelationship.get().getStatus().contains(String.valueOf(FriendStatus.SUBSCRIBE))) {
                body.put("Error", "Already subscribed to this target email !!");
                return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
            }
            if (friendRelationship.get().getStatus().contains(String.valueOf(FriendStatus.FRIEND))) {
                body.put("Error", "Already being friend of this target ,  !!");
                return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
            }
        }

        FriendRelationship relationship = new FriendRelationship
                (requestEmail.getEmailId(), targetEmail.getEmailId(), FriendStatus.SUBSCRIBE + "");
        relationshipRepository.save(relationship);
        body.put("success", "true");
        return new ResponseEntity<>(body, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Map<String, Object>> blockEmail(SubscribeAndBlockRequest subscribeRequest) {
        Map<String, Object> body = new HashMap<>();
        String error = RequestValidation.checkSubscribeAndBlockRequest(subscribeRequest);
        if(!(error == "")){
            body.put("Error",error);
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
        Optional<Email> optionalEmail1 = emailRepository.
                findByEmail(subscribeRequest.getRequester());
        Optional<Email> optionalEmail2 = emailRepository
                .findByEmail(subscribeRequest.getTarget());
        if (optionalEmail1.isEmpty() || optionalEmail2.isEmpty()) {
            body.put("Error", "Email not exist in database");
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
        Email requestEmail = optionalEmail1.get();
        Email targetEmail = optionalEmail2.get();
        Optional<FriendRelationship> friendRelationship = relationshipRepository
                .findByEmailIdAndFriendId(requestEmail.getEmailId(), targetEmail.getEmailId());
        if (friendRelationship.isPresent()) {
            if (friendRelationship.get().getStatus().contains(String.valueOf(FriendStatus.BLOCK))) {
                body.put("Error", "This email has already being blocked !!");
                return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
            }
            friendRelationship.map(friendRelationship1 -> {
                friendRelationship1.setStatus(String.valueOf(FriendStatus.BLOCK));
                return relationshipRepository.save(friendRelationship1);
            });
            body.put("success", "true");
            return new ResponseEntity<>(body, HttpStatus.CREATED);
        }

        FriendRelationship relationship = new FriendRelationship
                (requestEmail.getEmailId(), targetEmail.getEmailId(), FriendStatus.BLOCK + "");
        relationshipRepository.save(relationship);
        body.put("success", "true");
        return new ResponseEntity<>(body, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Map<String, Object>> retrieveEmails(RetrieveRequest retrieveRequest) {
        Map<String, Object> body = new HashMap<>();
        if (retrieveRequest.getSender() == null || retrieveRequest.getText() == null) {
            body.put("Error", "Invalid Request");
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
        Optional<Email> senderEmail = emailRepository.findByEmail(retrieveRequest.getSender());
        if (senderEmail.isEmpty()) {
            body.put("Error", "Email not existed");
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
        Set<String> emailList = emailUtil.getEmailsFromText(retrieveRequest.getText());
        emailList.removeIf(s -> emailRepository.findByEmail(s).isEmpty());
        List<FriendRelationship> relationshipList = relationshipRepository
                .findByEmailId(senderEmail.get().getEmailId());
        relationshipList.removeIf(friendRelationship
                -> friendRelationship.getStatus().contains(String.valueOf(FriendStatus.BLOCK)));
        List<String> listFriendsAndSubscribers = relationshipList.stream()
                .map(i -> emailRepository.findById(i.getFriendId()).get().getEmail())
                .collect(Collectors.toList());
        emailList.addAll(listFriendsAndSubscribers);
        if (emailList.size() == 0) {
            body.put("Error", "No recipients found for the given email ");
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
        body.put("success", "true");
        body.put("recipients", emailList);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }


    public List<String> getFriendsOfEmail(Email email) {
        return relationshipRepository
                .findByEmailIdAndStatus
                        (email.getEmailId(), String.valueOf(FriendStatus.FRIEND))
                .stream().map(i -> emailRepository.findById(i.getFriendId()).get().getEmail())
                .collect(Collectors.toList());
    }

}
