package com.example.demo.utils;

import com.example.demo.model.Email;
import com.example.demo.model.FriendRelationship;
import com.example.demo.repository.FriendRelationshipRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class EmailUtil {
    @Autowired
    FriendRelationshipRepository relationshipRepository;

    public boolean isFriendOf(Email email1, Email email2) {
        return (email1.getFriends().contains(email2) || email2.getFriends().contains(email1));
    }

    public FriendStatus getRelationshipStatus(Email email1, Email email2) {
        FriendRelationship friendRelationship = relationshipRepository
                .findByEmailIdAndFriendId(email1.getEmailId(), email2.getEmailId()).get(0);
        return FriendStatus.valueOf(friendRelationship.getStatus());
    }
}
