package br.com.cleanprosolutions.scheduling.document;

import br.com.cleanprosolutions.scheduling.enumerations.RecurrencePattern;
import br.com.cleanprosolutions.scheduling.enumerations.SchedulingStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * MongoDB document representing a service schedule.
 *
 * <p>Uses optimistic locking via {@code @Version} to prevent concurrent
 * scheduling conflicts. Follows the Builder pattern — use {@link #builder()}
 * or {@link #toBuilder()} to create or transition instances.</p>
 *
 * @author Emerson Lima
 * @since 1.0.0
 */
@Document(collection = "schedulings")
public final class Scheduling {

    @Id
    private final String id;
    private final String clientId;
    private final String contractorId;
    private final String serviceId;
    private final Instant startTime;
    private final Instant endTime;
    private final SchedulingStatus status;

    @Version
    private final Long version;

    private final Instant createdAt;
    private final Instant updatedAt;
    private final RecurrencePattern recurrencePattern;
    private final String parentSchedulingId;

    private Scheduling(final Builder builder) {
        this.id                  = builder.id;
        this.clientId            = builder.clientId;
        this.contractorId        = builder.contractorId;
        this.serviceId           = builder.serviceId;
        this.startTime           = builder.startTime;
        this.endTime             = builder.endTime;
        this.status              = builder.status;
        this.version             = builder.version;
        this.createdAt           = builder.createdAt;
        this.updatedAt           = builder.updatedAt;
        this.recurrencePattern   = builder.recurrencePattern;
        this.parentSchedulingId  = builder.parentSchedulingId;
    }

    /** Required by Spring Data MongoDB for deserialization. */
    @SuppressWarnings("unused")
    private Scheduling() {
        this.id                  = null;
        this.clientId            = null;
        this.contractorId        = null;
        this.serviceId           = null;
        this.startTime           = null;
        this.endTime             = null;
        this.status              = null;
        this.version             = null;
        this.createdAt           = null;
        this.updatedAt           = null;
        this.recurrencePattern   = null;
        this.parentSchedulingId  = null;
    }

    public String getId()                          { return id; }
    public String getClientId()                    { return clientId; }
    public String getContractorId()                { return contractorId; }
    public String getServiceId()                   { return serviceId; }
    public Instant getStartTime()                  { return startTime; }
    public Instant getEndTime()                    { return endTime; }
    public SchedulingStatus getStatus()            { return status; }
    public Long getVersion()                       { return version; }
    public Instant getCreatedAt()                  { return createdAt; }
    public Instant getUpdatedAt()                  { return updatedAt; }
    public RecurrencePattern getRecurrencePattern(){ return recurrencePattern; }
    public String getParentSchedulingId()          { return parentSchedulingId; }

    /**
     * Returns a builder pre-populated with this instance's values.
     * Use for state transitions (e.g., cancel, complete).
     */
    public Builder toBuilder() {
        return new Builder()
                .id(id)
                .clientId(clientId)
                .contractorId(contractorId)
                .serviceId(serviceId)
                .startTime(startTime)
                .endTime(endTime)
                .status(status)
                .version(version)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .recurrencePattern(recurrencePattern)
                .parentSchedulingId(parentSchedulingId);
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for {@link Scheduling}.
     */
    public static final class Builder {
        private String id;
        private String clientId;
        private String contractorId;
        private String serviceId;
        private Instant startTime;
        private Instant endTime;
        private SchedulingStatus status;
        private Long version;
        private Instant createdAt;
        private Instant updatedAt;
        private RecurrencePattern recurrencePattern = RecurrencePattern.NONE;
        private String parentSchedulingId;

        private Builder() {}

        public Builder id(final String id)                                    { this.id = id; return this; }
        public Builder clientId(final String clientId)                        { this.clientId = clientId; return this; }
        public Builder contractorId(final String contractorId)                { this.contractorId = contractorId; return this; }
        public Builder serviceId(final String serviceId)                      { this.serviceId = serviceId; return this; }
        public Builder startTime(final Instant startTime)                     { this.startTime = startTime; return this; }
        public Builder endTime(final Instant endTime)                         { this.endTime = endTime; return this; }
        public Builder status(final SchedulingStatus status)                  { this.status = status; return this; }
        public Builder version(final Long version)                            { this.version = version; return this; }
        public Builder createdAt(final Instant createdAt)                     { this.createdAt = createdAt; return this; }
        public Builder updatedAt(final Instant updatedAt)                     { this.updatedAt = updatedAt; return this; }
        public Builder recurrencePattern(final RecurrencePattern rp)          { this.recurrencePattern = rp; return this; }
        public Builder parentSchedulingId(final String parentSchedulingId)    { this.parentSchedulingId = parentSchedulingId; return this; }

        public Scheduling build() {
            return new Scheduling(this);
        }
    }
}
