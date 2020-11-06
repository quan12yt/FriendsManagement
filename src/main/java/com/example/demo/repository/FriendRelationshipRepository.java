package com.example.demo.repository;

import com.example.demo.model.Email;
import com.example.demo.model.FriendRelationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendRelationshipRepository extends JpaRepository<FriendRelationship,Long> {
//    List<FriendRelationship> findByEmailId(Long emailId);
}
