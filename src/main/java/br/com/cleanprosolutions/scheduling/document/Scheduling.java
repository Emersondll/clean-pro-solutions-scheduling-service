package br.com.cleanprosolutions.scheduling.document;

import br.com.cleanprosolutions.scheduling.enumerations.SchedulingStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * MongoDB document representing a service schedule.
 *
 * <p>Includes optimistic locking via the {@code @Version} annotation
 * to handle concurrent scheduling attempts safely.</p>
 *
 * @author Clean Pro Solutions Team
 * @since 1.0.0
 */
@Document(collection = "schedulings")
public class Scheduling {

    @Id
    private String id;

    private String clientId;
    private String contractorId;
    private String serviceId;

    private Instant startTime;
    private Instant endTime;

    private SchedulingStatus status;

    /**
     * Optimistic locking version.
     * Prevents concurrent updates from overwriting each other.
     */
    @Version
    private Long version;

    private Instant createdAt;
    private Instant updatedAt;

    public Scheduling() {
    }

    public Scheduling(final String id, final String clientId, final String contractorId,
                      final String serviceId, final Instant startTime, final Instant endTime,
                      final SchedulingStatus status) {
        this.id = id;
        this.clientId = clientId;
        this.contractorId = contractorId;
        this.serviceId = serviceId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    public String getId() { return id; }
    public void setId(final String id) { this.id = id; }

    public String getClientId() { return clientId; }
    public void setClientId(final String clientId) { this.clientId = clientId; }

    public String getContractorId() { return contractorId; }
    public void setContractorId(final String contractorId) { this.contractorId = contractorId; }

    public String getServiceId() { return serviceId; }
    public void setServiceId(final String serviceId) { this.serviceId = serviceId; }

    public Instant getStartTime() { return startTime; }
    public void setStartTime(final Instant startTime) { this.startTime = startTime; }

    public Instant getEndTime() { return endTime; }
    public void setEndTime(final Instant endTime) { this.endTime = endTime; }

    public SchedulingStatus getStatus() { return status; }
    public void setStatus(final SchedulingStatus status) { this.status = status; }

    public Long getVersion() { return version; }
    public void setVersion(final Long version) { this.version = version; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(final Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(final Instant updatedAt) { this.updatedAt = updatedAt; }
}
