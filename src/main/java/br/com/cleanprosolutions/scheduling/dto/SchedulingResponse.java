package br.com.cleanprosolutions.scheduling.dto;

import br.com.cleanprosolutions.scheduling.enumerations.RecurrencePattern;
import br.com.cleanprosolutions.scheduling.enumerations.SchedulingStatus;

import java.time.Instant;

/**
 * Response DTO representing a service schedule.
 *
 * @param id                  MongoDB unique identifier
 * @param clientId            Client ID
 * @param contractorId        Contractor ID
 * @param serviceId           Catalog service ID
 * @param startTime           Start time
 * @param endTime             End time
 * @param status              Current scheduling status
 * @param recurrencePattern   Recurrence rule for this scheduling
 * @param parentSchedulingId  ID of the originating scheduling for recurring instances
 * @param createdAt           Creation timestamp
 * @param updatedAt           Last update timestamp
 *
 * @author Emerson Lima
 * @since 1.0.0
 */
public record SchedulingResponse(
        String id,
        String clientId,
        String contractorId,
        String serviceId,
        Instant startTime,
        Instant endTime,
        SchedulingStatus status,
        RecurrencePattern recurrencePattern,
        String parentSchedulingId,
        Instant createdAt,
        Instant updatedAt
) {}
