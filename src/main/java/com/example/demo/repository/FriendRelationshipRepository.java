package com.example.demo.repository;

import com.example.demo.model.FriendRelationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRelationshipRepository extends JpaRepository<FriendRelationship, Long> {
    Optional<FriendRelationship> findByEmailIdAndFriendId( Long emailId,Long friendId);
}
