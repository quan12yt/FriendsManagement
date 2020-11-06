package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendRelationshipDTO {

    private Long relationshipId;
    @NotEmpty(message = "Email mustn't be empty or null")
    private Long emailId;
    @NotEmpty(message = "FriendEmail mustn't be empty or null")
    private long friendId;
}
