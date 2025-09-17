package io.github.emersondll.auth.repository;


import io.github.emersondll.auth.document.Scheduling;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SchedulingRepository extends MongoRepository<Scheduling, String> {
    List<Scheduling> findByClientId(String clientId);
    List<Scheduling> findByContractorId(String contractorId);
}