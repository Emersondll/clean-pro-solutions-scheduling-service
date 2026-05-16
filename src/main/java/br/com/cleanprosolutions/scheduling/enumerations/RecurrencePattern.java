package br.com.cleanprosolutions.scheduling.enumerations;

/**
 * Defines how a recurring scheduling repeats over time.
 *
 * <p>{@code NONE} is the default for one-off appointments.
 * The remaining values drive the offset calculation in
 * {@code SchedulingServiceImpl.createRecurring()}.</p>
 *
 * @author Clean Pro Solutions Team
 * @since 1.0.0
 */
public enum RecurrencePattern {

    /** Single occurrence — no recurrence. */
    NONE,

    /** Repeats every day. */
    DAILY,

    /** Repeats every 7 days. */
    WEEKLY,

    /** Repeats every 14 days. */
    BIWEEKLY,

    /** Repeats every 30 days. */
    MONTHLY
}
