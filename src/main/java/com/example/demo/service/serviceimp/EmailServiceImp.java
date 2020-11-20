package com.example.demo.service.serviceimp;

import com.example.demo.exception.EmailNotFoundException;
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
import com.example.demo.enums.FriendStatusEnum;
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
            throw new EmailNotFoundException("Email not found in database");
        }
        Email requestEmail = requestEmailOptional.get();
        return relationshipRepository
                .findByEmailIdAndStatus(requestEmail.getEmailId()
                        , FriendStatusEnum.FRIEND.name())
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
            throw new EmailNotFoundException("Both emails have to be in database");
        }
        Email email = optionalEmail.get();
        Email friendEmail = optionalFriendEmail.get();
        Optional<FriendRelationship> friendRelationship = relationshipRepository
                .findByEmailIdAndFriendId(email.getEmailId(), friendEmail.getEmailId());
        if (friendRelationship.isPresent()) {
            if (friendRelationship.get().getStatus
                    ().contains(FriendStatusEnum.BLOCK.name())) {
                throw new WrongRequirementException("This email has been blocked !!");
            }
            if (friendRelationship.get().getStatus().contains(FriendStatusEnum.FRIEND.name())) {
                throw new WrongRequirementException("Two Email have already being friend");
            }
        }
        try {
            FriendRelationship relationship = new FriendRelationship
                    (email.getEmailId(), friendEmail.getEmailId(), FriendStatusEnum.FRIEND.name());
            FriendRelationship inverseRelationship = new FriendRelationship
                    (friendEmail.getEmailId(), email.getEmailId(), FriendStatusEnum.FRIEND.name());
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
            throw new EmailNotFoundException("Email not exist");
        }
        List<String> friendsOfRequester = relationshipRepository
                .findByEmailIdAndStatus
                        (optionalRequester.get().getEmailId(), String.valueOf(FriendStatusEnum.FRIEND))
                .stream().map(i -> emailRepository.findById(i.getFriendId()).get().getEmail())
                .collect(Collectors.toList());
        List<String> friendsOfTarget = relationshipRepository
                .findByEmailIdAndStatus
                        (optionalTarget.get().getEmailId(), String.valueOf(FriendStatusEnum.FRIEND))
                .stream().map(i -> emailRepository.findById(i.getFriendId()).get().getEmail())
                .collect(Collectors.toList());
        friendsOfRequester.retainAll(friendsOfTarget);
        return friendsOfRequester;
    }

    @Override
    public Boolean subscribeTo(SubscribeAndBlockRequest subscribeRequest) {
        Optional<Email> optionalRequester = emailRepository.
                findByEmail(subscribeRequest.getRequester());
        Optional<Email> optionalTarget = emailRepository
                .findByEmail(subscribeRequest.getTarget());
        if (optionalRequester.isEmpty() || optionalTarget.isEmpty()) {
            throw new EmailNotFoundException("Requester or target email not existed");
        }
        Email requestEmail = optionalRequester.get();
        Email targetEmail = optionalTarget.get();
        Optional<FriendRelationship> friendRelationship = relationshipRepository
                .findByEmailIdAndFriendId(requestEmail.getEmailId(), targetEmail.getEmailId());
        if (friendRelationship.isPresent()) {
            if (friendRelationship.get().getStatus().contains(FriendStatusEnum.BLOCK.name())) {
                throw new WrongRequirementException("Target email has been blocked !!");
            }
            if (friendRelationship.get().getStatus().contains(FriendStatusEnum.SUBSCRIBE.name())) {
                throw new WrongRequirementException("Already subscribed to this target email !!");
            }
            if (friendRelationship.get().getStatus().contains(FriendStatusEnum.FRIEND.name())) {
                throw new WrongRequirementException("Already being friend of this target ,no need to subscribe  !!");
            }
        }
        FriendRelationship relationship = new FriendRelationship
                (requestEmail.getEmailId(), targetEmail.getEmailId(), FriendStatusEnum.SUBSCRIBE.name());
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
            throw new EmailNotFoundException("Requester or target email not existed");
        }
        Email requestEmail = optionalRequester.get();
        Email targetEmail = optionalTarget.get();
        Optional<FriendRelationship> friendRelationship = relationshipRepository
                .findByEmailIdAndFriendId(requestEmail.getEmailId(), targetEmail.getEmailId());
        if (friendRelationship.isPresent()) {
            if (friendRelationship.get().getStatus().contains(FriendStatusEnum.BLOCK.name())) {
                throw new WrongRequirementException("This email has already being blocked !!");
            }
            FriendRelationship friendRelationship1 = friendRelationship.get();
            friendRelationship1.setStatus(String.valueOf(FriendStatusEnum.BLOCK));
            relationshipRepository.save(friendRelationship.get());
            return true;
        }

        FriendRelationship relationship = new FriendRelationship
                (requestEmail.getEmailId(), targetEmail.getEmailId(), FriendStatusEnum.BLOCK.name());
        relationshipRepository.save(relationship);
        return true;
    }

    @Override
    public Set<String> retrieveEmails(RetrieveRequest retrieveRequest) {
        Optional<Email> senderEmail = emailRepository.findByEmail(retrieveRequest.getSender());
        if (senderEmail.isEmpty()) {
            throw new EmailNotFoundException("Sender mail not existed");
        }
        Set<String> emailList = emailUtil.getEmailsFromText(retrieveRequest.getText());
        emailList.removeIf(s -> emailRepository.findByEmail(s).isEmpty());
        List<FriendRelationship> relationshipList = relationshipRepository
                .findByEmailId(senderEmail.get().getEmailId());
        relationshipList.removeIf(friendRelationship
                -> friendRelationship.getStatus().contains(FriendStatusEnum.BLOCK.name()));
        List<String> listFriendsAndSubscribers = relationshipList.stream()
                .map(i -> emailRepository.findById(i.getFriendId()).get().getEmail())
                .collect(Collectors.toList());
        emailList.addAll(listFriendsAndSubscribers);
        return emailList;
    }

}
