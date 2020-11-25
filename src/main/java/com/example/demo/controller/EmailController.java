package com.example.demo.controller;

import com.example.demo.exception.InputInvalidException;
import com.example.demo.request.AddAndGetCommonRequest;
import com.example.demo.request.EmailRequest;
import com.example.demo.request.RetrieveRequest;
import com.example.demo.request.SubscribeAndBlockRequest;
import com.example.demo.response.GetFriendsAndCommonResponse;
import com.example.demo.response.RetrieveEmailResponse;
import com.example.demo.response.SuccessResponse;
import com.example.demo.service.EmailService;
import com.example.demo.utils.EmailValidation;
import com.example.demo.utils.RequestValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/emails")
public class EmailController {
    @Autowired
    private EmailService emailService;

    // add a friend relationship
    @PostMapping("/add")
    @PreAuthorize("hasRole('MEMBER') or hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse> addFriend(@Valid @RequestBody AddAndGetCommonRequest friendRequest) {
        String error = RequestValidation.checkAddAndSubscribeRequest(friendRequest);
        if (!error.equals("")) {
            throw new InputInvalidException(error);
        }
        return new ResponseEntity<>(new SuccessResponse
                (emailService.addFriend(friendRequest).toString()), HttpStatus.CREATED);
    }

    //get list mutual friends from 2 emails
    @PostMapping("/common")
    @PreAuthorize("hasRole('MEMBER') or hasRole('ADMIN')")
    public ResponseEntity<GetFriendsAndCommonResponse> getCommonFriends(@Valid @RequestBody AddAndGetCommonRequest friendRequest) {
        String error = RequestValidation.checkAddAndSubscribeRequest(friendRequest);
        if (!error.equals("")) {
            throw new InputInvalidException(error);
        }
        List<String> listEmails = emailService.getCommonFriends(friendRequest);
        if (listEmails.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return new ResponseEntity<>(new GetFriendsAndCommonResponse
                ("true", listEmails, listEmails.size()), HttpStatus.OK);
    }

    //get list friends of an email
    @PostMapping("/friends")
    @PreAuthorize("hasRole('MEMBER') or hasRole('ADMIN')")
    public ResponseEntity<GetFriendsAndCommonResponse> getFriends(@Valid @RequestBody EmailRequest emailRequest) {
        if (emailRequest == null) {
            throw new InputInvalidException("Invalid request");
        }
        if (!EmailValidation.isEmail(emailRequest.getEmail())) {
            throw new InputInvalidException("Invalid email");
        }
        List<String> listEmails = emailService.getFriendList(emailRequest);
        if (listEmails.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return new ResponseEntity<>(new GetFriendsAndCommonResponse
                ("true", listEmails, listEmails.size()), HttpStatus.OK);
    }

    //subscribe to an email
    @PutMapping("/subscribe")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse> subscribeTo(@Valid @RequestBody SubscribeAndBlockRequest subscribeRequest) {
        String error = RequestValidation.checkSubscribeAndBlockRequest(subscribeRequest);
        if (!error.equals("")) {
            throw new InputInvalidException(error);
        }
        return new ResponseEntity<>(new SuccessResponse
                (emailService.subscribeTo(subscribeRequest).toString()), HttpStatus.CREATED);
    }

    //block an email
    @PutMapping("/block")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse> blockEmail(@Valid @RequestBody SubscribeAndBlockRequest subscribeRequest) {
        String error = RequestValidation.checkSubscribeAndBlockRequest(subscribeRequest);
        if (!error.equals("")) {
            throw new InputInvalidException(error);
        }
        return new ResponseEntity<>(new SuccessResponse
                (emailService.blockEmail(subscribeRequest).toString()), HttpStatus.CREATED);
    }

    //retrieve contacted email addresses of an email
    @PostMapping("/retrieve")
    @PreAuthorize("hasRole('MEMBER') or hasRole('ADMIN')")
    public ResponseEntity<RetrieveEmailResponse> retrieveEmail(@Valid @RequestBody RetrieveRequest retrieveRequest) {
        String error = RequestValidation.checkRetrieveRequest(retrieveRequest);
        if (!error.equals("")) {
            throw new InputInvalidException(error);
        }
        Set<String> setEmails = emailService.retrieveEmails(retrieveRequest);
        if (setEmails.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return new ResponseEntity<>(new RetrieveEmailResponse
                ("true", setEmails), HttpStatus.OK);
    }
}
