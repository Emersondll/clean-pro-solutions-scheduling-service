package br.com.cleanprosolutions.scheduling.repository;

import br.com.cleanprosolutions.scheduling.document.Scheduling;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for {@link Scheduling} document persistence.
 *
 * @author Clean Pro Solutions Team
 * @since 1.0.0
 */
@Repository
public interface SchedulingRepository extends MongoRepository<Scheduling, String> {

    /**
     * Finds all schedulings for a specific client.
     *
     * @param clientId the client ID
     * @return list of schedulings
     */
    List<Scheduling> findByClientId(String clientId);

    /**
     * Finds all schedulings for a specific contractor.
     *
     * @param contractorId the contractor ID
     * @return list of schedulings
     */
    List<Scheduling> findByContractorId(String contractorId);
}
