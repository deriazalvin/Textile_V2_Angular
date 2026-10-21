package com.example.textile.repository;

import com.example.textile.entity.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClientRepository extends JpaRepository<Client, Long> {

    boolean existsByPhone(String phone);

    boolean existsByEmail(String email);

    boolean existsByPhoneAndIdNot(String phone, Long id);

    @Query("""
            SELECT c FROM Client c
            WHERE (:search IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%'))
                            OR LOWER(c.email) LIKE LOWER(CONCAT('%', :search, '%'))
                            OR c.phone LIKE CONCAT('%', :search, '%'))
              AND (:active IS NULL OR c.active = :active)
            """)
    Page<Client> search(@Param("search") String search,
                        @Param("active") Boolean active,
                        Pageable pageable);
}