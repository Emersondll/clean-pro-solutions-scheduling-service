package io.github.emersondll.auth.dto;

import io.github.emersondll.auth.enumerations.SchedulingStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record SchedulingResponse(
        UUID id,
        String clientId,
        String contractorId,
        String serviceId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        SchedulingStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}

