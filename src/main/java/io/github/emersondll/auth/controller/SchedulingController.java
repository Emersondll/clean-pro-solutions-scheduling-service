package io.github.emersondll.auth.controller;

import io.github.emersondll.auth.dto.SchedulingRequest;
import io.github.emersondll.auth.dto.SchedulingResponse;
import io.github.emersondll.auth.service.SchedulingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/schedulings")
public class SchedulingController {

    private final SchedulingService service;

    public SchedulingController(SchedulingService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<SchedulingResponse> create(@RequestBody SchedulingRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SchedulingResponse> findById(@PathVariable String id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<SchedulingResponse>> findByClient(@PathVariable String clientId) {
        return ResponseEntity.ok(service.findByClientId(clientId));
    }

    @GetMapping("/contractor/{contractorId}")
    public ResponseEntity<List<SchedulingResponse>> findByContractor(@PathVariable String contractorId) {
        return ResponseEntity.ok(service.findByContractorId(contractorId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SchedulingResponse> update(@PathVariable String id,
                                                     @RequestBody SchedulingRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<SchedulingResponse> cancel(@PathVariable String id) {
        return ResponseEntity.ok(service.cancel(id));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<SchedulingResponse> complete(@PathVariable String id) {
        return ResponseEntity.ok(service.complete(id));
    }
}