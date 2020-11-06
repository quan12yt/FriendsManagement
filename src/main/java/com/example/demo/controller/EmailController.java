package com.example.demo.controller;

import com.example.demo.dto.EmailDTO;
import com.example.demo.dto.FriendRelationshipDTO;
import com.example.demo.model.Email;
import com.example.demo.payload.EmailRequest;
import com.example.demo.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/emails")
public class EmailController {
    @Autowired
    private EmailService emailService;

    @GetMapping
    public ResponseEntity<?> getAllEmails() {
        List<EmailDTO> emailDTOS = emailService.getAllEmails();
        if (emailDTOS.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(emailDTOS);
    }

    @GetMapping("/friends")
    public ResponseEntity<Map<String, Object>> getFriendListByEmail(@Valid @RequestBody EmailRequest email) {
        List<String> tmp = emailService.getFriendList(email.getEmail());
        Map<String, Object> body = new HashMap<>();
        if (tmp.isEmpty()) {
            body.put("Error","Invalid request");
            return new ResponseEntity<Map<String, Object>>(body, HttpStatus.BAD_REQUEST);
        }
        body.put("success","true");
        body.put("friends",tmp);
        body.put("count",tmp.size());
        return new ResponseEntity<Map<String, Object>>(body, HttpStatus.OK);

    }

}
