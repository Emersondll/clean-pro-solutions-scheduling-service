package io.github.emersondll.auth.document;

import io.github.emersondll.auth.enumerations.SchedulingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "schedulings")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Scheduling {
    @Id
    private UUID id;
    private String clientId;       // Referência ao cliente (user-service)
    private String contractorId;   // Referência ao colaborador (user-service)
    private String serviceId;      // Referência ao serviço (catalog-service)
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private SchedulingStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Scheduling(String clientId, String contractorId, String serviceId,
                      LocalDateTime startTime, LocalDateTime endTime, SchedulingStatus status) {
        this.id = UUID.randomUUID();
        this.clientId = clientId;
        this.contractorId = contractorId;
        this.serviceId = serviceId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
