package com.example.demo.service.serviceimp;

import com.example.demo.dto.EmailDTO;
import com.example.demo.model.Email;
import com.example.demo.repository.EmailRepository;
import com.example.demo.repository.FriendRelationshipRepository;
import com.example.demo.service.EmailService;
import com.example.demo.utils.ConverterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmailServiceImp implements EmailService {

    @Autowired
    EmailRepository emailRepository;
    @Autowired
    FriendRelationshipRepository friendRelationshipRepository;
    private ConverterUtils converter = new ConverterUtils();

    @Override
    public List<EmailDTO> getAllEmails() {
        return emailRepository.findAll().stream()
                .map(converter::convertToEmailDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getFriendList(String email) {
        Optional<Email> email1 = emailRepository.findByEmail(email);
        if (email1.isEmpty()) {
            return new ArrayList<>();
        }
        return email1.get().getFriends().stream()
                .map(i -> i.getEmail()).collect(Collectors.toList());
    }
}
