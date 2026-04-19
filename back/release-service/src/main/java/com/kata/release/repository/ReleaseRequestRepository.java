package com.kata.release.repository;

import com.kata.release.model.ReleaseRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReleaseRequestRepository extends JpaRepository<ReleaseRequest, UUID> {}
