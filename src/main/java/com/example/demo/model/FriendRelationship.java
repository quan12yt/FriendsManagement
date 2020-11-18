package com.example.demo.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "friend_relationship", schema = "public")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FriendRelationship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "relationship_id", nullable = false)
    private Long relationshipId;
    @Column(name = "email_id")
    private Long emailId;
    @Column(name = "friend_id")
    private Long friendId;
    @Column(name = "status")
    private String status;

    @ManyToOne
    @JoinColumn(name = "email_id", insertable = false, updatable = false)
    private Email email;

    @ManyToOne
    @JoinColumn(name = "email_id", insertable = false, updatable = false)
    private Email friendEmail;

    public FriendRelationship(Long emailId, Long friendId, String s) {
        this.emailId = emailId;
        this.friendId = friendId;
        this.status = s;
        this.setEmail(null);
        this.setFriendEmail(null);

    }
}
