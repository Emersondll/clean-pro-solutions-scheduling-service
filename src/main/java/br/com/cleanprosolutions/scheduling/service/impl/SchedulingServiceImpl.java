package br.com.cleanprosolutions.scheduling.service.impl;

import br.com.cleanprosolutions.scheduling.document.Scheduling;
import br.com.cleanprosolutions.scheduling.dto.SchedulingRequest;
import br.com.cleanprosolutions.scheduling.dto.SchedulingResponse;
import br.com.cleanprosolutions.scheduling.enumerations.SchedulingStatus;
import br.com.cleanprosolutions.scheduling.event.ScheduleCreatedEvent;
import br.com.cleanprosolutions.scheduling.exception.SchedulingNotFoundException;
import br.com.cleanprosolutions.scheduling.repository.SchedulingRepository;
import br.com.cleanprosolutions.scheduling.service.SchedulingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of {@link SchedulingService}.
 *
 * <p>Handles the creation of schedules and publishes events to RabbitMQ.</p>
 *
 * @author Clean Pro Solutions Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulingServiceImpl implements SchedulingService {

    private final SchedulingRepository repository;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.scheduling:scheduling.exchange}")
    private String schedulingExchange;

    @Value("${rabbitmq.routing-key.schedule-created:schedule.created}")
    private String scheduleCreatedRoutingKey;

    @Override
    public SchedulingResponse create(final SchedulingRequest request) {
        log.info("Creating new scheduling for client: {} and contractor: {}",
                request.clientId(), request.contractorId());

        final Scheduling scheduling = new Scheduling(
                UUID.randomUUID().toString(),
                request.clientId(),
                request.contractorId(),
                request.serviceId(),
                request.startTime(),
                request.endTime(),
                SchedulingStatus.PENDING
        );

        scheduling.setCreatedAt(Instant.now());
        scheduling.setUpdatedAt(Instant.now());

        final Scheduling saved = repository.save(scheduling);
        log.info("Scheduling created with ID: {}", saved.getId());

        publishScheduleCreatedEvent(saved);

        return toResponse(saved);
    }

    @Override
    public SchedulingResponse findById(final String id) {
        log.info("Finding scheduling by ID: {}", id);
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new SchedulingNotFoundException(id));
    }

    @Override
    public List<SchedulingResponse> findByClientId(final String clientId) {
        log.info("Finding schedulings for client ID: {}", clientId);
        return repository.findByClientId(clientId).stream().map(this::toResponse).toList();
    }

    @Override
    public List<SchedulingResponse> findByContractorId(final String contractorId) {
        log.info("Finding schedulings for contractor ID: {}", contractorId);
        return repository.findByContractorId(contractorId).stream().map(this::toResponse).toList();
    }

    @Override
    public SchedulingResponse update(final String id, final SchedulingRequest request) {
        log.info("Updating scheduling ID: {}", id);
        final Scheduling scheduling = repository.findById(id)
                .orElseThrow(() -> new SchedulingNotFoundException(id));

        scheduling.setStartTime(request.startTime());
        scheduling.setEndTime(request.endTime());
        scheduling.setUpdatedAt(Instant.now());

        final Scheduling saved = repository.save(scheduling);
        return toResponse(saved);
    }

    @Override
    public SchedulingResponse cancel(final String id) {
        log.info("Canceling scheduling ID: {}", id);
        final Scheduling scheduling = repository.findById(id)
                .orElseThrow(() -> new SchedulingNotFoundException(id));

        scheduling.setStatus(SchedulingStatus.CANCELED);
        scheduling.setUpdatedAt(Instant.now());

        final Scheduling saved = repository.save(scheduling);
        return toResponse(saved);
    }

    @Override
    public SchedulingResponse complete(final String id) {
        log.info("Completing scheduling ID: {}", id);
        final Scheduling scheduling = repository.findById(id)
                .orElseThrow(() -> new SchedulingNotFoundException(id));

        scheduling.setStatus(SchedulingStatus.COMPLETED);
        scheduling.setUpdatedAt(Instant.now());

        final Scheduling saved = repository.save(scheduling);
        return toResponse(saved);
    }

    private void publishScheduleCreatedEvent(final Scheduling scheduling) {
        final ScheduleCreatedEvent event = new ScheduleCreatedEvent(
                UUID.randomUUID().toString(),
                scheduling.getId(),
                scheduling.getContractorId(),
                scheduling.getStartTime(),
                scheduling.getEndTime(),
                Instant.now()
        );

        log.info("Publishing ScheduleCreatedEvent for scheduling ID: {}", scheduling.getId());
        rabbitTemplate.convertAndSend(schedulingExchange, scheduleCreatedRoutingKey, event);
    }

    private SchedulingResponse toResponse(final Scheduling scheduling) {
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
