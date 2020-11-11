package com.example.demo.service;

import com.example.demo.payload.AddAndGetCommonRequest;
import com.example.demo.payload.EmailRequest;
import com.example.demo.payload.RetrieveRequest;
import com.example.demo.payload.SubscribeAndBlockRequest;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface EmailService {

    ResponseEntity<Map<String, Object>> getFriendList(EmailRequest emailRequest);

    ResponseEntity<Map<String, Object>> addFriend(AddAndGetCommonRequest friendRequest);

    ResponseEntity<Map<String, Object>> getCommonFriends(AddAndGetCommonRequest friendRequest);

    ResponseEntity<Map<String, Object>> subscribeTo(SubscribeAndBlockRequest subscribeRequest);

    ResponseEntity<Map<String, Object>> blockEmail(SubscribeAndBlockRequest subscribeRequest);

    ResponseEntity<Map<String, Object>> retrieveEmails(RetrieveRequest retrieveRequest);
}
