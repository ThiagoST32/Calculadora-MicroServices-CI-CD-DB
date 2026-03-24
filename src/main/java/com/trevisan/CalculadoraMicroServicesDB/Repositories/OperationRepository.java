package com.trevisan.CalculadoraMicroServicesDB.Repositories;

import com.trevisan.CalculadoraMicroServicesDB.Domain.Operations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationRepository extends JpaRepository<Operations, Long> {
    @Query(value = "SELECT * FROM operations ORDER BY local_date_time DESC LIMIT 1", nativeQuery = true)
    Operations findPreviousOperationsPersisted();
}
