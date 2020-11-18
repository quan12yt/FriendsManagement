package com.example.demo.utils;

import com.example.demo.enums.FriendStatus;
import com.example.demo.model.Email;
import com.example.demo.repository.EmailRepository;
import com.example.demo.repository.FriendRelationshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class EmailUtils {

    @Autowired
    FriendRelationshipRepository relationshipRepository;

    @Autowired
    EmailRepository emailRepository;


    public Set<String> getEmailsFromText(String text) {
        Set<String> listEmail = new HashSet<>();
        Matcher matcher = Pattern
                .compile("[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}").matcher(text);
        while (matcher.find()) {
            listEmail.add(matcher.group());
        }
        return listEmail;
    }

    public List<String> getFriendsOfEmail(Email email) {
        return relationshipRepository
                .findByEmailIdAndStatus
                        (email.getEmailId(), String.valueOf(FriendStatus.FRIEND))
                .stream().map(i -> emailRepository.findById(i.getFriendId()).get().getEmail())
                .collect(Collectors.toList());
    }

}
