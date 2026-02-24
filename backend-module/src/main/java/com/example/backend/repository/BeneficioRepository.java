package com.example.backend.repository;

import com.example.backend.entity.Beneficio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.List;

public interface BeneficioRepository extends JpaRepository<Beneficio, Long> {

    List<Beneficio> findByAtivoTrueOrderByNomeAsc();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Beneficio b WHERE b.id = :id")
    Beneficio findByIdForUpdate(@Param("id") Long id);
}
