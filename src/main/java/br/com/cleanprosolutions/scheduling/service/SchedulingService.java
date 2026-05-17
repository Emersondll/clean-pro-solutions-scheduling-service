package br.com.cleanprosolutions.scheduling.service;

import br.com.cleanprosolutions.scheduling.dto.SchedulingRequest;
import br.com.cleanprosolutions.scheduling.dto.SchedulingResponse;

import java.util.List;

/**
 * Service contract for scheduling operations.
 *
 * @author Emerson Lima
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

    /**
     * Creates a series of recurring schedulings from a single request.
     *
     * <p>The first occurrence is the parent (event published). Subsequent occurrences
     * are linked via {@code parentSchedulingId} and offset by the recurrence pattern.</p>
     *
     * @param request     scheduling details — must have a non-{@code NONE} recurrence pattern
     * @param occurrences total number of occurrences to generate (including the first)
     * @return list of created schedulings ordered chronologically
     * @throws IllegalArgumentException if recurrence pattern is {@code NONE}
     */
    List<SchedulingResponse> createRecurring(SchedulingRequest request, int occurrences);
}
