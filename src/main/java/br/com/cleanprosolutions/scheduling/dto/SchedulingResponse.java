package br.com.cleanprosolutions.scheduling.dto;

import br.com.cleanprosolutions.scheduling.enumerations.SchedulingStatus;

import java.time.Instant;

/**
 * Response DTO representing a service schedule.
 *
 * @param id           MongoDB unique identifier
 * @param clientId     Client ID
 * @param contractorId Contractor ID
 * @param serviceId    Catalog service ID
 * @param startTime    Start time
 * @param endTime      End time
 * @param status       Current scheduling status
 * @param createdAt    Creation timestamp
 * @param updatedAt    Last update timestamp
 *
 * @author Clean Pro Solutions Team
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
        Instant createdAt,
        Instant updatedAt
) {}
