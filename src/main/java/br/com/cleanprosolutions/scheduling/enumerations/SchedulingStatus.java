package br.com.cleanprosolutions.scheduling.enumerations;

/**
 * Status of a service scheduling.
 *
 * @author Emerson Lima
 * @since 1.0.0
 */
public enum SchedulingStatus {
    /** Scheduling created, waiting for confirmation/availability block. */
    PENDING,

    /** Scheduling confirmed by the system/availability service. */
    CONFIRMED,

    /** Scheduling canceled by the client or contractor. */
    CANCELED,

    /** Service has been completed. */
    COMPLETED
}
