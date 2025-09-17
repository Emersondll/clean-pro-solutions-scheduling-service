package io.github.emersondll.auth.service.impl;

import io.github.emersondll.auth.document.Scheduling;
import io.github.emersondll.auth.dto.SchedulingRequest;
import io.github.emersondll.auth.dto.SchedulingResponse;
import io.github.emersondll.auth.enumerations.SchedulingStatus;
import io.github.emersondll.auth.exception.SchedulingNotFoundException;
import io.github.emersondll.auth.repository.SchedulingRepository;
import io.github.emersondll.auth.service.SchedulingService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SchedulingServiceImpl implements SchedulingService {

    private final SchedulingRepository repository;

    public SchedulingServiceImpl(SchedulingRepository repository) {
        this.repository = repository;
    }

    @Override
    public SchedulingResponse create(SchedulingRequest request) {
        Scheduling scheduling = new Scheduling(
                request.clientId(),
                request.contractorId(),
                request.serviceId(),
                request.startTime(),
                request.endTime(),
                SchedulingStatus.PENDING
        );
        return toResponse(repository.save(scheduling));
    }

    @Override
    public SchedulingResponse findById(String id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new SchedulingNotFoundException(id));
    }

    @Override
    public List<SchedulingResponse> findByClientId(String clientId) {
        return repository.findByClientId(clientId).stream()
                .map(this::toResponse).toList();
    }

    @Override
    public List<SchedulingResponse> findByContractorId(String contractorId) {
        return repository.findByContractorId(contractorId).stream()
                .map(this::toResponse).toList();
    }

    @Override
    public SchedulingResponse update(String id, SchedulingRequest request) {
        Scheduling scheduling = repository.findById(id)
                .orElseThrow(() -> new SchedulingNotFoundException(id));
        scheduling.setStartTime(request.startTime());
        scheduling.setEndTime(request.endTime());
        scheduling.setUpdatedAt(LocalDateTime.now());
        return toResponse(repository.save(scheduling));
    }

    @Override
    public SchedulingResponse cancel(String id) {
        Scheduling scheduling = repository.findById(id)
                .orElseThrow(() -> new SchedulingNotFoundException(id));
        scheduling.setStatus(SchedulingStatus.CANCELED);
        scheduling.setUpdatedAt(LocalDateTime.now());
        return toResponse(repository.save(scheduling));
    }

    @Override
    public SchedulingResponse complete(String id) {
        Scheduling scheduling = repository.findById(id)
                .orElseThrow(() -> new SchedulingNotFoundException(id));
        scheduling.setStatus(SchedulingStatus.COMPLETED);
        scheduling.setUpdatedAt(LocalDateTime.now());
        return toResponse(repository.save(scheduling));
    }

    private SchedulingResponse toResponse(Scheduling scheduling) {
        return new SchedulingResponse(
                scheduling.getId(),
                scheduling.getClientId(),
                scheduling.getContractorId(),
                scheduling.getServiceId(),
                scheduling.getStartTime(),
                scheduling.getEndTime(),
                scheduling.getStatus(),
                scheduling.getCreatedAt(),
                scheduling.getUpdatedAt()
        );
    }
}