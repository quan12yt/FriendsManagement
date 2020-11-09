package com.example.demo.service;

import com.example.demo.dto.EmailDTO;
import com.example.demo.dto.FriendRelationshipDTO;
import com.example.demo.model.Email;
import com.example.demo.payload.AddFriendRequest;
import com.example.demo.payload.EmailRequest;
import com.example.demo.payload.SubscribeRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface EmailService {
    List<EmailDTO> getAllEmails();
    ResponseEntity<Map<String, Object>> getFriendList(EmailRequest emailRequest);
    ResponseEntity<Map<String, Object>> addFriend(AddFriendRequest friendRequest);
    ResponseEntity<Map<String, Object>> getCommonFriends(AddFriendRequest friendRequest);
    ResponseEntity<Map<String, Object>> subscribeTo(SubscribeRequest subscribeRequest);
}
