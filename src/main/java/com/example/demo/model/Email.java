package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "email", schema = "public")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Email {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "email_id", nullable = false)
    private Long emailId;

    @Column(name = "email")
    private String email;

    @ManyToMany
    @JoinTable(name = "friend_relationship", joinColumns = @JoinColumn(name = "email_id"), inverseJoinColumns = @JoinColumn(name = "friend_id"))
    @JsonIgnoreProperties("emailOne")
    private Set<Email> friends;




}
