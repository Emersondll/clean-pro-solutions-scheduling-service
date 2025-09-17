package io.github.emersondll.auth.dto;

import java.time.LocalDateTime;

public record SchedulingRequest(
        String clientId,
        String contractorId,
        String serviceId,
        LocalDateTime startTime,
        LocalDateTime endTime
) {}
