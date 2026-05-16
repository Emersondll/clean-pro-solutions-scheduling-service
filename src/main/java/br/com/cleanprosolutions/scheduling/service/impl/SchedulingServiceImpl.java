package br.com.cleanprosolutions.scheduling.service.impl;

import br.com.cleanprosolutions.scheduling.document.Scheduling;
import br.com.cleanprosolutions.scheduling.dto.SchedulingRequest;
import br.com.cleanprosolutions.scheduling.dto.SchedulingResponse;
import br.com.cleanprosolutions.scheduling.enumerations.RecurrencePattern;
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
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of {@link SchedulingService}.
 *
 * <p>Manages the scheduling lifecycle. All state transitions produce new immutable
 * {@link Scheduling} instances via the Builder pattern.</p>
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
        log.info("Creating scheduling — clientId: {}, contractorId: {}",
                request.clientId(), request.contractorId());

        final Instant now = Instant.now();
        final RecurrencePattern pattern = request.recurrencePattern() != null
                ? request.recurrencePattern()
                : RecurrencePattern.NONE;

        final Scheduling scheduling = Scheduling.builder()
                .id(UUID.randomUUID().toString())
                .clientId(request.clientId())
                .contractorId(request.contractorId())
                .serviceId(request.serviceId())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .status(SchedulingStatus.PENDING)
                .recurrencePattern(pattern)
                .createdAt(now)
                .updatedAt(now)
                .build();

        final Scheduling saved = repository.save(scheduling);
        log.info("Scheduling created — id: {}", saved.getId());

        publishScheduleCreatedEvent(saved);
        return toResponse(saved);
    }

    @Override
    public SchedulingResponse findById(final String id) {
        log.info("Finding scheduling — id: {}", id);
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new SchedulingNotFoundException(id));
    }

    @Override
    public List<SchedulingResponse> findByClientId(final String clientId) {
        log.info("Finding schedulings for clientId: {}", clientId);
        return repository.findByClientId(clientId).stream().map(this::toResponse).toList();
    }

    @Override
    public List<SchedulingResponse> findByContractorId(final String contractorId) {
        log.info("Finding schedulings for contractorId: {}", contractorId);
        return repository.findByContractorId(contractorId).stream().map(this::toResponse).toList();
    }

    @Override
    public SchedulingResponse update(final String id, final SchedulingRequest request) {
        log.info("Updating scheduling — id: {}", id);
        final Scheduling existing = repository.findById(id)
                .orElseThrow(() -> new SchedulingNotFoundException(id));

        final Scheduling updated = existing.toBuilder()
                .startTime(request.startTime())
                .endTime(request.endTime())
                .updatedAt(Instant.now())
                .build();

        final Scheduling saved = repository.save(updated);
        log.info("Scheduling {} updated", id);
        return toResponse(saved);
    }

    @Override
    public SchedulingResponse cancel(final String id) {
        log.info("Canceling scheduling — id: {}", id);
        return transitionStatus(id, SchedulingStatus.CANCELED);
    }

    @Override
    public SchedulingResponse complete(final String id) {
        log.info("Completing scheduling — id: {}", id);
        return transitionStatus(id, SchedulingStatus.COMPLETED);
    }

    @Override
    public List<SchedulingResponse> createRecurring(final SchedulingRequest request, final int occurrences) {
        log.info("Creating recurring scheduling — pattern: {}, occurrences: {}",
                request.recurrencePattern(), occurrences);

        if (request.recurrencePattern() == null || request.recurrencePattern() == RecurrencePattern.NONE) {
            throw new IllegalArgumentException("Recurrence pattern must not be NONE for recurring scheduling");
        }

        final SchedulingResponse parent = create(request);
        final List<SchedulingResponse> results = new ArrayList<>();
        results.add(parent);

        final long dayOffset = offsetDays(request.recurrencePattern());

        for (int i = 1; i < occurrences; i++) {
            final Instant now = Instant.now();
            final Scheduling child = Scheduling.builder()
                    .id(UUID.randomUUID().toString())
                    .clientId(request.clientId())
                    .contractorId(request.contractorId())
                    .serviceId(request.serviceId())
                    .startTime(request.startTime().plus(dayOffset * i, ChronoUnit.DAYS))
                    .endTime(request.endTime().plus(dayOffset * i, ChronoUnit.DAYS))
                    .status(SchedulingStatus.PENDING)
                    .recurrencePattern(request.recurrencePattern())
                    .parentSchedulingId(parent.id())
                    .createdAt(now)
                    .updatedAt(now)
                    .build();

            results.add(toResponse(repository.save(child)));
        }

        log.info("Created {} recurring schedulings — parentId: {}", results.size(), parent.id());
        return List.copyOf(results);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Private helpers
    // ─────────────────────────────────────────────────────────────────────────

    private SchedulingResponse transitionStatus(final String id, final SchedulingStatus newStatus) {
        final Scheduling existing = repository.findById(id)
                .orElseThrow(() -> new SchedulingNotFoundException(id));

        final Scheduling updated = existing.toBuilder()
                .status(newStatus)
                .updatedAt(Instant.now())
                .build();

        final Scheduling saved = repository.save(updated);
        log.info("Scheduling {} transitioned to {}", id, newStatus);
        return toResponse(saved);
    }

    private long offsetDays(final RecurrencePattern pattern) {
        return switch (pattern) {
            case DAILY    -> 1L;
            case WEEKLY   -> 7L;
            case BIWEEKLY -> 14L;
            case MONTHLY  -> 30L;
            case NONE     -> 0L;
        };
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
        log.info("Publishing ScheduleCreatedEvent — schedulingId: {}", scheduling.getId());
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
                scheduling.getRecurrencePattern(),
                scheduling.getParentSchedulingId(),
                scheduling.getCreatedAt(),
                scheduling.getUpdatedAt()
        );
    }
}
