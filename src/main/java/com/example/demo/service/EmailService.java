package com.example.demo.service;

import com.example.demo.dto.request.AddAndGetCommonRequest;
import com.example.demo.dto.request.EmailRequest;
import com.example.demo.dto.request.RetrieveRequest;
import com.example.demo.dto.request.SubscribeAndBlockRequest;

import java.util.List;
import java.util.Set;

public interface EmailService {

    List<String> getFriendList(EmailRequest emailRequest);

    Boolean addFriend(AddAndGetCommonRequest friendRequest);

    List<String> getCommonFriends(AddAndGetCommonRequest friendRequest);

    Boolean subscribeTo(SubscribeAndBlockRequest subscribeRequest);

    Boolean blockEmail(SubscribeAndBlockRequest subscribeRequest);

    Set<String> retrieveEmails(RetrieveRequest retrieveRequest);
}
