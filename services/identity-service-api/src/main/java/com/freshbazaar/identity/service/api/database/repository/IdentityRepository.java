package com.freshbazaar.identity.service.api.database.repository;

import com.freshbazaar.identity.service.api.database.entity.Identity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IdentityRepository extends JpaRepository<Identity, UUID> {

}
