package com.example.demo.utils;

import com.example.demo.dto.EmailDTO;
import com.example.demo.dto.FriendRelationshipDTO;
import com.example.demo.model.Email;
import com.example.demo.model.FriendRelationship;
import com.example.demo.repository.EmailRepository;
import com.example.demo.repository.FriendRelationshipRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class ConverterUtils {

    @Autowired
    private EmailRepository emailRepository;
    @Autowired
    private FriendRelationshipRepository relationshipRepository;
    @Autowired
    private ModelMapper modelMapper = new ModelMapper();

    public EmailDTO convertToEmailDTO(Email email) {
        return modelMapper.map(email, EmailDTO.class);
    }

    public Email convertToEmailEntity(EmailDTO emailDTO) {
        Email email = modelMapper.map(emailDTO, Email.class);
        Optional<Email> tmp = emailRepository.findByEmail(emailDTO.getEmail());
        if (tmp.isPresent()) {
            email.setEmailId(tmp.get().getEmailId());
            email.setFriends(tmp.get().getFriends());
        }
        return email;
    }

    public FriendRelationshipDTO convertToRelationshipDTO(FriendRelationship relationship) {
        return modelMapper.map(relationship, FriendRelationshipDTO.class);
    }

    public FriendRelationship convertToRelationshipEntity(FriendRelationshipDTO friendRelationshipDTO) {
        FriendRelationship friendRelationship = modelMapper.map(friendRelationshipDTO, FriendRelationship.class);
        Optional<FriendRelationship> tmp = relationshipRepository.findById(friendRelationshipDTO.getRelationshipId());
        if (tmp.isPresent()) {
            friendRelationship.setEmail(tmp.get().getEmail());
            friendRelationship.setFriendEmail(tmp.get().getFriendEmail());
        }
        return friendRelationship;
    }

}
