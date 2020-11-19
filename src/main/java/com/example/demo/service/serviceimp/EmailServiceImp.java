package com.example.demo.service.serviceimp;

import com.example.demo.exception.WrongRequirementException;
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
import com.example.demo.enums.FriendStatus;
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
        Optional<Email> requestEmailOptional = emailRepository.findByEmail(emailRequest.getEmail());
        if (requestEmailOptional.isEmpty()) {
            throw new WrongRequirementException("Email not found in database");
        }
        Email requestEmail = requestEmailOptional.get();
        return relationshipRepository
                .findByEmailIdAndStatus(requestEmail.getEmailId()
                        , FriendStatus.FRIEND.name())
                .stream().map(i -> emailRepository.findById(i.getFriendId()).get().getEmail())
                .collect(Collectors.toList());
    }


    @Override
    public Boolean addFriend(AddAndGetCommonRequest friendRequest) {
        Optional<Email> optionalEmail = emailRepository.
                findByEmail(friendRequest.getFriends().get(0));
        Optional<Email> optionalFriendEmail = emailRepository
                .findByEmail(friendRequest.getFriends().get(1));
        if (optionalEmail.isEmpty() || optionalFriendEmail.isEmpty()) {
            throw new WrongRequirementException("Both emails have to be in database");
        }
        Email email = optionalEmail.get();
        Email friendEmail = optionalFriendEmail.get();
        Optional<FriendRelationship> friendRelationship = relationshipRepository
                .findByEmailIdAndFriendId(email.getEmailId(), friendEmail.getEmailId());
        if (friendRelationship.isPresent()) {
            if (friendRelationship.get().getStatus
                    ().contains(FriendStatus.BLOCK.name())) {
                throw new WrongRequirementException("This email has been blocked !!");
            }
            if (friendRelationship.get().getStatus().contains(FriendStatus.FRIEND.name())) {
                throw new WrongRequirementException("Two Email have already being friend");
            }
        }
        try {
            FriendRelationship relationship = new FriendRelationship
                    (email.getEmailId(), friendEmail.getEmailId(), FriendStatus.FRIEND.name());
            FriendRelationship inverseRelationship = new FriendRelationship
                    (friendEmail.getEmailId(), email.getEmailId(), FriendStatus.FRIEND.name());
            relationshipRepository.save(relationship);
            relationshipRepository.save(inverseRelationship);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public List<String> getCommonFriends(AddAndGetCommonRequest friendRequest) {
        Optional<Email> optionalRequester = emailRepository.
                findByEmail(friendRequest.getFriends().get(0));
        Optional<Email> optionalTarget = emailRepository
                .findByEmail(friendRequest.getFriends().get(1));
        if (optionalRequester.isEmpty() || optionalTarget.isEmpty()) {
            throw new WrongRequirementException("Email not exist");
        }
        List<String> friendsOfEmail1 = this.emailUtil.getFriendsOfEmail(optionalRequester.get());
        List<String> friendsOfEmail2 = this.emailUtil.getFriendsOfEmail(optionalTarget.get());
        friendsOfEmail1.retainAll(friendsOfEmail2);
        return friendsOfEmail1;
    }

    @Override
    public Boolean subscribeTo(SubscribeAndBlockRequest subscribeRequest) {
        Optional<Email> optionalRequester = emailRepository.
                findByEmail(subscribeRequest.getRequester());
        Optional<Email> optionalTarget = emailRepository
                .findByEmail(subscribeRequest.getTarget());
        if (optionalRequester.isEmpty() || optionalTarget.isEmpty()) {
            throw new WrongRequirementException("Requester or target email not existed");
        }
        Email requestEmail = optionalRequester.get();
        Email targetEmail = optionalTarget.get();
        Optional<FriendRelationship> friendRelationship = relationshipRepository
                .findByEmailIdAndFriendId(requestEmail.getEmailId(), targetEmail.getEmailId());
        if (friendRelationship.isPresent()) {
            if (friendRelationship.get().getStatus().contains(FriendStatus.BLOCK.name())) {
                throw new WrongRequirementException("Target email has been blocked !!");
            }
            if (friendRelationship.get().getStatus().contains(FriendStatus.SUBSCRIBE.name())) {
                throw new WrongRequirementException("Already subscribed to this target email !!");
            }
            if (friendRelationship.get().getStatus().contains(FriendStatus.FRIEND.name())) {
                throw new WrongRequirementException("Already being friend of this target ,no need to subscribe  !!");
            }
        }
        FriendRelationship relationship = new FriendRelationship
                (requestEmail.getEmailId(), targetEmail.getEmailId(), FriendStatus.SUBSCRIBE.name());
        relationshipRepository.save(relationship);
        return true;

    }

    @Override
    public Boolean blockEmail(SubscribeAndBlockRequest subscribeRequest) {
        Optional<Email> optionalRequester = emailRepository.
                findByEmail(subscribeRequest.getRequester());
        Optional<Email> optionalTarget = emailRepository
                .findByEmail(subscribeRequest.getTarget());
        if (optionalRequester.isEmpty() || optionalTarget.isEmpty()) {
            throw new WrongRequirementException("Requester or target email not existed");
        }
        Email requestEmail = optionalRequester.get();
        Email targetEmail = optionalTarget.get();
        Optional<FriendRelationship> friendRelationship = relationshipRepository
                .findByEmailIdAndFriendId(requestEmail.getEmailId(), targetEmail.getEmailId());
        if (friendRelationship.isPresent()) {
            if (friendRelationship.get().getStatus().contains(FriendStatus.BLOCK.name())) {
                throw new WrongRequirementException("This email has already being blocked !!");
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
            throw new WrongRequirementException("Email not existed");
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
            throw new WrongRequirementException("No recipients found for the given email ");
        }
        return emailList;
    }

}
