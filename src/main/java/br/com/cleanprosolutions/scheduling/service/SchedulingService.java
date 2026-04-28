package br.com.cleanprosolutions.scheduling.service;

import br.com.cleanprosolutions.scheduling.dto.SchedulingRequest;
import br.com.cleanprosolutions.scheduling.dto.SchedulingResponse;

import java.util.List;

/**
 * Service contract for scheduling operations.
 *
 * @author Clean Pro Solutions Team
 * @since 1.0.0
 */
public interface SchedulingService {

    /**
     * Creates a new scheduling and publishes a ScheduleCreated event.
     *
     * @param request scheduling details
     * @return created scheduling
     */
    SchedulingResponse create(SchedulingRequest request);

    /**
     * Retrieves a scheduling by ID.
     *
     * @param id scheduling ID
     * @return the scheduling
     */
    SchedulingResponse findById(String id);

    /**
     * Lists all schedulings for a specific client.
     *
     * @param clientId the client ID
     * @return list of schedulings
     */
    List<SchedulingResponse> findByClientId(String clientId);

    /**
     * Lists all schedulings for a specific contractor.
     *
     * @param contractorId the contractor ID
     * @return list of schedulings
     */
    List<SchedulingResponse> findByContractorId(String contractorId);

    /**
     * Updates an existing scheduling.
     *
     * @param id      scheduling ID
     * @param request updated details
     * @return updated scheduling
     */
    SchedulingResponse update(String id, SchedulingRequest request);

    /**
     * Cancels a scheduling.
     *
     * @param id scheduling ID
     * @return canceled scheduling
     */
    SchedulingResponse cancel(String id);

    /**
     * Marks a scheduling as completed.
     *
     * @param id scheduling ID
     * @return completed scheduling
     */
    SchedulingResponse complete(String id);
}
