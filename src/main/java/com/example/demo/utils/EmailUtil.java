package com.example.demo.utils;

import com.example.demo.model.Email;
import com.example.demo.model.FriendRelationship;
import com.example.demo.repository.FriendRelationshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

public class EmailUtil {
    @Autowired
    public FriendRelationshipRepository relationshipRepository;

    public boolean isFriendOf(Email email1, Email email2) {
        return (email1.getFriends().contains(email2) || email2.getFriends().contains(email1));
    }

//    public FriendStatus getRelationshipStatus(Email email1, Email email2) {
//        FriendRelationship friendRelationship = relationshipRepository
//                    .findByEmailIdAndFriendId(email1.getEmailId(), email2.getEmailId());
//        return FriendStatus.valueOf(friendRelationship.getStatus());
//    }
}
