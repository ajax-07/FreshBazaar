package com.freshbazaar.identity.repository;

import com.freshbazaar.identity.entity.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthRepository extends JpaRepository<UserCredential, UUID> {
    Optional<UserCredential> findByUserName(String userName);
}
