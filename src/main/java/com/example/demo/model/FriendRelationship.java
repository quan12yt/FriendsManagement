package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "email" , schema = "public")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendRelationship {

    @Id
    @GeneratedValue
    @Column(name = "relationship_id")
    private Long relationshipId;
    @Column(name = "email_id")
    private Long emailId;
    @Column(name = "friend_id")
    private Long friendId;
    @Column(name = "status")
    private String status;

    @ManyToOne
    @JoinColumn(name = "email_id", insertable = false , updatable = false)
    private Email email;

    @ManyToOne
    @JoinColumn(name = "email_id", insertable = false , updatable = false)
    private Email friendEmail;
}
