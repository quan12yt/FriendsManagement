package com.example.demo.service.serviceimp;

import com.example.demo.exception.InputInvalidException;
import com.example.demo.exception.WrongStatusException;
import com.example.demo.model.Email;
import com.example.demo.model.FriendRelationship;
import com.example.demo.request.AddAndGetCommonRequest;
import com.example.demo.request.EmailRequest;
import com.example.demo.request.RetrieveRequest;
import com.example.demo.request.SubscribeAndBlockRequest;
import com.example.demo.repository.EmailRepository;
import com.example.demo.repository.FriendRelationshipRepository;
import com.example.demo.service.EmailService;
import com.example.demo.utils.EmailUtils;
import com.example.demo.utils.FriendStatus;
import org.springframework.beans.factory.annotation.Autowired;
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
    private EmailUtils emailUtil;


    @Override
    public List<String> getFriendList(EmailRequest emailRequest) {
        Optional<Email> emailOptional = emailRepository.findByEmail(emailRequest.getEmail());
        if (emailOptional.isEmpty()) {
            throw new InputInvalidException("Email not found in database");
        }
        Email email = emailOptional.get();
        return  relationshipRepository
                .findByEmailIdAndStatus(email.getEmailId()
                        , FriendStatus.FRIEND.name())
                .stream().map(i -> emailRepository.findById(i.getFriendId()).get().getEmail())
                .collect(Collectors.toList());
    }


    @Override
    public Boolean addFriend(AddAndGetCommonRequest friendRequest) {
        Optional<Email> optionalEmail1 = emailRepository.
                findByEmail(friendRequest.getFriends().get(0));
        Optional<Email> optionalEmail2 = emailRepository
                .findByEmail(friendRequest.getFriends().get(1));
        if (optionalEmail1.isEmpty() || optionalEmail2.isEmpty()) {
            throw new WrongStatusException("Both emails have to be in database");
        }
        Email email1 = optionalEmail1.get();
        Email email2 = optionalEmail2.get();
        Optional<FriendRelationship> friendRelationship = relationshipRepository
                .findByEmailIdAndFriendId(email1.getEmailId(), email2.getEmailId());
        if (friendRelationship.isPresent()) {
            if (friendRelationship.get().getStatus().contains(FriendStatus.BLOCK.name())) {
                throw new WrongStatusException("This email has been blocked !!");
            }
            if (friendRelationship.get().getStatus().contains(FriendStatus.FRIEND.name())) {
                throw new WrongStatusException("Two Email have already being friend");
            }
        }
        try {
            FriendRelationship relationship = new FriendRelationship
                    (email1.getEmailId(), email2.getEmailId(), FriendStatus.FRIEND.name());
            FriendRelationship inverseRelationship = new FriendRelationship
                    (email2.getEmailId(), email1.getEmailId(), FriendStatus.FRIEND.name());
            relationshipRepository.save(relationship);
            relationshipRepository.save(inverseRelationship);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public List<String> getCommonFriends(AddAndGetCommonRequest friendRequest) {
        Optional<Email> optionalEmail1 = emailRepository.
                findByEmail(friendRequest.getFriends().get(0));
        Optional<Email> optionalEmail2 = emailRepository
                .findByEmail(friendRequest.getFriends().get(1));
        if (optionalEmail1.isEmpty() || optionalEmail2.isEmpty()) {
            throw new WrongStatusException("Email not exist");
        }
        List<String> friendsOfEmail1 = this.emailUtil.getFriendsOfEmail(optionalEmail1.get());
        List<String> friendsOfEmail2 = this.emailUtil.getFriendsOfEmail(optionalEmail2.get());
        friendsOfEmail1.retainAll(friendsOfEmail2);
        return friendsOfEmail2;
    }

    @Override
    public Boolean subscribeTo(SubscribeAndBlockRequest subscribeRequest) {
        Optional<Email> optionalEmail1 = emailRepository.
                findByEmail(subscribeRequest.getRequester());
        Optional<Email> optionalEmail2 = emailRepository
                .findByEmail(subscribeRequest.getTarget());
        if (optionalEmail1.isEmpty() || optionalEmail2.isEmpty()) {
            throw new WrongStatusException("Requester or target email not existed");
        }
        Email requestEmail = optionalEmail1.get();
        Email targetEmail = optionalEmail2.get();
        Optional<FriendRelationship> friendRelationship = relationshipRepository
                .findByEmailIdAndFriendId(requestEmail.getEmailId(), targetEmail.getEmailId());
        if (friendRelationship.isPresent()) {
            if (friendRelationship.get().getStatus().contains(FriendStatus.BLOCK.name())) {
                throw new WrongStatusException("Target email has been blocked !!");
            }
            if (friendRelationship.get().getStatus().contains(FriendStatus.SUBSCRIBE.name())) {
                throw new WrongStatusException("Already subscribed to this target email !!");
            }
            if (friendRelationship.get().getStatus().contains(FriendStatus.FRIEND.name())) {
                throw new WrongStatusException("Already being friend of this target ,no need to subscribe  !!");
            }
        }
        FriendRelationship relationship = new FriendRelationship
                (requestEmail.getEmailId(), targetEmail.getEmailId(), FriendStatus.SUBSCRIBE.name());
        relationshipRepository.save(relationship);
        return true;

    }

    @Override
    public Boolean blockEmail(SubscribeAndBlockRequest subscribeRequest) {
        Optional<Email> optionalEmail1 = emailRepository.
                findByEmail(subscribeRequest.getRequester());
        Optional<Email> optionalEmail2 = emailRepository
                .findByEmail(subscribeRequest.getTarget());
        if (optionalEmail1.isEmpty() || optionalEmail2.isEmpty()) {
            throw new WrongStatusException("Requester or target email not existed");
        }
        Email requestEmail = optionalEmail1.get();
        Email targetEmail = optionalEmail2.get();
        Optional<FriendRelationship> friendRelationship = relationshipRepository
                .findByEmailIdAndFriendId(requestEmail.getEmailId(), targetEmail.getEmailId());
        if (friendRelationship.isPresent()) {
            if (friendRelationship.get().getStatus().contains(FriendStatus.BLOCK.name())) {
                throw new WrongStatusException("This email has already being blocked !!");
            }
            FriendRelationship friendRelationship1 = friendRelationship.get();
            friendRelationship1.setStatus(String.valueOf(FriendStatus.BLOCK));
            relationshipRepository.save(friendRelationship.get());
            return true;
        }

        FriendRelationship relationship = new FriendRelationship
                (requestEmail.getEmailId(), targetEmail.getEmailId(), FriendStatus.BLOCK.name());
        relationshipRepository.save(relationship);
        return true;
    }

    @Override
    public Set<String> retrieveEmails(RetrieveRequest retrieveRequest) {
        Optional<Email> senderEmail = emailRepository.findByEmail(retrieveRequest.getSender());
        if (senderEmail.isEmpty()) {
            throw new WrongStatusException("Email not existed");
        }
        Set<String> emailList = emailUtil.getEmailsFromText(retrieveRequest.getText());
        emailList.removeIf(s -> emailRepository.findByEmail(s).isEmpty());
        List<FriendRelationship> relationshipList = relationshipRepository
                .findByEmailId(senderEmail.get().getEmailId());
        relationshipList.removeIf(friendRelationship
                -> friendRelationship.getStatus().contains(FriendStatus.BLOCK.name()));
        List<String> listFriendsAndSubscribers = relationshipList.stream()
                .map(i -> emailRepository.findById(i.getFriendId()).get().getEmail())
                .collect(Collectors.toList());
        emailList.addAll(listFriendsAndSubscribers);
        if (emailList.isEmpty()) {
            throw new WrongStatusException("No recipients found for the given email ");
        }
        return emailList;
    }

}
