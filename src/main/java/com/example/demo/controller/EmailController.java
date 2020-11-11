package com.example.demo.controller;

import com.example.demo.payload.AddAndGetCommonRequest;
import com.example.demo.payload.EmailRequest;
import com.example.demo.payload.RetrieveRequest;
import com.example.demo.payload.SubscribeAndBlockRequest;
import com.example.demo.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/emails")
public class EmailController {
    @Autowired
    private EmailService emailService;

    // add a friend relationship
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addFriend(@Valid @RequestBody AddAndGetCommonRequest friendRequest) {
        return emailService.addFriend(friendRequest);
    }

    //get list mutual friends from 2 emails
    @PostMapping("/common")
    public ResponseEntity<Map<String, Object>> getCommonFriends(@Valid @RequestBody AddAndGetCommonRequest friendRequest) {
        return emailService.getCommonFriends(friendRequest);
    }

    //get list friends of an email
    @PostMapping("/friends")
    public ResponseEntity<Map<String, Object>> getFriends(@Valid @RequestBody EmailRequest emailRequest) {
        return emailService.getFriendList(emailRequest);
    }

    //subscribe to an email
    @PostMapping("/subscribe")
    public ResponseEntity<Map<String, Object>> subscribeTo(@Valid @RequestBody SubscribeAndBlockRequest subscribeRequest) {
        return emailService.subscribeTo(subscribeRequest);
    }

    //block an email
    @PostMapping("/block")
    public ResponseEntity<Map<String, Object>> blockEmail(@Valid @RequestBody SubscribeAndBlockRequest subscribeRequest) {
        return emailService.blockEmail(subscribeRequest);
    }

    //retrieve contacted email addresses of an email
    @PostMapping("/retrieve")
    public ResponseEntity<Map<String, Object>> retrieveEmail(@Valid @RequestBody RetrieveRequest retrieveRequest) {
        return emailService.retrieveEmails(retrieveRequest);
    }
}
