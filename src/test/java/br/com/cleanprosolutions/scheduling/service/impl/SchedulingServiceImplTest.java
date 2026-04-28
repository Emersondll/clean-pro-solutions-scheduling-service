package br.com.cleanprosolutions.scheduling.service.impl;

import br.com.cleanprosolutions.scheduling.document.Scheduling;
import br.com.cleanprosolutions.scheduling.dto.SchedulingRequest;
import br.com.cleanprosolutions.scheduling.dto.SchedulingResponse;
import br.com.cleanprosolutions.scheduling.enumerations.SchedulingStatus;
import br.com.cleanprosolutions.scheduling.event.ScheduleCreatedEvent;
import br.com.cleanprosolutions.scheduling.exception.SchedulingNotFoundException;
import br.com.cleanprosolutions.scheduling.repository.SchedulingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link SchedulingServiceImpl}.
 *
 * @author Clean Pro Solutions Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class SchedulingServiceImplTest {

    @Mock
    private SchedulingRepository repository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private SchedulingServiceImpl service;

    private SchedulingRequest request;
    private Scheduling scheduling;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "schedulingExchange", "scheduling.exchange");
        ReflectionTestUtils.setField(service, "scheduleCreatedRoutingKey", "schedule.created");

        request = new SchedulingRequest(
                "client-1", "contractor-1", "service-1",
                Instant.now().plus(1, ChronoUnit.DAYS),
                Instant.now().plus(1, ChronoUnit.DAYS).plus(2, ChronoUnit.HOURS)
        );

        scheduling = new Scheduling(
                "sched-1", "client-1", "contractor-1", "service-1",
                request.startTime(), request.endTime(), SchedulingStatus.PENDING
        );
        scheduling.setCreatedAt(Instant.now());
        scheduling.setUpdatedAt(Instant.now());
    }

    @Test
    @DisplayName("shouldCreateSchedulingAndPublishEvent")
    void shouldCreateSchedulingAndPublishEvent() {
        when(repository.save(any(Scheduling.class))).thenReturn(scheduling);

        final SchedulingResponse result = service.create(request);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo("sched-1");
        assertThat(result.status()).isEqualTo(SchedulingStatus.PENDING);

        verify(repository).save(any(Scheduling.class));
        verify(rabbitTemplate).convertAndSend(
                eq("scheduling.exchange"),
                eq("schedule.created"),
                any(ScheduleCreatedEvent.class)
        );
    }

    @Test
    @DisplayName("shouldUpdateScheduling")
    void shouldUpdateScheduling() {
        when(repository.findById("sched-1")).thenReturn(Optional.of(scheduling));
        when(repository.save(any(Scheduling.class))).thenReturn(scheduling);

        final SchedulingResponse result = service.update("sched-1", request);

        assertThat(result).isNotNull();
        verify(repository).findById("sched-1");
        verify(repository).save(any(Scheduling.class));
    }

    @Test
    @DisplayName("shouldCancelScheduling")
    void shouldCancelScheduling() {
        when(repository.findById("sched-1")).thenReturn(Optional.of(scheduling));
        when(repository.save(any(Scheduling.class))).thenReturn(scheduling);

        final SchedulingResponse result = service.cancel("sched-1");

        assertThat(result.status()).isEqualTo(SchedulingStatus.CANCELED);
        verify(repository).save(any(Scheduling.class));
    }

    @Test
    @DisplayName("shouldCompleteScheduling")
    void shouldCompleteScheduling() {
        when(repository.findById("sched-1")).thenReturn(Optional.of(scheduling));
        when(repository.save(any(Scheduling.class))).thenReturn(scheduling);

        final SchedulingResponse result = service.complete("sched-1");

        assertThat(result.status()).isEqualTo(SchedulingStatus.COMPLETED);
        verify(repository).save(any(Scheduling.class));
    }

    @Test
    @DisplayName("shouldThrowExceptionWhenSchedulingNotFound")
    void shouldThrowExceptionWhenSchedulingNotFound() {
        when(repository.findById("non-existent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById("non-existent"))
                .isInstanceOf(SchedulingNotFoundException.class);
    }
}
