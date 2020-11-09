package com.example.demo.controller;

import com.example.demo.dto.EmailDTO;
import com.example.demo.model.FriendRelationship;
import com.example.demo.payload.AddFriendRequest;
import com.example.demo.payload.EmailRequest;
import com.example.demo.repository.FriendRelationshipRepository;
import com.example.demo.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/emails")
public class EmailController {
    @Autowired
    private EmailService emailService;
    @Autowired
    private FriendRelationshipRepository relationshipRepository;

    @GetMapping
    public ResponseEntity<?> getAllEmails() {
        List<EmailDTO> emailDTOS = emailService.getAllEmails();
        if (emailDTOS.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(emailDTOS);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> addFriend(@Valid @RequestBody AddFriendRequest friendRequest){
        return emailService.addFriend(friendRequest);
    }
    @PostMapping("/common")
    public ResponseEntity<Map<String, Object>> getCommonFriends(@Valid @RequestBody AddFriendRequest friendRequest){
        return emailService.getCommonFriends(friendRequest);
    }

    @PostMapping("/friends")
    public ResponseEntity<Map<String, Object>> getFriends(@Valid @RequestBody EmailRequest emailRequest){
        return emailService.getFriendList(emailRequest);
    }

//    @GetMapping("/re")
//    public ResponseEntity<?> getAllRelationship() {
//        List<String> emailDTOS = relationshipRepository.findAll().stream().map(i -> i.getStatus()).collect(Collectors.toList());
//
//        return ResponseEntity.ok(emailDTOS);
//    }
}
