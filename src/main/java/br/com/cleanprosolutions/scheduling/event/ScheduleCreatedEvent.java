package br.com.cleanprosolutions.scheduling.event;

import java.time.Instant;

/**
 * Event published when a new scheduling is created.
 *
 * <p>Consumable by the {@code availability-service} to block the time slot,
 * and by the {@code notification-service} to alert the contractor.</p>
 *
 * @param eventId      Unique event ID for idempotency
 * @param schedulingId The created scheduling ID
 * @param contractorId The contractor's ID
 * @param startTime    Start time of the scheduled service
 * @param endTime      End time of the scheduled service
 * @param timestamp    Event creation timestamp
 *
 * @author Clean Pro Solutions Team
 * @since 1.0.0
 */
public record ScheduleCreatedEvent(
        String eventId,
        String schedulingId,
        String contractorId,
        Instant startTime,
        Instant endTime,
        Instant timestamp
) {}
