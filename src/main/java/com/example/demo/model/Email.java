package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "email", schema = "public")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Email {

    @Id
    @GeneratedValue
    @Column(name = "email_id")
    private Long emailId;

    @Column(name = "email")
    private String email;

    @ManyToMany
    @JoinTable(name = "friend_relationship" , joinColumns = @JoinColumn(name = "email_id"),inverseJoinColumns = @JoinColumn(name = "friend_id"))
    @JsonIgnoreProperties("friends")
    private Set<Email> friends;
}
