package br.com.cleanprosolutions.scheduling.controller;

import br.com.cleanprosolutions.scheduling.dto.SchedulingRequest;
import br.com.cleanprosolutions.scheduling.dto.SchedulingResponse;
import br.com.cleanprosolutions.scheduling.service.SchedulingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for scheduling management.
 *
 * @author Clean Pro Solutions Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/schedulings")
@RequiredArgsConstructor
@Tag(name = "Schedulings", description = "Scheduling management endpoints")
public class SchedulingController {

    private final SchedulingService service;

    @PostMapping
    @Operation(summary = "Create a new scheduling")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Scheduling created"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    public ResponseEntity<SchedulingResponse> create(@Valid @RequestBody final SchedulingRequest request) {
        log.info("POST /schedulings");
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a scheduling by ID")
    public ResponseEntity<SchedulingResponse> findById(@PathVariable final String id) {
        log.info("GET /schedulings/{}", id);
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/client/{clientId}")
    @Operation(summary = "Get all schedulings for a client")
    public ResponseEntity<List<SchedulingResponse>> findByClient(@PathVariable final String clientId) {
        log.info("GET /schedulings/client/{}", clientId);
        return ResponseEntity.ok(service.findByClientId(clientId));
    }

    @GetMapping("/contractor/{contractorId}")
    @Operation(summary = "Get all schedulings for a contractor")
    public ResponseEntity<List<SchedulingResponse>> findByContractor(@PathVariable final String contractorId) {
        log.info("GET /schedulings/contractor/{}", contractorId);
        return ResponseEntity.ok(service.findByContractorId(contractorId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a scheduling time")
    public ResponseEntity<SchedulingResponse> update(
            @PathVariable final String id,
            @Valid @RequestBody final SchedulingRequest request) {
        log.info("PUT /schedulings/{}", id);
        return ResponseEntity.ok(service.update(id, request));
    }

    @PostMapping("/recurring")
    @Operation(summary = "Create a recurring series of schedulings",
            description = "Generates `occurrences` schedulings offset by the recurrence pattern. Pattern must not be NONE.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Recurring series created"),
            @ApiResponse(responseCode = "400", description = "Validation error or NONE pattern")
    })
    public ResponseEntity<List<SchedulingResponse>> createRecurring(
            @Valid @RequestBody final SchedulingRequest request,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "4") final int occurrences) {
        log.info("POST /schedulings/recurring — pattern: {}, occurrences: {}", request.recurrencePattern(), occurrences);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createRecurring(request, occurrences));
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel a scheduling")
    public ResponseEntity<SchedulingResponse> cancel(@PathVariable final String id) {
        log.info("PATCH /schedulings/{}/cancel", id);
        return ResponseEntity.ok(service.cancel(id));
    }

    @PatchMapping("/{id}/complete")
    @Operation(summary = "Mark a scheduling as completed")
    public ResponseEntity<SchedulingResponse> complete(@PathVariable final String id) {
        log.info("PATCH /schedulings/{}/complete", id);
        return ResponseEntity.ok(service.complete(id));
    }
}
