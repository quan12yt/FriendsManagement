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
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/emails")
public class EmailController {
    @Autowired
    private EmailService emailService;

    // add a friend relationship
    @PostMapping("/add")
    public ResponseEntity<SuccessResponse> addFriend(@Valid @RequestBody AddAndGetCommonRequest friendRequest) {
        String error = RequestValidation.checkAddAndSubscribeRequest(friendRequest);
        if (!(error == "")) {
            throw new InputInvalidException(error);
        }
        return new ResponseEntity<>(new SuccessResponse
                (emailService.addFriend(friendRequest).toString()), HttpStatus.CREATED);
    }

    //get list mutual friends from 2 emails
    @PostMapping("/common")
    public ResponseEntity<GetFriendsAndCommonResponse> getCommonFriends(@Valid @RequestBody AddAndGetCommonRequest friendRequest) {
        String error = RequestValidation.checkAddAndSubscribeRequest(friendRequest);
        if (!(error == "")) {
            throw new InputInvalidException(error);
        }
        List<String> listEmails = emailService.getCommonFriends(friendRequest);
        return new ResponseEntity<>(new GetFriendsAndCommonResponse
                ("true", listEmails, listEmails.size()), HttpStatus.OK);
    }

    //get list friends of an email
    @PostMapping("/friends")
    public ResponseEntity<GetFriendsAndCommonResponse> getFriends(@Valid @RequestBody EmailRequest emailRequest) {
        if (emailRequest == null) {
            throw new InputInvalidException("Invalid request");
        }
        if (!EmailValidation.isEmail(emailRequest.getEmail())) {
            throw new InputInvalidException("Invalid email");
        }
        List<String> listEmails = emailService.getFriendList(emailRequest);

        return new ResponseEntity<>(new GetFriendsAndCommonResponse
                ("true", listEmails, listEmails.size()), HttpStatus.OK);
    }

    //subscribe to an email
    @PutMapping("/subscribe")
    public ResponseEntity<SuccessResponse> subscribeTo(@Valid @RequestBody SubscribeAndBlockRequest subscribeRequest) {
        String error = RequestValidation.checkSubscribeAndBlockRequest(subscribeRequest);
        if (!(error == "")) {
            throw new InputInvalidException(error);
        }
        return new ResponseEntity<>(new SuccessResponse
                (emailService.subscribeTo(subscribeRequest).toString()), HttpStatus.CREATED);
    }

    //block an email
    @PutMapping("/block")
    public ResponseEntity<SuccessResponse> blockEmail(@Valid @RequestBody SubscribeAndBlockRequest subscribeRequest) {
        String error = RequestValidation.checkSubscribeAndBlockRequest(subscribeRequest);
        if (!(error == "")) {
            throw new InputInvalidException(error);
        }
        return new ResponseEntity<>(new SuccessResponse
                (emailService.blockEmail(subscribeRequest).toString()), HttpStatus.CREATED);
    }

    //retrieve contacted email addresses of an email
    @PostMapping("/retrieve")
    public ResponseEntity<RetrieveEmailResponse> retrieveEmail(@Valid @RequestBody RetrieveRequest retrieveRequest) {
        String error = RequestValidation.checkRetrieveRequest(retrieveRequest);
        if (!(error == "")) {
            throw new InputInvalidException(error);
        }
        return new ResponseEntity<>(new RetrieveEmailResponse
                ("true", emailService.retrieveEmails(retrieveRequest)), HttpStatus.OK);
    }
}
