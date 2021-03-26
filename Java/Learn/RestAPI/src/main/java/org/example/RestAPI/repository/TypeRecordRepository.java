package org.example.RestAPI.repository;

import org.example.RestAPI.model.TypeRecord;
import org.example.RestAPI.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TypeRecordRepository extends JpaRepository<TypeRecord, Long> {
    @Query(value = "SELECT * FROM TYPERECORD WHERE typerecord_name = :typerecord_name", nativeQuery = true)
    Optional<TypeRecord> findByTyperecord_name(@Param("typerecord_name") String name);
}
