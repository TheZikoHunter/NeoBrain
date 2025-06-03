package com.code.backend.repository;

import com.code.backend.entity.Reclamation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface ReclamationRepository extends JpaRepository<Reclamation, Long> {
}
