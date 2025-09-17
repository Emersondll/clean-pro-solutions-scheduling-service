package io.github.emersondll.auth.service;

import io.github.emersondll.auth.dto.SchedulingRequest;
import io.github.emersondll.auth.dto.SchedulingResponse;

import java.util.List;

public interface SchedulingService {
    SchedulingResponse create(SchedulingRequest request);
    SchedulingResponse findById(String id);
    List<SchedulingResponse> findByClientId(String clientId);
    List<SchedulingResponse> findByContractorId(String contractorId);
    SchedulingResponse update(String id, SchedulingRequest request);
    SchedulingResponse cancel(String id);
    SchedulingResponse complete(String id);
}