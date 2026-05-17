package br.com.cleanprosolutions.scheduling.service.impl;

import br.com.cleanprosolutions.scheduling.document.Scheduling;
import br.com.cleanprosolutions.scheduling.dto.SchedulingRequest;
import br.com.cleanprosolutions.scheduling.dto.SchedulingResponse;
import br.com.cleanprosolutions.scheduling.enumerations.RecurrencePattern;
import br.com.cleanprosolutions.scheduling.enumerations.SchedulingStatus;
import br.com.cleanprosolutions.scheduling.event.ScheduleCreatedEvent;
import br.com.cleanprosolutions.scheduling.exception.SchedulingNotFoundException;
import br.com.cleanprosolutions.scheduling.repository.SchedulingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
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
 * @author Emerson Lima
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

        final Instant start = Instant.now().plus(1, ChronoUnit.DAYS);
        final Instant end = start.plus(2, ChronoUnit.HOURS);
        final Instant now = Instant.now();

        request = new SchedulingRequest("client-1", "contractor-1", "service-1", start, end, RecurrencePattern.NONE);

        scheduling = Scheduling.builder()
                .id("sched-1")
                .clientId("client-1")
                .contractorId("contractor-1")
                .serviceId("service-1")
                .startTime(start)
                .endTime(end)
                .status(SchedulingStatus.PENDING)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    @Test
    @DisplayName("shouldCreateSchedulingAndPublishEvent")
    void shouldCreateSchedulingAndPublishEvent() {
        when(repository.save(any(Scheduling.class))).thenReturn(scheduling);

        final SchedulingResponse result = service.create(request);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo("sched-1");
        assertThat(result.status()).isEqualTo(SchedulingStatus.PENDING);
        assertThat(result.clientId()).isEqualTo("client-1");

        verify(repository).save(any(Scheduling.class));
        verify(rabbitTemplate).convertAndSend(
                eq("scheduling.exchange"),
                eq("schedule.created"),
                any(ScheduleCreatedEvent.class)
        );
    }

    @Test
    @DisplayName("shouldFindSchedulingById")
    void shouldFindSchedulingById() {
        when(repository.findById("sched-1")).thenReturn(Optional.of(scheduling));

        final SchedulingResponse result = service.findById("sched-1");

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo("sched-1");
    }

    @Test
    @DisplayName("shouldFindSchedulingsByClientId")
    void shouldFindSchedulingsByClientId() {
        when(repository.findByClientId("client-1")).thenReturn(List.of(scheduling));

        final List<SchedulingResponse> results = service.findByClientId("client-1");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).clientId()).isEqualTo("client-1");
    }

    @Test
    @DisplayName("shouldFindSchedulingsByContractorId")
    void shouldFindSchedulingsByContractorId() {
        when(repository.findByContractorId("contractor-1")).thenReturn(List.of(scheduling));

        final List<SchedulingResponse> results = service.findByContractorId("contractor-1");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).contractorId()).isEqualTo("contractor-1");
    }

    @Test
    @DisplayName("shouldUpdateSchedulingTimestamps")
    void shouldUpdateSchedulingTimestamps() {
        final Instant newStart = Instant.now().plus(2, ChronoUnit.DAYS);
        final Instant newEnd = newStart.plus(3, ChronoUnit.HOURS);
        final SchedulingRequest updateRequest = new SchedulingRequest("client-1", "contractor-1", "service-1", newStart, newEnd, RecurrencePattern.NONE);

        final Scheduling updated = scheduling.toBuilder().startTime(newStart).endTime(newEnd).build();

        when(repository.findById("sched-1")).thenReturn(Optional.of(scheduling));
        when(repository.save(any(Scheduling.class))).thenReturn(updated);

        final SchedulingResponse result = service.update("sched-1", updateRequest);

        assertThat(result).isNotNull();
        final ArgumentCaptor<Scheduling> captor = ArgumentCaptor.forClass(Scheduling.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getStartTime()).isEqualTo(newStart);
    }

    @Test
    @DisplayName("shouldCancelSchedulingAndTransitionStatus")
    void shouldCancelSchedulingAndTransitionStatus() {
        final Scheduling canceled = scheduling.toBuilder().status(SchedulingStatus.CANCELED).build();

        when(repository.findById("sched-1")).thenReturn(Optional.of(scheduling));
        when(repository.save(any(Scheduling.class))).thenReturn(canceled);

        final SchedulingResponse result = service.cancel("sched-1");

        assertThat(result.status()).isEqualTo(SchedulingStatus.CANCELED);
        final ArgumentCaptor<Scheduling> captor = ArgumentCaptor.forClass(Scheduling.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(SchedulingStatus.CANCELED);
    }

    @Test
    @DisplayName("shouldCompleteSchedulingAndTransitionStatus")
    void shouldCompleteSchedulingAndTransitionStatus() {
        final Scheduling completed = scheduling.toBuilder().status(SchedulingStatus.COMPLETED).build();

        when(repository.findById("sched-1")).thenReturn(Optional.of(scheduling));
        when(repository.save(any(Scheduling.class))).thenReturn(completed);

        final SchedulingResponse result = service.complete("sched-1");

        assertThat(result.status()).isEqualTo(SchedulingStatus.COMPLETED);
    }

    @Test
    @DisplayName("shouldThrowExceptionWhenSchedulingNotFound")
    void shouldThrowExceptionWhenSchedulingNotFound() {
        when(repository.findById("non-existent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById("non-existent"))
                .isInstanceOf(SchedulingNotFoundException.class);
    }

    @Test
    @DisplayName("shouldThrowExceptionWhenCancelingNonExistentScheduling")
    void shouldThrowExceptionWhenCancelingNonExistentScheduling() {
        when(repository.findById("non-existent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.cancel("non-existent"))
                .isInstanceOf(SchedulingNotFoundException.class);
    }

    @Test
    @DisplayName("shouldCreateRecurringSchedulingsWithWeeklyPattern")
    void shouldCreateRecurringSchedulingsWithWeeklyPattern() {
        final Instant start = Instant.now().plus(1, ChronoUnit.DAYS);
        final Instant end = start.plus(2, ChronoUnit.HOURS);
        final SchedulingRequest recurringReq = new SchedulingRequest(
                "client-1", "contractor-1", "service-1", start, end, RecurrencePattern.WEEKLY);

        when(repository.save(any(Scheduling.class))).thenReturn(scheduling);

        final List<SchedulingResponse> results = service.createRecurring(recurringReq, 3);

        assertThat(results).hasSize(3);
    }

    @Test
    @DisplayName("shouldThrowWhenCreateRecurringWithNonePattern")
    void shouldThrowWhenCreateRecurringWithNonePattern() {
        final SchedulingRequest noneReq = new SchedulingRequest(
                "client-1", "contractor-1", "service-1",
                Instant.now().plus(1, ChronoUnit.DAYS),
                Instant.now().plus(2, ChronoUnit.DAYS),
                RecurrencePattern.NONE);

        assertThatThrownBy(() -> service.createRecurring(noneReq, 3))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("shouldCreateRecurringWithDailyPattern")
    void shouldCreateRecurringWithDailyPattern() {
        final Instant start = Instant.now().plus(1, ChronoUnit.DAYS);
        final SchedulingRequest req = new SchedulingRequest(
                "client-1", "contractor-1", "service-1", start, start.plus(1, ChronoUnit.HOURS), RecurrencePattern.DAILY);
        when(repository.save(any(Scheduling.class))).thenReturn(scheduling);

        assertThat(service.createRecurring(req, 2)).hasSize(2);
    }

    @Test
    @DisplayName("shouldCreateRecurringWithMonthlyPattern")
    void shouldCreateRecurringWithMonthlyPattern() {
        final Instant start = Instant.now().plus(1, ChronoUnit.DAYS);
        final SchedulingRequest req = new SchedulingRequest(
                "client-1", "contractor-1", "service-1", start, start.plus(1, ChronoUnit.HOURS), RecurrencePattern.MONTHLY);
        when(repository.save(any(Scheduling.class))).thenReturn(scheduling);

        assertThat(service.createRecurring(req, 2)).hasSize(2);
    }
}
