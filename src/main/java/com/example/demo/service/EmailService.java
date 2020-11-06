package com.example.demo.service;

import com.example.demo.dto.EmailDTO;
import com.example.demo.dto.FriendRelationshipDTO;
import com.example.demo.model.Email;

import java.util.List;

public interface EmailService {
    List<EmailDTO> getAllEmails();
    List<String> getFriendList(String email);
}
