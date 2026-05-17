package br.com.cleanprosolutions.scheduling.dto;

import br.com.cleanprosolutions.scheduling.enumerations.RecurrencePattern;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

/**
 * Request DTO for creating or updating a service schedule.
 *
 * @param clientId          ID of the client requesting the service
 * @param contractorId      ID of the contractor providing the service
 * @param serviceId         ID of the catalog service
 * @param startTime         Start time of the scheduled service
 * @param endTime           End time of the scheduled service
 * @param recurrencePattern Recurrence rule; defaults to {@link RecurrencePattern#NONE}
 *
 * @author Emerson Lima
 * @since 1.0.0
 */
public record SchedulingRequest(
        @NotBlank(message = "Client ID is required")
        String clientId,

        @NotBlank(message = "Contractor ID is required")
        String contractorId,

        @NotBlank(message = "Service ID is required")
        String serviceId,

        @NotNull(message = "Start time is required")
        @Future(message = "Start time must be in the future")
        Instant startTime,

        @NotNull(message = "End time is required")
        @Future(message = "End time must be in the future")
        Instant endTime,

        RecurrencePattern recurrencePattern
) {}
