package com.example.demo.utils;

public class ConverterUtils {

//    @Autowired
//    private EmailRepository emailRepository;
//    @Autowired
//    private FriendRelationshipRepository relationshipRepository;
//    @Autowired
//    private ModelMapper modelMapper = new ModelMapper();
//
//    public EmailDTO convertToEmailDTO(Email email) {
//        return modelMapper.map(email, EmailDTO.class);
//    }
//
//    public Email convertToEmailEntity(EmailDTO emailDTO) {
//        Email email = modelMapper.map(emailDTO, Email.class);
//        Optional<Email> tmp = emailRepository.findByEmail(emailDTO.getEmail());
//        if (tmp.isPresent()) {
//            email.setEmailId(tmp.get().getEmailId());
//            email.setFriends(tmp.get().getFriends());
//        }
//        return email;
//    }
//
//    public FriendRelationshipDTO convertToRelationshipDTO(FriendRelationship relationship) {
//        return modelMapper.map(relationship, FriendRelationshipDTO.class);
//    }
//
//    public FriendRelationship convertToRelationshipEntity(FriendRelationshipDTO friendRelationshipDTO) {
//        FriendRelationship friendRelationship = modelMapper.map(friendRelationshipDTO, FriendRelationship.class);
//        Optional<FriendRelationship> tmp = relationshipRepository.findById(friendRelationshipDTO.getRelationshipId());
//        if (tmp.isPresent()) {
//            friendRelationship.setEmail(tmp.get().getEmail());
//            friendRelationship.setFriendEmail(tmp.get().getFriendEmail());
//        }
//        return friendRelationship;
//    }

}
