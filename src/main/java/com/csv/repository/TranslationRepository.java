package com.csv.repository;

import com.csv.entity.TranslationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface TranslationRepository extends JpaRepository<TranslationEntity, String> {
    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE translation", nativeQuery = true)
    void truncateTable();
}
