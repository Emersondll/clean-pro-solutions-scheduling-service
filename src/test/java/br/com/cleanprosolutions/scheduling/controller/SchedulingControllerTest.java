package br.com.cleanprosolutions.scheduling.controller;

import br.com.cleanprosolutions.scheduling.dto.SchedulingRequest;
import br.com.cleanprosolutions.scheduling.dto.SchedulingResponse;
import br.com.cleanprosolutions.scheduling.enumerations.RecurrencePattern;
import br.com.cleanprosolutions.scheduling.enumerations.SchedulingStatus;
import br.com.cleanprosolutions.scheduling.service.SchedulingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link SchedulingController}.
 *
 * @author Emerson Lima
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class SchedulingControllerTest {

    @Mock
    private SchedulingService service;

    @InjectMocks
    private SchedulingController controller;

    private SchedulingRequest request;
    private SchedulingResponse response;

    @BeforeEach
    void setUp() {
        final Instant future = Instant.now().plusSeconds(3600);
        request = new SchedulingRequest("client-1", "contractor-1", "svc-1",
                future, future.plusSeconds(3600), RecurrencePattern.NONE);

        response = new SchedulingResponse("sched-1", "client-1", "contractor-1", "svc-1",
                future, future.plusSeconds(3600), SchedulingStatus.PENDING,
                RecurrencePattern.NONE, null, Instant.now(), Instant.now());
    }

    @Test
    @DisplayName("shouldCreateSchedulingAndReturn201")
    void shouldCreateSchedulingAndReturn201() {
        when(service.create(any(SchedulingRequest.class))).thenReturn(response);

        final ResponseEntity<SchedulingResponse> result = controller.create(request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    @DisplayName("shouldFindSchedulingById")
    void shouldFindSchedulingById() {
        when(service.findById("sched-1")).thenReturn(response);

        final ResponseEntity<SchedulingResponse> result = controller.findById("sched-1");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    @DisplayName("shouldFindSchedulingsByClient")
    void shouldFindSchedulingsByClient() {
        when(service.findByClientId("client-1")).thenReturn(List.of(response));

        final ResponseEntity<List<SchedulingResponse>> result = controller.findByClient("client-1");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).hasSize(1);
    }

    @Test
    @DisplayName("shouldFindSchedulingsByContractor")
    void shouldFindSchedulingsByContractor() {
        when(service.findByContractorId("contractor-1")).thenReturn(List.of(response));

        final ResponseEntity<List<SchedulingResponse>> result = controller.findByContractor("contractor-1");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).hasSize(1);
    }

    @Test
    @DisplayName("shouldUpdateScheduling")
    void shouldUpdateScheduling() {
        when(service.update(eq("sched-1"), any(SchedulingRequest.class))).thenReturn(response);

        final ResponseEntity<SchedulingResponse> result = controller.update("sched-1", request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("shouldCreateRecurringSchedulings")
    void shouldCreateRecurringSchedulings() {
        when(service.createRecurring(any(SchedulingRequest.class), anyInt()))
                .thenReturn(List.of(response, response));

        final ResponseEntity<List<SchedulingResponse>> result = controller.createRecurring(request, 2);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).hasSize(2);
    }

    @Test
    @DisplayName("shouldCancelScheduling")
    void shouldCancelScheduling() {
        when(service.cancel("sched-1")).thenReturn(response);

        final ResponseEntity<SchedulingResponse> result = controller.cancel("sched-1");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("shouldCompleteScheduling")
    void shouldCompleteScheduling() {
        when(service.complete("sched-1")).thenReturn(response);

        final ResponseEntity<SchedulingResponse> result = controller.complete("sched-1");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
